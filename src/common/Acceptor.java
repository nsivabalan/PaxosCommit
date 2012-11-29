package common;

import java.io.IOException;

public class Acceptor extends Node {
	private RMQReceiver bcastQueue;	 
	private String paxosLeaderId;
	
	public Acceptor(String nodeId, String fileName, String bcastExchangeName, String selfQueueName) throws IOException
	{
		super(nodeId, fileName);
		this.bcastQueue = new RMQReceiver(bcastExchangeName, true);		
	}
}