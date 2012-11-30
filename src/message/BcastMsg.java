package message;
import common.Common;

/*
 * This class is used to model messsages relating to broadcasting the commit or abort by all acceptors
 */
public class BcastMsg {
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