package node;

import java.io.IOException;

import common.Common;
import common.RMQReceiver;
import common.RMQSender_Old;



public class Acceptor extends Node {
		private String paxosLeaderId;
	
	public Acceptor(String nodeId, String fileName, String paxosLeaderId) throws IOException
	{
		super(nodeId, fileName);
		this.bcastQueueReceiver = new RMQReceiver(bcastExchangeName, true);
		this.bcastQueueSender = new RMQSender_Old(bcastExchangeName);
		this.paxosQueue = new RMQSender_Old(this.paxosLeaderId + Common.InQueueSuffix);
		
		
	}
}