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
import common.MessageController;
import common.Request;
import common.Resource;
import common.Tuple;

public class Node {
	protected String nodeId;
	private Resource localResource;

	protected MessageController messageController;
	
	private final static Logger LOGGER = Logger.getLogger(Node.class.getName());
	private static FileHandler logFile;



	public Node(String nodeId, String fileName) throws IOException
	{
		this.nodeId = nodeId;
		//Bind the resource
		this.localResource = new Resource(fileName, Common.FilePath);

		//RMQ Message Controller.
		this.messageController = new MessageController(this.nodeId);

		//Logging Specific
		logFile = new FileHandler(Common.FilePath+"/"+this.nodeId+".log");
		logFile.setFormatter(new SimpleFormatter());
		LOGGER.setLevel(Level.INFO); //Sets the default level if not provided.		
		LOGGER.addHandler(logFile);		
	}	

	public void DeclareExchanges( Tuple<String, String>[] exchanges) throws IOException
	{
		//Declare exchanges for the sender.
		for(Tuple<String, String> exchange : exchanges)
		{
			this.messageController.DeclareExchange(exchange.x, exchange.y);
		}
	}
	
	public void InitializeConsumer() throws IOException
	{
		this.messageController.InitializeConsumer();
	}

	//Add a new log entry.
	public void AddLogEntry(String message, String status, Level level){		
		LOGGER.logp(level, this.getClass().toString(), message, status);		
	}


}