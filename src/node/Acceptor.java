package node;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import message.PaxosMsg;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import common.Common;
import common.Common.AcceptorState;
import common.MessageWrapper;
import common.RMQReceiver;
import common.RMQSender;

public class Acceptor extends Node {
	private RMQReceiver bcastQueueReceiver;
	private RMQSender bcastQueueSender;
	private RMQSender paxosQueue;

	private String paxosLeaderId;
	private Map<String,Integer> datatoLsn;
	final class AckStatusMap {
		Integer gsn;
		Common.AcceptorState state;
		Set<String> Acceptors;		
	}
	private MultiKeyMap datalsntoAckStatusMap;
	
	public Acceptor(String nodeId, String fileName, String bcastExchangeName) throws IOException
	{
		super(nodeId, fileName);
		this.bcastQueueReceiver = new RMQReceiver(bcastExchangeName, true);
		this.bcastQueueSender = new RMQSender(bcastExchangeName);
		this.paxosQueue = new RMQSender(this.paxosLeaderId + Common.InQueueSuffix);
		datalsntoAckStatusMap = MultiKeyMap.decorate(new LinkedMap(1000));
		
	}
	
	public void run() throws IOException, InterruptedException, ClassNotFoundException{
		
		 while (true) {
			 MessageWrapper msgwrap= bcastQueueReceiver.ReceiveMessage();    
		    
				if(msgwrap.getmessageclass().equals("PaxosMsg"))
				{
				
				}
				
		  }		
	}
	
	//method used to process the accept msg for the first time when the Paxos leader sends the data
	public void processAcceptMessage(int lsn,String data) throws IOException
	{
		if(datatoLsn.get(data)==null)
		{
			datatoLsn.put(data,lsn);
			AckStatusMap ackstatusmap=new AckStatusMap();
			ackstatusmap.state=AcceptorState.ACCEPT;
			datalsntoAckStatusMap.put(data,lsn,ackstatusmap);
			//send ack msg to PL
			this.sendPrepareAcktoPaxosLeader(lsn);
		}
		else{
			//what do we do?
		}
	}
	
	//method used to send acknowledgement msg to the paxos leader for the received msg
	public void sendPrepareAcktoPaxosLeader(int lsn) throws IOException
	{
		
		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK,lsn); 
		paxosQueue.SendMessage(Common.CreateMessageWrapper(msg));
	}
	
	//method used to send commit acknowledgement to every other acceptors
	public void sendCommitAcktoPaxosLeader(int gsn) throws IOException
	{
		
		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK,gsn); 
		paxosQueue.SendMessage(Common.CreateMessageWrapper(msg));
	}
	
	//method used to send abort acknowledgement to every other acceptors
	public void sendAbortAcktoPaxosLeader(int lsn) throws IOException
	{
		
		PaxosMsg msg = new PaxosMsg(this.nodeId, Common.PaxosMsgType.ACK,lsn); 
		paxosQueue.SendMessage(Common.CreateMessageWrapper(msg));
	}
	
	//method used to process commit msg from paxos leader
	public void processCommitMessage(int lsn,int gsn)
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
	public void processAbortMessage(int lsn)
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
	public void processCommitAckMessage(int lsn,String nodeid)
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
	public void processAbortAckMessage(int lsn,String nodeid)
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