import java.io.IOException;

import javax.sound.midi.Receiver;

import common.*;

public class Sender {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Common.RMQServer = "localhost";
		
		
		try {
			String exchange = "test_direct";
			MessageController sender = new MessageController();
			sender.DeclareExchange(exchange, "direct");
			
			int i = 30;
			while(i < 40)
			{
				MessageWrapper message = new MessageWrapper("HelloWorld "+i, MessageWrapper.class);
				System.out.println("Send");
				sender.SendMessage(message, exchange, "node3");
				i++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
