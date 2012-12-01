package common;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class RMQReceiver {
	private String EXCHANGE_NAME = "";
	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private QueueingConsumer consumer;
	private Boolean broadcast;
	private String queueName;
	
	public RMQReceiver(String exchangeName, Boolean broadcast) throws IOException{
		this.broadcast = broadcast;
		this.EXCHANGE_NAME = exchangeName;
		this.factory = new ConnectionFactory();
		this.factory.setHost(Common.RMQServer);
		this.connection = this.factory.newConnection();
		this.channel = this.connection.createChannel();		
		
		if (this.broadcast)
		{
			this.channel.exchangeDeclare(this.EXCHANGE_NAME, "fanout");
			this.queueName = this.channel.queueDeclare().getQueue();
			this.channel.queueBind(this.queueName, this.EXCHANGE_NAME, "fanout");			
		}
		else 
		{
			this.channel.queueDeclare(this.EXCHANGE_NAME, false, false, false, null);
		}
		
		this.consumer = new QueueingConsumer(this.channel);
		
		if(this.broadcast)
			this.channel.basicConsume(this.queueName, true, this.consumer);
		else
			this.channel.basicConsume(this.EXCHANGE_NAME, true, this.consumer);
	}
	
	
	public MessageWrapper ReceiveMessage() throws IOException, InterruptedException
	{	
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		String msg = new String(delivery.getBody());
		System.out.println("Received Message "+msg);
		return MessageWrapper.getDeSerializedMessage(msg);		
	}
}
