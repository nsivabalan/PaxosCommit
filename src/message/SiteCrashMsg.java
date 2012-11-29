package message;

/*
 * This class is used to model the messages sent, to deal with site crashses
 */
public class SiteCrashMsg extends MessageBase{
     public enum msg_type{CRASH,RECOVER};
     msg_type type;
     
	public msg_type getType() {
		return type;
	}
	public void setType(msg_type type) {
		this.type = type;
	}     
     
}
