package node;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import common.Common;
import common.RMQReceiver;
import common.RMQSender;
import common.Request;
import common.Resource;

public class Node {
	private String nodeId;
	private Resource localResource;
	private Map<Integer, Request> requestMap;
	private Queue<Request> requestQueue;
	
	private RMQReceiver inQueue;
	
	private final static Logger LOGGER = Logger.getLogger(Node.class.getName());
	private static FileHandler logFile;
	

	
	public Node(String nodeId, String fileName) throws IOException
	{
		this.nodeId = nodeId;
		this.localResource = new Resource(fileName, Common.FilePath);
		this.requestMap = new HashMap<Integer, Request>();
		this.requestQueue = new LinkedList<Request>();	
		
		this.inQueue = new RMQReceiver(this.nodeId+Common.InQueueSuffix, false);
		
		//Logging Specific
		logFile = new FileHandler(Common.FilePath+"/"+this.nodeId+".log");
		logFile.setFormatter(new SimpleFormatter());
		LOGGER.setLevel(Level.INFO); //Sets the default level if not provided.		
		LOGGER.addHandler(logFile);		
	}	
	
	//Add a new log entry.
	public void AddLogEntry(String message, String status, Level level){		
		LOGGER.logp(level, this.getClass().toString(), message, status);		
	}
	
	
}