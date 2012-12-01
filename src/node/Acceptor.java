package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import message.BcastMsg;
import message.PaxosMsg;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import common.Common;
import common.Common.AcceptorState;
import common.Common.BcastMsgType;
import common.Common.PaxosMsgType;
import common.MessageWrapper;
import common.RMQReceiver;
import common.RMQSender_Old;
import common.Tuple;

public class Acceptor extends Node {

	final class AckStatusMap {
		Integer gsn;
		Common.AcceptorState state;
		Set<String> Acceptors;		
	}

	private String paxosLeaderId;
	private String paxosLeaderExchange;
	private Map<String,Integer> datatoLsn;
	private MultiKeyMap datalsntoAckStatusMap;

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

		datalsntoAckStatusMap = MultiKeyMap.decorate(new LinkedMap(1000));
	}



	public void run() throws IOException, InterruptedException, ClassNotFoundException{

		while (true) {
			MessageWrapper msgwrap= messageController.ReceiveMessage();   
			if (msgwrap.getmessageclass() == BcastMsg.class)
			{
				BcastMsg msg = (BcastMsg) msgwrap.getDeSerializedInnerMessage();

				if(msg.getType() == BcastMsgType.COMMIT_ACK)
					ProcessCommitAckMessage(msg.getLsn(), msg.getNodeid());

				else if (msg.getType() == BcastMsgType.ABORT_ACK)
					ProcessAbortAckMessage(msg.getLsn(), msg.getNodeid());

			}
			else if (msgwrap.getmessageclass() == PaxosMsg.class)
			{
				PaxosMsg msg = (PaxosMsg) msgwrap.getDeSerializedInnerMessage();

				if(msg.getType() == PaxosMsgType.ACCEPT)
					ProcessAcceptMessage(msg.getLsn(), msg.getData());

				else if (msg.getType() == PaxosMsgType.COMMIT)
					ProcessCommitMessage(msg.getLsn(), msg.getGsn());

				else if (msg.getType() == PaxosMsgType.ABORT)
					ProcessAbortMessage(msg.getLsn());
			}
		}		
	}

	//method used to process the accept msg for the first time when the Paxos leader sends the data
	public void ProcessAcceptMessage(int lsn,String data) throws IOException
	{
		if(datatoLsn.get(data)==null)
		{
			datatoLsn.put(data,lsn);
			AckStatusMap ackstatusmap=new AckStatusMap();
			ackstatusmap.state=AcceptorState.ACCEPT;
			datalsntoAckStatusMap.put(data,lsn,ackstatusmap);
			//send ack msg to PL
			this.SendMessageToPaxosLeader(lsn);
		}
		else{
			//what do we do?
		}
	}

	public void SendMessageToPaxosLeader(int lsn) throws IOException
	{
		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK, lsn); 
		messageController.SendMessage(Common.CreateMessageWrapper(msg), Common.DirectMessageExchange, this.paxosLeaderId);
	}

	//method used to process commit msg from paxos leader
	public void ProcessCommitMessage(int lsn,int gsn)
	{
		if(datalsntoAckStatusMap.get(lsn)!=null)
		{
			AckStatusMap tempmap=(AckStatusMap) datalsntoAckStatusMap.get(lsn);
			tempmap.gsn=gsn;
			tempmap.state=AcceptorState.COMMIT;
			datalsntoAckStatusMap.put(lsn,tempmap);
			//send commit ack to PL
		}
		else{
			//what do we do?
		}
	}

	//method used to process abort msg from paxos leader
	public void ProcessAbortMessage(int lsn)
	{
		if(datalsntoAckStatusMap.get(lsn)!=null)
		{
			AckStatusMap tempmap=(AckStatusMap) datalsntoAckStatusMap.get(lsn);
			tempmap.state=AcceptorState.ABORT;
			datalsntoAckStatusMap.put(lsn,tempmap);
			//send abort_ack to PL
		}
	}

	//method used to process commit acknowledgement from other acceptors
	public void ProcessCommitAckMessage(int lsn,String nodeid)
	{
		if(datalsntoAckStatusMap.get(lsn)!=null)
		{
			AckStatusMap tempmap=(AckStatusMap) datalsntoAckStatusMap.get(lsn);
			tempmap.Acceptors.add(nodeid);
			if(tempmap.Acceptors.size()==3){
				tempmap.state=AcceptorState.COMMIT_ACK;
				datalsntoAckStatusMap.put(lsn,tempmap);			
			}
			else
			{
				datalsntoAckStatusMap.put(lsn,tempmap);
			}
		}
		else{
			//what do we do?
		}
	}

	//method used to process abort acknowledgement from other acceptors
	public void ProcessAbortAckMessage(int lsn,String nodeid)
	{
		if(datalsntoAckStatusMap.get(lsn)!=null)
		{
			AckStatusMap tempmap=(AckStatusMap) datalsntoAckStatusMap.get(lsn);
			tempmap.Acceptors.add(nodeid);
			if(tempmap.Acceptors.size()==3){
				tempmap.state=AcceptorState.ABORT_ACK;
				datalsntoAckStatusMap.put(lsn,tempmap);			 
			}
			else{
				datalsntoAckStatusMap.put(lsn,tempmap);
			}
		}
		else{
			//what to we do?
		}

	}

}