package common;
import java.io.IOError;
import java.io.IOException;

import com.rabbitmq.client.*;

public class RMQSender_Old {
	private String EXCHANGE_NAME = "";
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	
	public RMQSender_Old(String exchangeName) throws IOException{		
		this.EXCHANGE_NAME = exchangeName;
		factory = new ConnectionFactory();
		factory.setHost(Common.RMQServer);
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(this.EXCHANGE_NAME, "fanout");		
	}

	public void SendMessage(MessageWrapper message) throws IOException
	{
		channel.basicPublish(this.EXCHANGE_NAME, "", null, message.getSerializedMessage().getBytes());
		System.out.println("Sent Message "+ message.getSerializedMessage());
	}
			
}
