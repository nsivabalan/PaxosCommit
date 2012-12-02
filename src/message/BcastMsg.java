package message;
import java.util.UUID;

import common.Common;

/*
 * This class is used to model messsages relating to broadcasting the commit or abort by all acceptors
 */
public class BcastMsg extends MessageBase{
	
	private String nodeid;
	private Common.BcastMsgType type;	
	private int gsn;
	private String data;

	//to bcast commit/abort msg
	public BcastMsg(String nodeid,Common.BcastMsgType type, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.gsn=gsn;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public Common.BcastMsgType getType() {
		return type;
	}

	public void setType(Common.BcastMsgType type) {
		this.type = type;
	}

	public UUID getUID() {
		return uid;
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

}