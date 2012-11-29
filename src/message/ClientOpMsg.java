package message;

/* this class is used to model the messages relating to client operations like read request, append request and also to get back response
 * form the paxos leaders
 */

public class ClientOpMsg extends MessageBase{
	public enum msg_type {READ,APPEND,READ_RESPONSE,APPEND_RESPONSE};
	msg_type type;
	String data;
	//data is empty incase of read, data to be appended incase of append, 
	//content of grads/stats file incase of READ_RESPONSE and boolean incase of APPEND_RESPONSE 
	
	
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
