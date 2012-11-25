package common;

/*
 * Rename to Queue?
 * Add RabbitMQ interface for sending and receiving messages.
 */

public class NetworkAddress {
	String server;
	
	public NetworkAddress(String server)
	{
		this.server = server;
	}
	
	public boolean SendMessage(Message msg)
	{
		
	return true;
	}
	
	public Message ReceiveMessage()
	{
	return new Message();	
	}
}
