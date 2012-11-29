package node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import common.Request;
import common.Resource;

public class TPCCoordinator extends Node {
	private class RoundStatus {
		Resource resource;
		Request request;
	}
	
	private Map<String, String> resourcePaxosLeaderMap;
	
	public TPCCoordinator(String nodeId, String fileName) throws IOException {
		//TPCCoordinator does not handle any resource.
		super(nodeId, "");
		this.resourcePaxosLeaderMap = new HashMap<String, String>();
	}
	
	public void AddResourcePaxosLeaderMapping(String paxosLeaderId, String resourceName){
		this.resourcePaxosLeaderMap.put(resourceName, paxosLeaderId);		
	}
	
}
