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

	public static void main(String args[]) throws Exception
	{
		String nodeid=args[0];
		String leaderOne=args[1];
		String leaderTwo=args[2];
		Client clientobj=new Client(nodeid, leaderOne, leaderTwo);
		new Thread(clientobj).start();
		clientobj.ProcessInput();
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
			System.out.println("Enter an data ");
			request = in.nextLine();
			System.out.println("Enter an dest id(1 or 2 ");
			destid = in.nextInt();
			
			if(requesttype.equals("read")){
				msg= new ClientOpMsg(this.nodeId, Common.ClientOPMsgType.READ, (destid==0)?this.paxosLeaderOneId:this.paxosLeaderTwoId);
				this.sendClientOpMsg(msg, this.paxosLeaderOneId);
			}
			else if (request.equals("append")){
				msg = new ClientOpMsg(this.nodeId, Common.ClientOPMsgType.APPEND, (destid==0)?this.paxosLeaderOneId:this.paxosLeaderTwoId);
				msg.setData(request);
				this.sendClientOpMsg(msg, this.paxosLeaderOneId);
			}

		Thread.sleep(60000);
		}
	}

	public void sendClientOpMsg(ClientOpMsg msg,String destid) throws IOException
	{

		messageController.SendMessage(Common.CreateMessageWrapper(msg), Common.DirectMessageExchange, destid);
	}

	public void run(){

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
