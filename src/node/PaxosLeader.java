package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import common.Common;
import common.Tuple;
import common.Common.PaxosLeaderState;
import common.Common.State;
import common.Common.TwoPCMsgType;
import common.MessageWrapper;

import message.ClientOpMsg;
import message.PaxosMsg;
import message.TwoPCMsg;


import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.*;

public class PaxosLeader extends Node{

	final class TransactionStatus {
		Integer lsn;
		Integer gsn;
		PaxosLeaderState state;
		Set<String> acceptorListPrepare;
		Set<String> acceptorListCommit;
		Set<String> acceptorListAbort;

		public TransactionStatus(int lsn)
		{
			this.lsn = lsn;
			this.gsn = -1;
			this.state = PaxosLeaderState.PREPARE;
			this.acceptorListPrepare = new HashSet<String>();	
			this.acceptorListCommit =  new HashSet<String>();
			this.acceptorListAbort =  new HashSet<String>();
		}		
	}

	private static int lsnCounter = 0;

	private static String tpcCoordinatorId;
	private String paxosLeaderExchange;
	private String tpcCoordinatorExchance;

	private BidiMap dataLSNMap;
	private Map<Integer, TransactionStatus> lsnTransactionStatusMap;

	public PaxosLeader(String nodeId, String fileName, String tpcCoordinatorId) throws IOException {		
		super(nodeId, fileName);

		this.tpcCoordinatorId = tpcCoordinatorId;
		this.paxosLeaderExchange = Common.PaxosLeaderExchange + this.nodeId;
		this.tpcCoordinatorExchance = Common.TPCCoordinatorExchange + this.tpcCoordinatorId;

		ArrayList<Tuple<String, String>> exchanges = new ArrayList<Tuple<String, String>>();
		exchanges.add(new Tuple(Common.DirectMessageExchange, Common.directExchangeType));
		exchanges.add(new Tuple(this.paxosLeaderExchange, Common.bcastExchangeType));
		exchanges.add(new Tuple(this.tpcCoordinatorExchance, Common.bcastExchangeType));

		this.DeclareExchanges(exchanges);
		this.InitializeConsumer();

		this.dataLSNMap = new DualHashBidiMap();		
		this.lsnTransactionStatusMap = new HashMap<Integer, TransactionStatus>();
	}


	public void run() throws IOException, InterruptedException, ClassNotFoundException{

		while (true) {
			MessageWrapper msgwrap=  messageController.ReceiveMessage();    

			if(msgwrap.getmessageclass().equals("ClientOpMsg"))
			{
				ClientOpMsg msg = (ClientOpMsg) msgwrap.getDeSerializedInnerMessage();
				ProcessClientMessageData(msg);
			}
			
			else if(msgwrap.getmessageclass().equals("PaxosMsg"))
			{
				PaxosMsg msg = (PaxosMsg) msgwrap.getDeSerializedInnerMessage();
				TransactionStatus temp = lsnTransactionStatusMap.get(msg.getLsn());
				
				if (temp.state == PaxosLeaderState.PREPARE)
					ProcessPrepareAck(msg.getLsn(), msg.getNodeid());
				
				else if (temp.state == PaxosLeaderState.COMMIT)
					ProcessCommitAck(msg.getLsn(), msg.getNodeid());
				
				else if (temp.state == PaxosLeaderState.ABORT)
					ProcessAbortAck(msg.getLsn(), msg.getNodeid());
			}
			else if(msgwrap.getmessageclass().equals("TwoPCMsg"))
			{
				TwoPCMsg msg = (TwoPCMsg) msgwrap.getDeSerializedInnerMessage();
				if (msg.getType() == TwoPCMsgType.COMMIT)
					ProcessCommitRequest(msg.getLsn(), msg.getGsn());
				else 
					ProcessAbortRequest(msg.getLsn());
			}
		}		
	}

	//method used to process client msg
	public void ProcessClientMessageData(ClientOpMsg msg) throws IOException
	{
		if(msg.getType()==Common.ClientOPMsgType.APPEND)
		{
			int lsn=this.GetLSNfromMessageData(msg.getData());
			if(lsn != -1)
			{
				this.ProcessAppendRequest(lsn, msg.getData());				
			}
			else
			{
				//TODO: RESPOND WITH Duplicate type message.
			}

		}
		else{
			//TODO: Respond with read of file.
		}		
	}

