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
	public BcastMsg(String nodeid,Common.BcastMsgType type, int gsn, UUID uid, String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.gsn=gsn;
		this.uid = uid;
		this.data=data;
	}
	
	
	public String getData() {
		return data;
	}


	public void setData(String data) {
		this.data = data;
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
	
	
	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		
		bf.append("\n" + this.getClass().getName() + " - " + this.type);
		bf.append("\n Source - " + this.nodeid);
		bf.append("\n UUID - " + this.uid);
		bf.append("\n GSN - " + this.gsn);
		bf.append("\n Data - " + this.data);
		bf.append("\n");
		
		return bf.toString();
	}

}