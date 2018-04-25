package com.ni3.ag.navigator.server.domain;

public class EdgeScope{
	private int edgeId;
	private int groupId;
	private String flag;

	public EdgeScope(int edgeId, int groupId, String flag){
		this.edgeId = edgeId;
		this.groupId = groupId;
		this.flag = flag;
	}

	public int getEdgeId(){
		return edgeId;
	}

	public void setEdgeId(int edgeId){
		this.edgeId = edgeId;
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
