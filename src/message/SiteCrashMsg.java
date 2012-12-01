package message;
import common.Common;
/*
 * This class is used to model the messages sent, to deal with site crashses
 */
public class SiteCrashMsg extends MessageBase{
	private String nodeid;
	private Common.SiteCrashMsgType type;
     
	public SiteCrashMsg(String nodeid,Common.SiteCrashMsgType type)
	{
		this.nodeid=nodeid;
		this.type=type;
	}
	public Common.SiteCrashMsgType getType() {
		return type;
	}
	public void setType(Common.SiteCrashMsgType type) {
		this.type = type;
	}     
     
}
