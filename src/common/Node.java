package common;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import common.Common;

public class Node {
	
	private Resource localResource;
	private Map<Integer, Request> requestMap;
	private Queue<Request> requestQueue;
	private String nodeId;
	private RMQSender outQueue;
	private RMQReceiver inQueue;
	
	private final static Logger LOGGER = Logger.getLogger(Node.class .getName()); 

	
	public Node(String nodeId, String fileName) throws IOException
	{
		this.nodeId = nodeId;
		this.localResource = new Resource(fileName, Common.FilePath);
		this.requestMap = new HashMap<Integer, Request>();
		this.requestQueue = new LinkedList<Request>();	
		this.outQueue = new RMQSender(this.nodeId+"_out");
		this.inQueue = new RMQReceiver(this.nodeId+"in", false);
	}	
}