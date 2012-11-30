package node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import common.Common;
import common.RMQReceiver;
import common.RMQSender;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.*;

final class TransactionStatus {
	Integer lsn;
	Integer gsn;
	Common.PaxosLeaderState state;
	Set<String> AcceptorList;
}

public class PaxosLeader extends Node{
	
	private static int lsnCounter = 0;
	
	private RMQSender bcastQueueSender;
	private RMQReceiver tpcBcastQueueReceiver;
		
	private BidiMap dataLSNMap;
	
	
	private Map<Integer, TransactionStatus> lsnTransactionStatusMap;
	
	public PaxosLeader(String nodeId, String fileName, String bcastQueueName, String tpcBcastQueueName) throws IOException {		
		super(nodeId, fileName);
		this.bcastQueueSender = new RMQSender(bcastQueueName);
		this.tpcBcastQueueReceiver = new RMQReceiver(tpcBcastQueueName, true);
		
		this.dataLSNMap = new DualHashBidiMap();		
		
		this.lsnTransactionStatusMap = new HashMap<Integer, TransactionStatus>();
	}
	
	//Use this method to generate a new LSN value for each new message.
	public int getLSNfromMessageData(String data){
		int lsn;
		if(!this.dataLSNMap.containsKey(data))
		{
			lsn = getNewLSN();
			this.dataLSNMap.put(data, lsn);			
		}
		else
		{
			lsn = (Integer) this.dataLSNMap.get(data);
			//check the status of the lsn. 
			//if it is not in the status table
			//then overwrite lsn value with new lsn.
			//else reply with -1.
		}
		return lsn;
	}
	
	// Update LSN after receiving commit response from 2PC.
	public void processLSNStatusRequest(int lsn, int gsn)
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.gsn = gsn;
		this.lsnTransactionStatusMap.put(lsn, temp);
	}
	
	// Update Acceptor List on receiving ack response for PREPARE Phase from an Acceptor.
	public void processLSNStatusRequest(int lsn, String nodeId)
	{
		TransactionStatus temp = this.lsnTransactionStatusMap.get(lsn);
		temp.AcceptorList.add(nodeId);		
		if (temp.AcceptorList.size() == Common.NoAcceptors)
		{
			temp.state = Common.PaxosLeaderState.ACCEPT;
			
		}
		this.lsnTransactionStatusMap.put(lsn, temp);
	}
	
	public void SendInfoMessageToTPCCoordinator(int lsn)
	{
		String msg = (String) this.dataLSNMap.getKey(lsn);
		//TODO: Send Info Message to TPC. 
		
	}
	
	
	public void ProcessAbortMessage(int lsn)
	{
		/*
		 * 0. Broadcast Message to all Acceptors.
		 * 
		 */
	}
	
	public void ProcessAbortAckMessage(int lsn)
	{
		/*
		 * 1. Delete entry from Data-LSN Map
		 * 2. Delete entry from LSN-Status Map
		 */
	}
	
	
	private static int getNewLSN(){
		return lsnCounter++;
	}
}
