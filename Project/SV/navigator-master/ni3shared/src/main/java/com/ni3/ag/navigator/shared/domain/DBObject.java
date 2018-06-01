package com.ni3.ag.navigator.shared.domain;

import java.util.Map;

public class DBObject{
	private int id;
	private int entityId;
	private NodeMetaphor metaphor;
	private Map<Integer, String> data;

	public DBObject(int id, int entityId){
		this.id = id;
		this.entityId = entityId;
	}

	public void setData(Map<Integer, String> data){
		this.data = data;
	}

	public int getId(){
		return id;
	}

	public int getEntityId(){
		return entityId;
	}

	public NodeMetaphor getMetaphor(){
		return metaphor;
	}

	public void setMetaphor(NodeMetaphor metaphor){
		this.metaphor = metaphor;
	}

	public Map<Integer, String> getData(){
		return data;
	}
}
