package node;

import java.io.IOException;

import common.Common;
import common.RMQReceiver;
import common.RMQSender;



public class Acceptor extends Node {
	private static String Exchange_PaxosLeader;
	
	private RMQReceiver bcastQueueReceiver;
	private RMQSender bcastQueueSender;
	private RMQSender paxosQueue;

	private String paxosLeaderId;
	
	public Acceptor(String nodeId, String fileName, String bcastExchangeName) throws IOException
	{
		super(nodeId, fileName);
		this.bcastQueueReceiver = new RMQReceiver(bcastExchangeName, true);
		this.bcastQueueSender = new RMQSender(bcastExchangeName);
		this.paxosQueue = new RMQSender(this.paxosLeaderId + Common.InQueueSuffix);
		
		
	}
}