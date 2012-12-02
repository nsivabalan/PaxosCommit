package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.MultiKeyMap;
import java.sql.Timestamp;

import common.Common;
import common.Common.ClientOPMsgType;
import common.Tuple;
import common.Common.TPCState;
import common.Common.TwoPCMsgType;
import common.MessageWrapper;
import message.ClientOpMsg;
import message.TwoPCMsg;


public class TPCCoordinator extends Node {

	final class TwoPhaseCommitStatus {
		Integer gsn;
		TPCState state;
		Map<String,Integer> nodeidtoLsnMap;
		Map<String,Integer> nodeidtoLsnCommitackMap;
		Map<String,Integer> nodeidtoLsnAbortackMap;
		Timestamp tmstamp_init;
		Timestamp tmstamp_final;
		
		public TwoPhaseCommitStatus(int lsn)
		{
			this.gsn = -1;
			this.state = TPCState.INIT;
			this.tmstamp_init=new Timestamp(new Date().getTime());
			this.nodeidtoLsnMap = new HashMap<String,Integer>();	
			this.nodeidtoLsnCommitackMap =  new HashMap<String,Integer>();	
			this.nodeidtoLsnAbortackMap =  new HashMap<String,Integer>();	
		}
	}

	
	private String TwoPCExchange;
	private Map<String, String> resourcePaxosLeaderMap;
	private MultiKeyMap datagsntoTwoPhaseStatus;

	private static int gsnCounter = 0;

	public TPCCoordinator(String nodeId, String fileName,String bcastTPCQueueName) throws IOException {
		//TPCCoordinator does not handle any resource.
		super(nodeId, "");

		//Initialize the Message Coordinator Exchanges.
		this.TwoPCExchange = Common.TPCCoordinatorExchange + this.nodeId;
		
		ArrayList<Tuple<String, String>> exchanges = new ArrayList<Tuple<String, String>>();
		exchanges.add(new Tuple(Common.DirectMessageExchange, Common.directExchangeType));
		exchanges.add(new Tuple(this.TwoPCExchange, Common.bcastExchangeType));
		
		this.DeclareExchanges(exchanges);
		this.InitializeConsumer();
		
		//Initialize the Local data structures.
		this.resourcePaxosLeaderMap = new HashMap<String, String>();
		this.datagsntoTwoPhaseStatus = MultiKeyMap.decorate(new LinkedMap(1000));
	}

	public void AddResourcePaxosLeaderMapping(String paxosLeaderId, String resourceName){
		this.resourcePaxosLeaderMap.put(resourceName, paxosLeaderId);		
	}


	public void run() throws IOException, InterruptedException, ClassNotFoundException{

		while (true) {
			MessageWrapper msgwrap= messageController.ReceiveMessage();    

			if(msgwrap.getmessageclass() == TwoPCMsg.class)
			{
				TwoPCMsg msg = (TwoPCMsg) msgwrap.getDeSerializedInnerMessage();
				if (msg.getType() == TwoPCMsgType.INFO)
					ProcessInfoRequest(msg.getLsn(), msg.getData(), msg.getNodeid());
				else if (msg.getType() == TwoPCMsgType.COMMIT)
					ProcessCommitAck(msg.getGsn(), msg.getNodeid());
				else if (msg.getType() == TwoPCMsgType.ABORT)
					ProcessAbortAck(msg.getLsn(), msg.getNodeid(),msg.getData());
			}
		}		
	}


	//method used to process New data from the client (forwarded by paxos leader after getting the majority
	public void ProcessInfoRequest(int lsn,String data,String nodeid) throws IOException
	{
		if(datagsntoTwoPhaseStatus.get(data)!=null)
		{
			TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwoPhaseStatus.get(data);
			
			if(!(temp.nodeidtoLsnMap.containsKey(nodeid)))
			{
				temp.nodeidtoLsnMap.put(nodeid, lsn);						
				if (temp.nodeidtoLsnMap.size() == Common.NoPaxosLeaders && temp.state == TPCState.INIT) 
				{					
					temp.state=TPCState.COMMIT;
					int gsn=this.getNewGSN();
					temp.gsn=gsn;
					datagsntoTwoPhaseStatus.put(data, temp);
					TwoPhaseCommitStatus cur_stat=(TwoPhaseCommitStatus) datagsntoTwoPhaseStatus.get(gsn);
					Map<String,Integer> tempmap=cur_stat.nodeidtoLsnMap;
					ArrayList<Integer> lsnvalues= (ArrayList<Integer>) tempmap.values();
					for(int i=0;i<lsnvalues.size();i++)
					{
						this.SendCommitMessage(lsnvalues.get(i),gsn);
					}			
				}
				else
				{
					datagsntoTwoPhaseStatus.put(data, temp);	
				}
			
			}	
		}
		else
		{
			TwoPhaseCommitStatus temp=new TwoPhaseCommitStatus(lsn);
			datagsntoTwoPhaseStatus.put(data,temp);
		}

	}

