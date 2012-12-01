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
			String[] exchanges = {"test_direct"};
			String nodeId = args[1];
			RMQReceiver receiver = new RMQReceiver(exchanges, nodeId);
			
			while(true)
			{	
				MessageWrapper rMessage = receiver.ReceiveMessage();			
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}