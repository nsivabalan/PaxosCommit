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
			RMQSender sender = new RMQSender("Broadcast");
			RMQReceiver receiver = new RMQReceiver("Broadcast", true);
			int i = 20;
			while(i < 30)
			{
				MessageWrapper message = new MessageWrapper("HelloWorld "+i, "ClassName");
				sender.SendMessage(message);
				i++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
