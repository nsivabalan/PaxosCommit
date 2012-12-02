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
			String exchange = "test_direct";
			String nodeId = args[1];
			MessageController controller = new MessageController(nodeId);
			controller.DeclareExchange(exchange, "direct");
			controller.InitializeConsumer();
			
			while(true)
			{	
				MessageWrapper rMessage = controller.ReceiveMessage();
				System.out.println("test");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}