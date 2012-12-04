package common;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import message.PaxosMsg;

import com.google.gson.*;


public class Common {
	
	//RMQ Attributes
	public static String RMQServer = "";
	public static String DirectMessageExchange = "DirectExchange";
	public static String PaxosLeaderExchange = "PaxosLeaderExchange";
	public static String TPCCoordinatorExchange = "2PCCoordinatorExchange";
	public static String directExchangeType = "direct";
	public static String bcastExchangeType = "fanout";
	
	//Configuration Attributes
	public static Integer NoAcceptors = 3;	
	public static Integer NoPaxosLeaders = 2;
	public static String FilePath = "";
	public static Integer ReadLineCount = 0;

	public static Integer init_timeout=10;
	public static Integer commitabort_timeout=15;
	
	private static Map<String, String> NodeMap = new HashMap<String, String>();
	public static String InQueueSuffix = "_IN";
	
	
	//Instance Attributes
	
	//Enums
	public enum State {ACTIVE, PAUSED};
	public enum RequestType {PREPARE, COMMIT, ABORT};
	public enum ClientOPMsgType{READ,APPEND,READ_RESPONSE,APPEND_RESPONSE,ABORT};
	public enum PaxosMsgType{ACCEPT,ACK,COMMIT,ABORT};
	public enum TwoPCMsgType{COMMIT, ABORT, INFO, ACK};
	public enum SiteCrashMsgType{CRASH,RECOVER};
	public enum BcastMsgType{COMMIT_ACK,ABORT_ACK};
	public enum PaxosLeaderState{PREPARE, ACCEPT, COMMIT, COMMIT_ACK, ABORT, ABORT_ACK};
	public enum AcceptorState{ACCEPT, COMMIT, COMMIT_ACK, ABORT, ABORT_ACK};
	public enum TPCState{INIT, COMMIT, COMMIT_ACK, ABORT, ABORT_ACK};
	
	//Static Functions
	public static <T> String Serialize(T message)
	{
		Gson gson = new Gson();
		return gson.toJson(message, message.getClass());
	}
	
	//TODO: Use this function instead of local deserialization function in RMQReceiver. 
	@SuppressWarnings("rawtypes")
	public static <T> T Deserialize(String json, Class className)
	{
		Gson gson = new Gson();
		return (T) gson.fromJson(json, className);
	}
	
	//Translate Node Address from Node Id.
	public static String GetNodeAddress(String nodeId){
		return NodeMap.get(nodeId);
	}
	
	//Add an entry to Node Id to Address Translation Map.
	public static void AddNodeAddress(String nodeId, String nodeAddress){
		NodeMap.put(nodeId, nodeAddress);
	}
	
	public static <T> MessageWrapper CreateMessageWrapper(T message){
		return new MessageWrapper(Common.Serialize(message), message.getClass());
	}
	
	public static Class GetClassfromString(String s) throws ClassNotFoundException
	{
		Class<?> cls = Class.forName(s);
		return cls;
	}
	
	public static Integer GetQuorumSize()
	{
		return (int) (Math.floor(Common.NoAcceptors/2) + 1);
	}
	
	public static Timestamp getUpdatedTimestamp(Timestamp original, int sec){
		
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(original.getTime());
        cal.add(Calendar.SECOND, sec);
        Timestamp later = new Timestamp(cal.getTime().getTime());
        return later;
	}
	//Instance Functions
	
	
}
