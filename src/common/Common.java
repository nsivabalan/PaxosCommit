package common;
import com.google.gson.*;


public class Common {
	public enum State {ACTIVE, PAUSED};
	public static String RMQServer;
	
	public static String Serialize(Object message)
	{
		Gson gson = new Gson();
		return gson.toJson(message);
	}
	
	//TODO:
	public static <T> T Deserialize(String json, T className)
	{
		Gson gson = new Gson();
		return (T) gson.fromJson(json, className.getClass());
	}
}
