package message;
import common.Common;
/* this class is used to model the messages relating to client operations like read request, append request and also to get back response
 * form the paxos leaders
 */

public class ClientOpMsg extends MessageBase{
	private String nodeid;
	private Common.ClientOPMsgType type;
	private String data;
	//data is empty incase of read, data to be appended incase of append, 
	//content of grads/stats file incase of READ_RESPONSE and boolean incase of APPEND_RESPONSE 
	
	public ClientOpMsg(String nodeid,Common.ClientOPMsgType type,String data)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.data=data;
	}
	
	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
	}

	public Common.ClientOPMsgType getType() {
		return type;
	}
	public void setType(Common.ClientOPMsgType type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
}
