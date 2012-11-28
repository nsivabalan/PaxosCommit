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
	private Boolean broadcast;
	
	public RMQReceiver(String exchangeName, Boolean broadcast) throws IOException{
		this.broadcast = broadcast;
		this.EXCHANGE_NAME = exchangeName;
		factory = new ConnectionFactory();
		factory.setHost(Common.RMQServer);
		connection = factory.newConnection();
		channel = connection.createChannel();		
		if (this.broadcast)
		{
			channel.exchangeDeclare(this.EXCHANGE_NAME, "fanout");
		}
	}
	
	public Message ReceiveMessage() throws IOException, InterruptedException
	{
		String queueName;
		QueueingConsumer consumer = new QueueingConsumer(channel);
		
		if(this.broadcast)
		{
			queueName = channel.queueDeclare().getQueue();
			channel.queueBind(queueName, this.EXCHANGE_NAME, "fanout");
			channel.basicConsume(queueName, true, consumer);
		}
		else
		{
			channel.queueDeclare(this.EXCHANGE_NAME, false, false, false, null);
			channel.basicConsume(this.EXCHANGE_NAME, true, consumer);
		}
		
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		System.out.println("Received Message "+ delivery.getBody().toString());
		return Message.getDeSerializedMessage(delivery.getBody().toString());
		
	}
}
