package message;
import common.Common;
/*
 * This class is used to model the messages dealing with paxos, like the accept msg, acknowledge msg and commit or abort msg which 
 * the paxos leader sends to all the acceptors in its paxos group
 */

public class PaxosMsg extends MessageBase{
	private String nodeid;
	private Common.PaxosMsgType type;
	private int lsn; 
	private int gsn;
	private String data; 	
	
	//to send data to all acceptors for first time
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,int lsn,String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.lsn=lsn;
		this.data=data;
	}
	
	//to send accept msgs to acceptors after getting gsn from TPC
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,int lsn, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.lsn=lsn;
		this.gsn=gsn;
	}	
	
	//send commit/abort msg to acceptors after getting response from TPC
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;		
		this.gsn=gsn;
	}
	
    // when PL doesnt get response from TPC, what do we do?
	//retry or send abort msg to acceptors, if so, what is de format of the msg?(PaxosMsgType,lsn)
	
	public Common.PaxosMsgType getType() {
		return type;
	}
	
	public void setType(Common.PaxosMsgType type) {
		this.type = type;
	}
	
}