	//method used to process commit acknowlegement either of paxos leader
	public void ProcessCommitAck(int gsn,String nodeid)
	{
		
			TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwoPhaseStatus.get(gsn);
			if(!(temp.nodeidtoLsnCommitackMap.containsKey(nodeid))){
				temp.nodeidtoLsnCommitackMap.put(nodeid, gsn);			
			if((temp.nodeidtoLsnCommitackMap.size()==Common.NoPaxosLeaders) && temp.state== TPCState.COMMIT){					
					temp.state=TPCState.COMMIT_ACK;	
					datagsntoTwoPhaseStatus.put(gsn, temp);
					SendClientCommitResponse("append","done with append");
					//fix this
			}
			else{
				temp.tmstamp_final=new Timestamp(new Date().getTime());
				datagsntoTwoPhaseStatus.put(gsn, temp);	
			}
			}
		
	}

	//method used to process the abort acknowledgement msg from either of paxos leader
	public void ProcessAbortAck(int lsn, String nodeid,String data)
	{
		TwoPhaseCommitStatus temp=(TwoPhaseCommitStatus) datagsntoTwoPhaseStatus.get(data);
		
		if(!(temp.nodeidtoLsnAbortackMap.containsKey(nodeid))){
			temp.nodeidtoLsnAbortackMap.put(nodeid, lsn);
		if((temp.nodeidtoLsnAbortackMap.size()==Common.NoPaxosLeaders) && temp.state== TPCState.ABORT){					
				temp.state=TPCState.ABORT_ACK;
				datagsntoTwoPhaseStatus.put(data, temp);
				SendClientAbortResponse(data);			
		}
		else{
			temp.tmstamp_final=new Timestamp(new Date().getTime());
			datagsntoTwoPhaseStatus.put(data, temp);		
		}
		}
		
	}
	
	public void SendClientCommitResponse(String response,String data)
	{
		ClientOpMsg msg;
		if(response.equals("read"))
		msg=new ClientOpMsg(this.nodeId, ClientOPMsgType.READ_RESPONSE, data);
		if(response.equals("append"))
		msg=new ClientOpMsg(this.nodeId, ClientOPMsgType.APPEND_RESPONSE, data);
		
	}	
	
	public void SendClientAbortResponse(String data)
	{
		ClientOpMsg msg=new ClientOpMsg(this.nodeId, ClientOPMsgType.ABORT, data);
	}
	
	public void SendClientMessage(ClientOpMsg msg) throws IOException
	{
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(msg), msg.getClass());
		this.messageController.SendMessage(msgwrap, Common.DirectMessageExchange, "");
		//fix this
	}
	
	//if TPC doesnt receive info msg from both paxos leaders, it sends a abort msg paxos leader who has just sent the data
	// or just the data(is there a need for lsn? if no, we can inform both the paxos leaders
	//we just know the lsn from only one PL
	public void SendAbortMessage(int lsn,String data,String nodeid) throws IOException
	{
		TwoPCMsg abortmsg = new TwoPCMsg(this.nodeId, Common.TwoPCMsgType.ABORT, lsn);
		SendTPCMessage(abortmsg);
	}

	//method used to send commit msg after receiving info msg from both the paxos leaders
	//call this method for each PL with diff lsn values
	public void SendCommitMessage(int lsn,int gsn) throws IOException
	{
		TwoPCMsg commitmsg = new TwoPCMsg(this.nodeId, Common.TwoPCMsgType.COMMIT, lsn,gsn);
		SendTPCMessage(commitmsg);
	}
	

	public void SendTPCMessage(TwoPCMsg msg) throws IOException
	{
		MessageWrapper msgwrap = new MessageWrapper(Common.Serialize(msg), msg.getClass());
		this.messageController.SendMessage(msgwrap, this.TwoPCExchange, "");
	}
	


	private static int getNewGSN(){
		return gsnCounter++;
	}

}
