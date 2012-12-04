import java.io.IOException;

import common.Common;

import node.Acceptor;
import node.Client;
import node.PaxosLeader;
import node.TPCCoordinator;
import node.Node;


public class Deploy {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		// TODO Auto-generated method stub
		
		Common.RMQServer = "localhost";
		Common.FilePath = "/home/sivabalan/deploy";
		
		String deployNodeId = args[0];
		String deployNodeType = args[1];
		
		System.out.println(deployNodeId);
		
		if( deployNodeType.equals("Acceptor"))
		{
			String paxosLeaderId = args[2];
			String file = args[3];
			Acceptor node = new Acceptor(deployNodeId, file, paxosLeaderId);
			System.out.println("Starting Acceptor");
			node.run();
		}
		else if (deployNodeType.equals("PaxosLeader"))
		{
			String tpcCoordinatorId = args[2];
			String file = args[3];
			PaxosLeader node = new PaxosLeader(deployNodeId, file, tpcCoordinatorId);
			node.run();
		}
		else if (deployNodeType.equals("TPCCoordinator"))
		{			
			TPCCoordinator node = new TPCCoordinator(deployNodeId);
			node.run();
		}
		else if (deployNodeType.equals("Client"))
		{			
			String paxosLeader1 = args[2];
			String paxosLeader2 = args[3];
			
			Client node = new Client(deployNodeId, paxosLeader1, paxosLeader2);
			System.out.println("creating thread.");
			new Thread(node).start();
			System.out.println("processing input");
			node.ProcessInput();
		}
		
	}

}
