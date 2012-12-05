package message;
import java.util.UUID;

import common.Common;
/* this class is used to model the messages relating to client operations like read request, append request and also to get back response
 * form the paxos leaders
 */

public class ClientOpMsg extends MessageBase{
	private String nodeid;	
	private Common.ClientOPMsgType type;	
	private String data;
	
	public UUID getUid() {
		return uid;
	}

	public void setUid(UUID uid) {
		this.uid = uid;
	}

	public ClientOpMsg(String nodeid, Common.ClientOPMsgType type, String data, UUID uid)
	{
		this.nodeid=nodeid;
		this.type=type;
		this.data=data;
		this.uid = uid;
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

	public String toString() {
		StringBuilder bf = new StringBuilder();
		
		//bf.append("\n" + this.getClass().getName() + " - " + this.type);
		bf.append("\n Source - " + this.nodeid);
		bf.append("\n UID - " + this.uid);		
		bf.append("\n Data - " + this.data);
		bf.append("\n");
		
		return bf.toString();
	}
	
}
