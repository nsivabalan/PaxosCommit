package message;
import common.Common;

/*
 * This class is used to model the messages relating to interaction with 2PC coordinator
 */
public class TwoPCMsg extends MessageBase{
	private String nodeid;
	private Common.TwoPCMsgType type;
	private int lsn;
	private int gsn;
	private String data; // represents the value
     
     //TPC responds to Paxos leaders with gsn
     public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,int lsn, int gsn)
     {
    	 this.nodeid=nodeid;
    	 this.type=type;
    	 this.lsn=lsn;
    	 this.gsn=gsn; 
     }
     
     public int getLsn() {
		return lsn;
	}

	public String getNodeid() {
		return nodeid;
	}

	public void setNodeid(String nodeid) {
		this.nodeid = nodeid;
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

	//Paxos leader sends msg to TPC with lsn and data
     public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,int lsn,String data)
     {
    	 this.nodeid=nodeid;
    	 this.type=type;
    	 this.lsn=lsn;
    	 this.data=data;
     }
     
     //TPC sends abort or commit msg to both PL in 2nd phase
     //it can be lsn also in first phase?
     
     public TwoPCMsg(String nodeid,Common.TwoPCMsgType type,int gsn)
     {
    	 this.nodeid=nodeid;
    	 this.type=type;
    	 this.gsn=gsn; 
     }
     
     
	public Common.TwoPCMsgType getType() {
		return type;
	}
	public void setType(Common.TwoPCMsgType type) {
		this.type = type;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}     
     
}