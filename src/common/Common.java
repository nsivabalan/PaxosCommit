package common;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.*;


public class Common {
	//Static Attributes
	public static String FilePath = "";
	public static String RMQServer = "";
	private static Map<String, String> NodeMap = new HashMap<String, String>();
	
	//Instance Attributes
	
	
	//Enums
	public enum State {ACTIVE, PAUSED};
	public enum RequestType {PREPARE, COMMIT, ABORT};
	
	//Static Functions
	public static String Serialize(Object message)
	{
		Gson gson = new Gson();
		return gson.toJson(message);
	}
	
	//TODO: Use this function instead of local deserialization function in RMQReceiver. 
	public static <T> T Deserialize(String json, T className)
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
	
	//Instance Functions
	
	
}
