package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import message.ClientOpMsg;
import common.Common;
import common.MessageWrapper;
import common.Tuple;

public class Client extends Node implements Runnable{
	private String paxosLeaderOneId;
	private String paxosLeaderTwoId;

	public Client(String nodeId,String paxosLeaderOneId,String paxosLeaderTwoId) throws IOException {		
		super(nodeId, "");
		this.paxosLeaderOneId=paxosLeaderOneId;
		this.paxosLeaderTwoId=paxosLeaderTwoId;
		ArrayList<Tuple<String, String>> exchanges = new ArrayList<Tuple<String, String>>();
		exchanges.add(new Tuple(Common.DirectMessageExchange, Common.directExchangeType));
		this.DeclareExchanges(exchanges);
		this.InitializeConsumer();
	}

	public void ProcessInput() throws IOException, InterruptedException
	{
		String requesttype,request;
		int destid;
		ClientOpMsg msg;
		while(true)
		{
			Scanner in = new Scanner(System.in);			 
			System.out.println("Enter a reqeust type (read/append) ");
			requesttype = in.nextLine();
			
			if(requesttype.equals("read")){
				System.out.println("Enter a dest id(1 or 2) ");
				destid = in.nextInt();
				
				msg= new ClientOpMsg(this.nodeId, Common.ClientOPMsgType.READ, (destid==1)?this.paxosLeaderOneId:this.paxosLeaderTwoId,java.util.UUID.randomUUID());
				this.sendClientOpMsg(msg, (destid==1)?this.paxosLeaderOneId:this.paxosLeaderTwoId);
			}
			else if (requesttype.equals("append")){
				System.out.println("Enter data ");
				request = in.nextLine();				
				msg = new ClientOpMsg(this.nodeId, Common.ClientOPMsgType.APPEND, request, java.util.UUID.randomUUID());
				//msg.setData(request);
				this.sendClientOpMsg(msg, this.paxosLeaderOneId);
				//msg.setNodeid(paxosLeaderTwoId);
				this.sendClientOpMsg(msg, this.paxosLeaderTwoId);
			}

		Thread.sleep(600);
		}
	}

	public void sendClientOpMsg(ClientOpMsg msg, String destid) throws IOException
	{
		messageController.SendMessage(Common.CreateMessageWrapper(msg), Common.DirectMessageExchange, destid);
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
