package com.ni3.ag.navigator.server.domain;

public class Group{

	private int id;
	private String nodeScope;
	private String edgeScope;

	public Group(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public String getNodeScope(){
		return nodeScope;
	}

	public void setNodeScope(String nodeScope){
		this.nodeScope = nodeScope;
	}

	public String getEdgeScope(){
		return edgeScope;
	}

	public void setEdgeScope(String edgeScope){
		this.edgeScope = edgeScope;
	}
}
