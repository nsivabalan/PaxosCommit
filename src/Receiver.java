import java.io.IOException;



import common.*;

public class Receiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Common.RMQServer = "localhost";		
		
		try {
			RMQReceiver receiver = new RMQReceiver("Broadcast", true);
			while(true)
			{	
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