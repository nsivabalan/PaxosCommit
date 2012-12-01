package common;

import java.io.IOError;
import java.io.IOException;

import com.rabbitmq.client.*;

public class RMQSenderNew {
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	
	public RMQSenderNew() throws IOException{		
		
		//Initialize RabbitMQ connection.
		factory = new ConnectionFactory();
		factory.setHost(Common.RMQServer);
		connection = factory.newConnection();
		channel = connection.createChannel();
		
	}
	
	public void DeclareExchange(String exchange, String exchangeType) throws IOException
	{
		this.channel.exchangeDeclare(exchange, exchangeType);
	}

	public void SendMessage(MessageWrapper message, String exchangeName, String routingKey) throws IOException
	{
		channel.basicPublish(exchangeName, routingKey, null, message.getSerializedMessage().getBytes());
		System.out.println("Sent Message "+ message.getSerializedMessage());
	}
			
}
