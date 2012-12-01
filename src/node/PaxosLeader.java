package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import common.Common;
import common.Common.PaxosLeaderState;
import common.Common.State;
import common.MessageWrapper;
import common.RMQReceiver;
import common.RMQSender;

import message.ClientOpMsg;
import message.PaxosMsg;
import message.TwoPCMsg;


import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.*;

final class TransactionStatus {
	Integer lsn;
	Integer gsn;
	PaxosLeaderState state;
	Set<String> AcceptorList;
}

public class PaxosLeader extends Node{
	
	private static int lsnCounter = 0;
	
	private RMQSender bcastQueueSender;
	private RMQReceiver tpcBcastQueueReceiver;
		
	private BidiMap dataLSNMap;
	
	
	private Map<Integer, TransactionStatus> lsnTransactionStatusMap;
	
	public PaxosLeader(String nodeId, String fileName, String bcastQueueName, String tpcBcastQueueName) throws IOException {		
		super(nodeId, fileName);
		this.bcastQueueSender = new RMQSender(bcastQueueName);
		this.tpcBcastQueueReceiver = new RMQReceiver(tpcBcastQueueName, true);
		
		this.dataLSNMap = new DualHashBidiMap();		
		
		this.lsnTransactionStatusMap = new HashMap<Integer, TransactionStatus>();
	}
	
	
	public void run() throws IOException, InterruptedException{
		
		 while (true) {
			 MessageWrapper msgwrap= tpcBcastQueueReceiver.ReceiveMessage();    
		    
				if(msgwrap.getmessageclass().equals("ClientOpMsg")){
					
				}
				else if(msgwrap.getmessageclass().equals("PaxosMsg"))
				{
				}
				else if(msgwrap.getmessageclass().equals("TwoPCMsg"))
				{
				}
		  }		
	}
	
	
	public void processClientMessageData(ClientOpMsg msg) throws IOException
	{
		if(msg.getType()==Common.ClientOPMsgType.APPEND)
		{
			int lsn=this.getLSNfromMessageData(msg.getData());
			if(lsn!=-1)
			{
				this.processAppendRequest(lsn);
			}
			else {}
			
		}
		else{
			//code to process read request
		}		
	}
	
	public void processAppendRequest(int lsn) throws IOException
	{
		PaxosMsg paxosmsg=new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACCEPT, lsn);
		MessageWrapper msgwrap=new MessageWrapper(Common.Serialize(paxosmsg), "PaxosMsg");
		bcastQueueSender.SendMessage(msgwrap);
	}
	
	
	//Use this method to generate a new LSN value for each new message.
	public int getLSNfromMessageData(String data) throws IOException{
		int lsn;
		if(!this.dataLSNMap.containsKey(data))
		{
			lsn = getNewLSN();
			this.dataLSNMap.put(data, lsn);
			TransactionStatus temp=new TransactionStatus();
			temp.lsn=lsn;
			temp.state=Common.PaxosLeaderState.PREPARE; // is it active
			lsnTransactionStatusMap.put(lsn,temp );
		}
		else
		{
			lsn = (Integer) this.dataLSNMap.get(data);
			//check the status of the lsn. 
			//if it is not in the status table
			//then overwrite lsn value with new lsn.
			//else reply with -1.
			if(lsnTransactionStatusMap.get(lsn)!=null)
			{
				//ignore,bcz some other trans is goin on
				
				return -1;
			}
			else
			{
				//a dup msg and the trans was aborted and hence lsntansmap doesnt contain the entry
				TransactionStatus temp=new TransactionStatus();
				temp.lsn=lsn;
				temp.state = Common.PaxosLeaderState.ACCEPT;
				lsnTransactionStatusMap.put(lsn,temp);
				PaxosMsg msg=new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACCEPT, lsn);
				MessageWrapper msgwrap=new MessageWrapper(Common.Serialize(msg), "PaxosMsg");
				bcastQueueSender.SendMessage(msgwrap);
				
			}
			
		}
		return lsn;
	}
	
	// Update LSN after receiving commit response from 2PC.
	public void processLSNStatusRequest(int lsn, int gsn) throws IOException
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.gsn = gsn;
		this.lsnTransactionStatusMap.put(lsn, temp);
		//propagate info to all acceptors
		PaxosMsg msg=new PaxosMsg(this.nodeId, Common.PaxosMsgType.COMMIT,lsn,gsn);
		MessageWrapper msgwrap=new MessageWrapper(Common.Serialize(msg), "PaxosMsg");
		bcastQueueSender.SendMessage(msgwrap);
	}
	
	// Update Acceptor List on receiving ack response for PREPARE Phase from an Acceptor.
	//how to diff lsn accept from a gsn accept?
	public void processLSNStatusRequest(int lsn, String nodeId)
	{
		if(this.lsnTransactionStatusMap.get(lsn)!=null){
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.AcceptorList.add(nodeId);		
		if (temp.AcceptorList.size() == Common.NoAcceptors) //shouldn't this be majority?
		{
			temp.state = Common.PaxosLeaderState.ACCEPT;
			this.lsnTransactionStatusMap.put(lsn, temp);			
			//send msg to TPC
			this.SendInfoMessageToTPCCoordinator(lsn);
		}
		else{
		this.lsnTransactionStatusMap.put(lsn, temp);
		//nothing to do unless we get a majority
		}
		}
		else
		{
			//ignore, bcz the trans might have been aborted and so no entry exists
		}
	}
	
	public void SendInfoMessageToTPCCoordinator(int lsn)
	{
		String msg = (String) this.dataLSNMap.getKey(lsn);
		//TODO: Send Info Message to TPC. 
		TwoPCMsg tpcmsg=new TwoPCMsg(this.nodeId, Common.TwoPCMsgType.INFO,lsn,msg);
		//how to send msg to TPC coordinator
		
		
	}
	
	
	public void ProcessAbortMessage(int lsn) throws IOException
	{
		/*
		 * 0. Broadcast Message to all Acceptors.
		 * 
		 */
		
		//propagate info to all acceptors
	    PaxosMsg msg=new PaxosMsg(this.nodeId, Common.PaxosMsgType.ABORT, lsn);
		MessageWrapper msgwrap=new MessageWrapper(Common.Serialize(msg), "PaxosMsg");
		bcastQueueSender.SendMessage(msgwrap);
	}
	
	public void ProcessAbortAckMessage(int lsn)
	{
		/*
		 * 1. Delete entry from Data-LSN Map
		 * 2. Delete entry from LSN-Status Map
		 */
		
		if(this.lsnTransactionStatusMap.get(lsn)!=null){
			TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
			temp.AcceptorList.add(this.nodeId);		
			if (temp.AcceptorList.size() == Common.NoAcceptors) //should this be majority 
			{
				this.dataLSNMap.remove(lsn);
				this.lsnTransactionStatusMap.remove(lsn);
			}
			else{
			this.lsnTransactionStatusMap.put(lsn, temp);
			//nothing to do unless we get a majority
			}
			}
			else
			{
				//ignore, bcz the trans might have been aborted and so no entry exists
			}		
		
	}
	
	
	private static int getNewLSN(){
		return lsnCounter++;
	}
}
