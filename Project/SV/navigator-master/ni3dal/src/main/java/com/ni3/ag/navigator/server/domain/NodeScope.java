package com.ni3.ag.navigator.server.domain;

public class NodeScope{
	private int nodeId;
	private int groupId;
	private String flag;

	public NodeScope(int nodeId, int groupId, String flag){
		this.nodeId = nodeId;
		this.groupId = groupId;
		this.flag = flag;
	}

	public int getNodeId(){
		return nodeId;
	}

	public void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}

	public int getGroupId(){
		return groupId;
	}

	public void setGroupId(int groupId){
		this.groupId = groupId;
	}

	public String getFlag(){
		return flag;
	}

	public void setFlag(String flag){
		this.flag = flag;
	}

}