	//Process New Append Request from Client
	public void ProcessAppendRequest(int lsn, String data) throws IOException 
	{
		TransactionStatus temp=new TransactionStatus(lsn);
		this.dataLSNMap.put(data, lsn); 
		this.lsnTransactionStatusMap.put(lsn,temp );
		
		PaxosMsg paxosMsg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACCEPT, lsn);
		SendPaxosMsg(paxosMsg);
	}

	//Process New Read Request from Client
	public String ProcessReadRequest()
	{
		//TODO : Implement file line number logic.
		return new String();
	}
	
	//Broadcast append request to all acceptors.
	public void SendPaxosMsg(PaxosMsg msg) throws IOException
	{		
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(msg), msg.getClass());
		this.messageController.SendMessage(msgwrap, this.paxosLeaderExchange, "");
	}


	// Update LSN after receiving commit response from 2PC.
	public void ProcessCommitRequest(int lsn, int gsn) throws IOException
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.gsn = gsn;
		temp.state = Common.PaxosLeaderState.COMMIT;
		this.lsnTransactionStatusMap.put(lsn, temp);

		//propagate info to all acceptors
		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.COMMIT,lsn,gsn);
		SendPaxosMsg(msg);
	}

	//method used to process abort msg from the two phase coordinator
		public void ProcessAbortRequest(int lsn) throws IOException
		{
			/*
			 * 0. Broadcast Message to all Acceptors.
			 * 1. Change State to Abort.
			 */

			//propagate info to all acceptors
			TransactionStatus temp = lsnTransactionStatusMap.get(lsn);
			temp.state = Common.PaxosLeaderState.ABORT;
			lsnTransactionStatusMap.put(lsn, temp);
			
			PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ABORT, lsn);		
			SendPaxosMsg(msg);
		}

		
	//Process Ack from Acceptor
	public void ProcessPrepareAck(int lsn, String nodeId) throws IOException
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.acceptorListPrepare.add(nodeId);
		if (temp.acceptorListPrepare.size() >= Common.GetQuorumSize() && temp.state == PaxosLeaderState.PREPARE) 
		{
			temp.state = Common.PaxosLeaderState.ACCEPT;
			TwoPCMsg msg = new TwoPCMsg(this.nodeId, TwoPCMsgType.INFO, -1);
			this.SendTPCMsg(msg);
		}
		else
		{

		}
		this.lsnTransactionStatusMap.put(lsn, temp);
	}

	public void ProcessCommitAck(int lsn, String nodeId) throws IOException
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.acceptorListCommit.add(nodeId);
		if (temp.acceptorListCommit.size() >= Common.GetQuorumSize() && temp.state == PaxosLeaderState.COMMIT) 
		{
			temp.state = Common.PaxosLeaderState.COMMIT_ACK;
			TwoPCMsg msg = new TwoPCMsg(this.nodeId, TwoPCMsgType.COMMIT, temp.gsn);
			this.SendTPCMsg(msg);
		}
		else
		{

		}
		this.lsnTransactionStatusMap.put(lsn, temp);
	}
	
	public void ProcessAbortAck(int lsn, String nodeId) throws IOException
	{
		
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.acceptorListAbort.add(nodeId);
		if (temp.acceptorListAbort.size() >= Common.GetQuorumSize() && temp.state == PaxosLeaderState.ABORT) 
		{			
			temp.state = Common.PaxosLeaderState.ABORT_ACK;
			TwoPCMsg msg = new TwoPCMsg(this.nodeId, TwoPCMsgType.ABORT, temp.gsn);
			this.SendTPCMsg(msg);
			
		}
		else 
		{
			
		}
		this.lsnTransactionStatusMap.put(lsn, temp);
		
		if (temp.acceptorListAbort.size() == Common.NoAcceptors && temp.state == PaxosLeaderState.ABORT_ACK)			
		{	
			//TODO: Add GUID for transactions. Delete data entry from LSN Map.
			//this.dataLSNMap.remove(key)
			this.lsnTransactionStatusMap.remove(lsn);
		}
		
		
	}

	//Send Message to TPC Coord.
	public void SendTPCMsg(TwoPCMsg msg) throws IOException
	{		
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(msg), msg.getClass());
		this.messageController.SendMessage(msgwrap, this.paxosLeaderExchange, "");
	}

	
	
	private static int getNewLSN(){
		return lsnCounter++;
	}
	
	//Use this method to generate a new LSN value for each new message.
		public int GetLSNfromMessageData(String data) throws IOException{
			int lsn;
			if(!this.dataLSNMap.containsKey(data))
				lsn = getNewLSN();			
			else
				lsn = -1;
			return lsn;
		}


}
