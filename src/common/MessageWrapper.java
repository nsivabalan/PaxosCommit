package common;
import message.MessageBase;

import com.google.gson.*;

// Message is a wrapper class to abstract any message class.
public class MessageWrapper {
	private String serializedMessage;
	private String messageClass;
	
	public MessageWrapper(String serializedMessage, Class className)
	{
		this.serializedMessage = serializedMessage;
		this.messageClass = className.getName();		
	}	
	
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
		return Common.Deserialize(json, MessageWrapper.class);
	}
	
	@SuppressWarnings("rawtypes")
	public Class getmessageclass() throws ClassNotFoundException
	{
		return Common.GetClassfromString(this.messageClass);
	}
	
	//Static function to get Deserialized Inner message.
	public MessageBase getDeSerializedInnerMessage() throws ClassNotFoundException
	{				
		MessageBase innerMsg = Common.Deserialize(this.serializedMessage, Common.GetClassfromString(this.messageClass));
		return innerMsg;
	}
	
	
	
}
