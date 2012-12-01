package common;
import message.MessageBase;

import com.google.gson.*;

// Message is a wrapper class to abstract any message class.
public class MessageWrapper {
	private String serializedMessage;
	private Class messageClass;
	
	public MessageWrapper(String serializedMessage, Class className)
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
	public static MessageWrapper getDeSerializedMessage(String json)
	{		
		return Common.Deserialize(json, MessageWrapper.class);
	}
	
	@SuppressWarnings("rawtypes")
	public Class getmessageclass()
	{
		return this.messageClass;
	}
	
	//Static function to get Deserialized Inner message.
	public static MessageBase getDeSerializedInnerMessage(String json)
	{
		MessageWrapper msg = Common.Deserialize(json, MessageWrapper.class);
		MessageBase innerMsg = Common.Deserialize(msg.serializedMessage, msg.messageClass);
		return innerMsg;
	}
	
}
