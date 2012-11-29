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
	
	public boolean SendMessage(MessageWrapper msg)
	{
		
	return true;
	}
	
	public MessageWrapper ReceiveMessage()
	{
	return new MessageWrapper("","");	
	}
}
