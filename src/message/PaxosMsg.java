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
	
	//FOR INFO MSG.
	public PaxosMsg(String nodeid,Common.PaxosMsgType type, UUID uid, String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
		this.data=data;
	}
	
	//FOR COMMIT
	public PaxosMsg(String nodeid,Common.PaxosMsgType type,UUID uid, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
		this.gsn=gsn;
	}	
	
	//FOR ABORT
	public PaxosMsg(String nodeid,Common.PaxosMsgType type, UUID uid)
	{
		this.nodeid=nodeid;
		this.type=type;		
		this.uid = uid;
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

	public UUID getUID() {
		return this.uid;
	}

	public void setUID(UUID uid) {
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
