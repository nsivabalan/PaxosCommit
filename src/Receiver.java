import java.io.IOException;



import common.*;

public class Receiver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Common.RMQServer = "ec2-174-129-81-226.compute-1.amazonaws.com";		
		
		try {
			String[] exchanges = {"test_bcast"};
			String nodeId = args[1];
			RMQReceiverNew receiver = new RMQReceiverNew(exchanges, nodeId);
			
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