package node;

import java.io.IOException;

import common.RMQReceiver;
import common.RMQSender;

public class PaxosLeader extends Node{
	private RMQSender bcastQueueSender;
	private RMQReceiver tpcBcastQueueReceiver;
	
	
	public PaxosLeader(String nodeId, String fileName, String bcastQueueName, String tpcBcastQueueName) throws IOException {		
		super(nodeId, fileName);
		this.bcastQueueSender = new RMQSender(bcastQueueName);
		this.tpcBcastQueueReceiver = new RMQReceiver(tpcBcastQueueName, true);
	}
	

}
