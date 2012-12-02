package message;
import java.util.UUID;

import common.Common;
/*
 * This class is used to model the messages dealing with paxos, like the accept msg, acknowledge msg and commit or abort msg which 
 * the paxos leader sends to all the acceptors in its paxos group
 */

public class PaxosMsg extends MessageBase{
	private String nodeid;
	private Common.PaxosMsgType type;
	private int gsn;
	private String data; 	
	
	//to send data to all acceptors for first time
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,UUID uid,String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
		this.data=data;
	}
	
	//to send accept msgs to acceptors after getting gsn from TPC
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,UUID uid, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
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
	//retry or send abort msg to acceptors, if so, what is de format of the msg?(PaxosMsgType,uid)
	
	public Common.PaxosMsgType getType() {
		return type;
	}
	
	public void setType(Common.PaxosMsgType type) {
		this.type = type;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public UUID getuid() {
		return this.uid;
	}

	public void setuid(UUID uid) {
		this.uid = uid;
	}

	public int getGsn() {
		return gsn;
	}

	public void setGsn(int gsn) {
		this.gsn = gsn;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	
	
}
