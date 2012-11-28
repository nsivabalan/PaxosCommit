import java.io.IOException;

import javax.sound.midi.Receiver;

import common.*;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Common.RMQServer = "169.231.111.161";
		
		
		try {
			RMQSender sender = new RMQSender("Broadcast");
			RMQReceiver receiver = new RMQReceiver("Broadcast", true);
			int i = 0;
			while(true)
			{
				Message message = new Message("HelloWorld "+i, "ClassName");
				sender.SendMessage(message);
				Message rMessage = receiver.ReceiveMessage();				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
