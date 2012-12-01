package common;

import com.rabbitmq.client.*;
import java.io.IOException;


public class RMQReceiverNew {
	private String[] Exchanges;
	
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private QueueingConsumer consumer;
	
	private String QueueName;
	
	public RMQReceiverNew(String[] exchanges, String queueName) throws IOException
	{
		//Get the list of all exchanges to subscribe to.
		this.Exchanges = exchanges;
		this.QueueName = queueName;
		
		//Initialize RabbitMQ connection.
		this.factory = new ConnectionFactory();
		this.factory.setHost(Common.RMQServer);
		this.connection = this.factory.newConnection();
		this.channel = this.connection.createChannel();
		
		for(String exchange : this.Exchanges)
		{
			this.channel.queueBind(this.QueueName, exchange, this.QueueName);
		}
		
		this.consumer = new QueueingConsumer(this.channel);
		this.channel.basicConsume(this.QueueName, true, this.consumer);
		
	}
	
	public void BindQueueToExchange(String exchange) throws IOException
	{
		this.channel.queueBind(this.QueueName, exchange, this.QueueName);
	}
	
	public MessageWrapper ReceiveMessage() throws IOException, InterruptedException
	{
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String msg = new String(delivery.getBody());
		System.out.println("Received Message" + msg);
		return MessageWrapper.getDeSerializedMessage(msg);
	}

}
