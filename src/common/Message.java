package common;
import com.google.gson.*;

// Message is a wrapper class to abstract any message class.
public class Message {
	private String serializedMessage;
	private String messageClass;
	
	public Message(String serializedMessage, String className)
	{
		this.serializedMessage = serializedMessage;
		this.messageClass = className;		
	}	
	
	//Serialize the Message Wrapper Class.
	public String getSerializedMessage()
	{
		return Common.Serialize(this);
	}
	
	//Static function to get Deserialized message.
	public static Message getDeSerializedMessage(String json)
	{
		Gson gson = new Gson();
		return gson.fromJson(json, Message.class);
	}
	
}
