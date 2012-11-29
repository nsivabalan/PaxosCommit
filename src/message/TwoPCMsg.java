package message;

/*
 * This class is used to model the messages relating to interaction with 2PC coordinator
 */

public class TwoPCMsg extends MessageBase{
     public enum msg_type{COMMIT,ABORT};
     msg_type type;
     String data; // represents the value
	public msg_type getType() {
		return type;
	}
	public void setType(msg_type type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}     
     
}