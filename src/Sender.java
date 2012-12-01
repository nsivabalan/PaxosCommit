import java.io.IOException;

import javax.sound.midi.Receiver;

import common.*;

public class Sender {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Common.RMQServer = "ec2-174-129-81-226.compute-1.amazonaws.com";
		
		
		try {
			String exchange = "test_bcast";
			RMQSenderNew sender = new RMQSenderNew();
			sender.DeclareExchange(exchange, "fanout");
			
			int i = 20;
			while(i < 30)
			{
				MessageWrapper message = new MessageWrapper("HelloWorld "+i, MessageWrapper.class);
				System.out.println("Send");
				sender.SendMessage(message, exchange, "");
				i++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
