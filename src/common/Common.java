package common;
import java.util.HashMap;
import java.util.Map;

import message.PaxosMsg;

import com.google.gson.*;


public class Common {
	//Static Attributes
	public static String FilePath = "";
		//RMQ Attributes
	public static String RMQServer = "";
	public static String DirectMessageExchange = "DirectExchange";
	
	public static Integer NoAcceptors = 3;
	private static Map<String, String> NodeMap = new HashMap<String, String>();
	public static String InQueueSuffix = "_IN";
	
	
	//Instance Attributes
	
	
	//Enums
	public enum State {ACTIVE, PAUSED};
	public enum RequestType {PREPARE, COMMIT, ABORT};
	public enum ClientOPMsgType{READ,APPEND,READ_RESPONSE,APPEND_RESPONSE,ABORT};
	public enum PaxosMsgType{ACCEPT,ACK,COMMIT,ABORT};
	public enum TwoPCMsgType{COMMIT,ABORT,INFO};
	public enum SiteCrashMsgType{CRASH,RECOVER};
	public enum BcastMsgType{COMMIT_ACK,ABORT_ACK};
	
	public enum PaxosLeaderState{PREPARE, ACCEPT, COMMIT, COMMIT_ACK, ABORT};
	public enum AcceptorState{ACCEPT, COMMIT, COMMIT_ACK, ABORT, ABORT_ACK};
	public enum TPCState{INIT, COMMIT, COMMIT_ACK, ABORT};
	
	//Static Functions
	public static String Serialize(Object message)
	{
		Gson gson = new Gson();
		return gson.toJson(message);
	}
	
	//TODO: Use this function instead of local deserialization function in RMQReceiver. 
	@SuppressWarnings("rawtypes")
	public static <T> T Deserialize(String json, Class className)
	{
		Gson gson = new Gson();
		return (T) gson.fromJson(json, className.getClass());
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
	
	//Instance Functions
	
	
}
