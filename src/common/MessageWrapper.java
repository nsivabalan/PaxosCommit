package common;
import message.MessageBase;

import com.google.gson.*;

// Message is a wrapper class to abstract any message class.
public class MessageWrapper {
	private String serializedMessage;
	private String messageClass;
	
	public MessageWrapper(String serializedMessage, String className)
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
		Gson gson = new Gson();
		return gson.fromJson(json, MessageWrapper.class);
	}
	
	public String getmessageclass()
	{
		return this.messageClass;
	}
	
	//Static function to get Deserialized Inner message.
	public static MessageBase getDeSerializedInnerMessage(String json)
	{
		Gson gson = new Gson();
		MessageWrapper msgwrap= gson.fromJson(json, MessageWrapper.class);
		return gson.fromJson(msgwrap.serializedMessage,msgwrap.messageClass);
	}
	
}
