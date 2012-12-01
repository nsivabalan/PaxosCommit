package message;
import common.Common;

/*
 * This class is used to model messsages relating to broadcasting the commit or abort by all acceptors
 */
public class BcastMsg extends MessageBase{
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

	public int getLsn() {
		return lsn;
	}

	public void setLsn(int lsn) {
		this.lsn = lsn;
	}

	public int getGsn() {
		return gsn;
	}

	public void setGsn(int gsn) {
		this.gsn = gsn;
	}

	private String nodeid;
	private Common.BcastMsgType type;
	private int lsn;
	private int gsn;

	//to bcast commit/abort msg
	public BcastMsg(String nodeid,Common.BcastMsgType type, int gsn)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.gsn=gsn;
	}

	//is there a scenario to bcast lsn with abort?
}