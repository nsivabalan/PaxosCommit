package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import message.BcastMsg;
import message.PaxosMsg;
import message.SiteCrashMsg;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import common.Common;
import common.Common.AcceptorState;
import common.Common.BcastMsgType;
import common.Common.PaxosMsgType;
import common.Common.SiteCrashMsgType;
import common.Common.State;
import common.MessageWrapper;
import common.RMQReceiver;
import common.RMQSender_Old;
import common.Tuple;

public class Acceptor extends Node {

	final class TransactionStatus {
		Integer gsn;
		String data;
		Common.AcceptorState state;
		Set<String> Acceptors;		
	}

	private String paxosLeaderId;
	private String paxosLeaderExchange;
	private Map<UUID, TransactionStatus> uidTransactionStatusMap;
	private static int lastCommitGSN = 0;

	@SuppressWarnings("unchecked")
	public Acceptor(String nodeId, String fileName, String paxosLeaderId) throws IOException
	{
		super(nodeId, fileName);
		this.paxosLeaderId = paxosLeaderId;
		this.paxosLeaderExchange = Common.PaxosLeaderExchange + this.paxosLeaderId;

		ArrayList<Tuple<String, String>> exchanges = new ArrayList<Tuple<String, String>>();
		exchanges.add(new Tuple(Common.DirectMessageExchange, Common.directExchangeType));
		exchanges.add(new Tuple(this.paxosLeaderExchange, Common.bcastExchangeType));
		this.DeclareExchanges(exchanges);
		this.InitializeConsumer();

		this.uidTransactionStatusMap = new HashMap<UUID, TransactionStatus>();		
	}



	public void run() throws IOException, InterruptedException, ClassNotFoundException{

		while (true) {

			MessageWrapper msgwrap= messageController.ReceiveMessage();   
			if (msgwrap != null)
			{


				if (msgwrap.getmessageclass() == BcastMsg.class  && this.NodeState == State.ACTIVE)
				{
					BcastMsg msg = (BcastMsg) msgwrap.getDeSerializedInnerMessage();

					if(msg.getType() == BcastMsgType.COMMIT_ACK)
						ProcessCommitAckMessage(msg.getUID(), msg.getGsn(), msg.getNodeid());

					else if (msg.getType() == BcastMsgType.ABORT_ACK)
						ProcessAbortAckMessage(msg.getUID(), msg.getNodeid());
				}

				else if (msgwrap.getmessageclass() == PaxosMsg.class  && this.NodeState == State.ACTIVE)
				{
					PaxosMsg msg = (PaxosMsg) msgwrap.getDeSerializedInnerMessage();

					if(msg.getType() == PaxosMsgType.ACCEPT)
						ProcessAcceptMessage(msg.getUID(), msg.getData());

					else if (msg.getType() == PaxosMsgType.COMMIT)
						ProcessCommitMessage(msg.getUID(), msg.getGsn());

					else if (msg.getType() == PaxosMsgType.ABORT)
						ProcessAbortMessage(msg.getUID());
				}
				else if (msgwrap.getmessageclass() == SiteCrashMsg.class)
				{
					SiteCrashMsg msg = (SiteCrashMsg) msgwrap.getDeSerializedInnerMessage();
					if(msg.getType() == SiteCrashMsgType.CRASH && this.NodeState == State.ACTIVE)
					{
						this.NodeState = State.PAUSED;
					}
					else if(msg.getType() == SiteCrashMsgType.RECOVER && this.NodeState == State.PAUSED)
					{
						this.NodeState = State.ACTIVE;
					}					
				}
				else
				{
					//Message Discarded.
				}
			}
			//TODO : logic for broadcast and timeout.
		}		
	}

	//method used to process the accept msg for the first time when the Paxos leader sends the data
	public void ProcessAcceptMessage(UUID uid, String data) throws IOException
	{
		TransactionStatus temp = new TransactionStatus();
		temp.state = AcceptorState.ACCEPT;
		this.uidTransactionStatusMap.put(uid, temp);

		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK, uid);
		this.SendMessageToPaxosLeader(msg);

	}

	public void SendMessageToPaxosLeader(PaxosMsg msg) throws IOException
	{		 
		messageController.SendMessage(Common.CreateMessageWrapper(msg), Common.DirectMessageExchange, this.paxosLeaderId);
	}

	//method used to process commit msg from paxos leader
	public void ProcessCommitMessage(UUID uid, int gsn) throws IOException
	{
		TransactionStatus temp = this.uidTransactionStatusMap.get(uid);
		temp.gsn=gsn;
		temp.state=AcceptorState.COMMIT;		
		this.uidTransactionStatusMap.put(uid, temp);
		ProcessCommitMessage(uid, gsn); //Write to file.

		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK, uid);
		this.SendMessageToPaxosLeader(msg);		
	}

	//method used to process abort msg from paxos leader
	public void ProcessAbortMessage(UUID uid)
	{
		TransactionStatus temp = this.uidTransactionStatusMap.get(uid);
		temp.state=AcceptorState.ABORT;
		this.uidTransactionStatusMap.put(uid, temp);
	}

	public void ProcessCommitToFile(UUID uid, int gsn)
	{
		if (gsn == this.lastCommitGSN + 1)
		{
			ProcessAppendToFile(uid);			

			while(true)
			{
				//TODO : Process buffer contents to commit.
			}
		}
		else
		{
			//TODO : ADD TO BUFFER.			
		}
	}

	public void ProcessAppendToFile(UUID uid)
	{
		//TODO : write to file.
		// Increment lastGSN;
	}


	//method used to process commit acknowledgement from other acceptors
	public void ProcessCommitAckMessage(UUID uid, int gsn, String nodeid)
	{
		TransactionStatus temp = this.uidTransactionStatusMap.get(uid);		
		temp.Acceptors.add(nodeid);

		if(temp.Acceptors.size()== Common.NoAcceptors)
		{			
			temp.state=AcceptorState.COMMIT_ACK;
			this.uidTransactionStatusMap.put(uid, temp);
		}
		else if (temp.state == AcceptorState.ACCEPT)
		{	
			temp.state = AcceptorState.COMMIT;
			temp.gsn = gsn;
			this.uidTransactionStatusMap.put(uid, temp);

			ProcessCommitToFile(uid, gsn);
		}


	}

	//method used to process abort acknowledgement from other acceptors
	public void ProcessAbortAckMessage(UUID uid, String nodeid)
	{
		TransactionStatus temp = this.uidTransactionStatusMap.get(uid);		
		temp.Acceptors.add(nodeid);

		if(temp.Acceptors.size()== Common.NoAcceptors)
		{			
			temp.state=AcceptorState.ABORT_ACK;				 
		}
		else if (temp.state == AcceptorState.ACCEPT)
		{	
			temp.state = AcceptorState.ABORT;			
		}

		this.uidTransactionStatusMap.put(uid, temp);

	}

}