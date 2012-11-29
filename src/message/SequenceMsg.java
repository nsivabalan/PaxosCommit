package message;

/*
 * this class is used to model the messages relating to sequencer 
 */

public class SequenceMsg extends MessageBase{
	public enum msg_type {REQUEST,RESPONSE};
	msg_type type;
	int seq_num;
	String data;
	
	public msg_type getType() {
		return type;
	}
	public void setType(msg_type type) {
		this.type = type;
	}
	public int getSeq_num() {
		return seq_num;
	}
	public void setSeq_num(int seq_num) {
		this.seq_num = seq_num;
	}
	public String getdata() {
		return data;
	}
	public void setdata(String data) {
		this.data = data;
	}
	
	
}