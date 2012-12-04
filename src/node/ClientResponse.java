package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import message.ClientOpMsg;
import common.Common;
import common.MessageWrapper;
import common.Triplet;
import common.Tuple;

public class ClientResponse extends Node implements Runnable{
	private String paxosLeaderOneId;
	private String paxosLeaderTwoId;

	public ClientResponse(String nodeId,String paxosLeaderOneId,String paxosLeaderTwoId) throws IOException {		
		super(nodeId, "");
		this.paxosLeaderOneId=paxosLeaderOneId;
		this.paxosLeaderTwoId=paxosLeaderTwoId;
		ArrayList<Triplet<String, String, Boolean>> exchanges = new ArrayList<Triplet<String, String, Boolean>>();
		exchanges.add(new Triplet(Common.DirectMessageExchange, Common.directExchangeType, true));
		this.DeclareExchanges(exchanges);
		this.InitializeConsumer();
	}

		public void run(){
		System.out.println("Inside run");
		while (true) {
			MessageWrapper msgwrap;
			try {
				msgwrap = messageController.ReceiveMessage();
			
				if(msgwrap.getmessageclass().equals("ClientOpMsg"))
				{
					ClientOpMsg msg = (ClientOpMsg) msgwrap.getDeSerializedInnerMessage();
					ProcessClientResponseData(msg);
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
		}		
	}


	public void ProcessClientResponseData(ClientOpMsg msg)
	{
		System.out.println(" ---------------  Data received --------------- ");
		System.out.println("Source "+msg.getNodeid());
		System.out.println("Data "+msg.getData());
	}

}
