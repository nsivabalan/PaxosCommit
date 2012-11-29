package message;


/*
 * This class is used to model the messages dealing with paxos, like the prepare msg, acknowledge msg and info msg which the 2PC coordinator
 * sends to all the paxos leaders
 */

public class PaxosMsg extends MessageBase{
	
	public enum msg_type {PREPARE,ACK,COMMIT,ABORT};
	msg_type type;
	int ballotnum; 
	int accept_num;
	String accept_val; 
	
	
	public msg_type getType() {
		return type;
	}
	
	public void setType(msg_type type) {
		this.type = type;
	}
	public int getBallotnum() {
		return ballotnum;
	}
	public void setBallotnum(int ballotnum) {
		this.ballotnum = ballotnum;
	}
	public int getAccept_num() {
		return accept_num;
	}
	public void setAccept_num(int accept_num) {
		this.accept_num = accept_num;
	}
	public String getAccept_val() {
		return accept_val;
	}
	public void setAccept_val(String accept_val) {
		this.accept_val = accept_val;
	}
	
}
