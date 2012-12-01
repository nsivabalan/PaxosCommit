package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.Iterator;

import message.TwoPCMsg;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;

import com.sun.jmx.snmp.Timestamp;

import common.Common;
import common.Common.TPCState;
import common.Common.TwoPCMsgType;
import common.MessageWrapper;
import common.RMQSender;
import common.Request;
import common.Resource;

public class TPCCoordinator extends Node {
	//private Map<Integer, Map<>>
	
	final class TwoPhaseCommitStatus {
		Integer gsn;
		TPCState state;
		Map<String,Integer> nodeidtoLsnMap;
		Map<String,Integer> nodeidtoLsnMapack;
		Timestamp tmstamp_init;
		Timestamp tmstamp_final;
	}
	private RMQSender bcastTPCQueueSender;
	private Map<String, String> resourcePaxosLeaderMap;
	private MultiKeyMap datagsntoTwpPhaseStatus;
	//private Map<Integer,String> gsntoDataMap;
	private static int gsnCounter = 0;
	
	public TPCCoordinator(String nodeId, String fileName,String bcastTPCQueueName) throws IOException {
		//TPCCoordinator does not handle any resource.
		super(nodeId, "");
		this.resourcePaxosLeaderMap = new HashMap<String, String>();
		//this.gsntoDataMap=new HashMap<Integer, String>();
		datagsntoTwpPhaseStatus = MultiKeyMap.decorate(new LinkedMap(1000));
		this.bcastTPCQueueSender = new RMQSender(bcastTPCQueueName);
	}
	
	public void AddResourcePaxosLeaderMapping(String paxosLeaderId, String resourceName){
		this.resourcePaxosLeaderMap.put(resourceName, paxosLeaderId);		
	}
	
	
	public void run() throws IOException, InterruptedException, ClassNotFoundException{
		
		 while (true) {
			 MessageWrapper msgwrap= inQueue.ReceiveMessage();    
		    
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
	
	
	//method used to process New data from the client (forwarded by paxos leader after getting the majority
	public void processNewData(int lsn,String data,String nodeid) throws IOException
	{
		if(datagsntoTwpPhaseStatus.get(data)!=null)
		{
			TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwpPhaseStatus.get(data);
			if(temp.nodeidtoLsnMap.size()==1){
			temp.nodeidtoLsnMap.put(nodeid, lsn);
			temp.state=TPCState.COMMIT;
			int gsn=this.getNewGSN();
			temp.gsn=gsn;
			//this.gsntoDataMap.put(gsn, data);
			datagsntoTwpPhaseStatus.put(data, temp);
			TwoPhaseCommitStatus cur_stat=(TwoPhaseCommitStatus) datagsntoTwpPhaseStatus.get(gsn);
			Map<String,Integer> tempmap=cur_stat.nodeidtoLsnMap;
			ArrayList<Integer> lsnvalues= (ArrayList<Integer>) tempmap.values();
			for(int i=0;i<lsnvalues.size();i++)
			{
			this.sendCommitMessage(lsnvalues.get(i),gsn);
			}
			}
			else if(temp.nodeidtoLsnMap.size()==2){	//already 2 Paxos leaders have sent the msg
				
					//ignore ?
				}		
		}
			else
			{
				TwoPhaseCommitStatus temp=new TwoPhaseCommitStatus();
				//temp.lsn=lsn;
				temp.nodeidtoLsnMap.put(data, lsn);
				temp.state=TPCState.INIT;
				temp.tmstamp_init=new Timestamp();
				datagsntoTwpPhaseStatus.put(data,temp);
			}
		
	}
	
	//if TPC doesnt receive info msg from both paxos leaders, it sends a abort msg paxos leader who has just sent the data
	// or just the data(is there a need for lsn? if no, we can inform both the paxos leaders
	//we just know the lsn from only one PL
	public void sendAbortMessage(int lsn,String data,String nodeid) throws IOException
	{
		TwoPCMsg abortmsg = new TwoPCMsg(this.nodeId, Common.TwoPCMsgType.ABORT, lsn);
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(abortmsg), abortmsg.getClass());
		bcastTPCQueueSender.SendMessage(msgwrap);
	}
	
	//method used to send commit msg after receiving info msg from both the paxos leaders
	//call this method for each PL with diff lsn values
	public void sendCommitMessage(int lsn,int gsn) throws IOException
	{
		TwoPCMsg commitmsg = new TwoPCMsg(this.nodeId, Common.TwoPCMsgType.COMMIT, lsn,gsn);
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(commitmsg), commitmsg.getClass());
		bcastTPCQueueSender.SendMessage(msgwrap);
	}
	
	//method used to process commit acknowlegement either of paxos leader
	public void processCommitAck(int gsn,String nodeid)
	{
		if(datagsntoTwpPhaseStatus.get(gsn)!=null)
		{
			TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwpPhaseStatus.get(gsn);
			if(temp.nodeidtoLsnMapack.size()==0){				
				temp.nodeidtoLsnMapack.put(nodeid, gsn);
				temp.state=TPCState.COMMIT_ACK;
				temp.tmstamp_final=new Timestamp();
				datagsntoTwpPhaseStatus.put(gsn, temp);
				//wait for commit msg from other Paxos leader
			}
			if(temp.nodeidtoLsnMapack.size()==1){
				if(temp.nodeidtoLsnMapack.get(nodeid)!=null){
					temp.nodeidtoLsnMapack.put(nodeid, gsn);
					temp.state=TPCState.COMMIT_ACK;
				//	temp.tmstamp_final=new Timestamp();
					datagsntoTwpPhaseStatus.put(gsn, temp);
					//send commit msg to client
				}
			}		
			
			else if(temp.nodeidtoLsnMap.size()==2){	//already 2 Paxos leaders have sent the ack for commit or ack				
					//ignore ?
				}		
		}
			//what if datagsntoTwpPhaseStatus doesnt contain the gsn
	}
	
	//method used to process the abort acknowledgement msg from either of paxos leader
	public void processAbortAck(int lsn,String data,String nodeid)
	{
		if(datagsntoTwpPhaseStatus.get(data)!=null)
		{
			TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwpPhaseStatus.get(data);
			if(temp.nodeidtoLsnMapack.size()==1){
			temp.nodeidtoLsnMapack.put(nodeid, lsn);
			temp.state=TPCState.ABORT_ACK;
			temp.tmstamp_final=new Timestamp();
			datagsntoTwpPhaseStatus.put(data, temp);
			//send abort msg to client
			}
			else if(temp.nodeidtoLsnMap.size()==2){	//already 2 Paxos leaders have sent the ack for abort				
					//ignore ?
				}		
		}
			//what if datagsntoTwpPhaseStatus doesnt contain the gsn
	}
	
	
	private static int getNewGSN(){
		return gsnCounter++;
	}
	
}
