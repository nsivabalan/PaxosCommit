package message;
import java.util.UUID;

import common.Common;

/*
 * This class is used to model the messages relating to interaction with 2PC coordinator
 */
public class TwoPCMsg extends MessageBase{
	private String nodeid;
	private Common.TwoPCMsgType type;
	private int gsn;
	private String data; // represents the value

	//TPC responds to Paxos leaders with gsn
	public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,UUID uid, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
		this.gsn=gsn; 
	}


	//Paxos leader sends msg to TPC with uid and data
	public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,UUID uid,String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.uid=uid;
		this.data=data;
	}

	//TPC sends abort or commit msg to both PL in 2nd phase
	//it can be uid also in first phase?

	public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.gsn=gsn; 
	}

	public UUID getuid() {
		return this.uid;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
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

	public Common.TwoPCMsgType getType() {
		return type;
	}
	public void setType(Common.TwoPCMsgType type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}     

}