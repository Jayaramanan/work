package com.ni3.ag.navigator.server.domain;

import com.ni3.ag.navigator.server.services.EdgeLoader;

public class ProxyEdge extends Edge{
	private EdgeLoader loader;
	private boolean inited;

	public ProxyEdge(int edgeID, EdgeLoader loader){
		ID = edgeID;
		this.loader = loader;
	}

	@Override
	public int getFavoriteId(){
		checkInited();
		return super.getFavoriteId();
	}

	@Override
	public int getType(){
		checkInited();
		return super.getType();
	}

	@Override
	public int getStatus(){
		checkInited();
		return super.getStatus();
	}

	@Override
	public int getDirected(){
		checkInited();
		return super.getDirected();
	}

	@Override
	public int getConnectionType(){
		checkInited();
		return super.getConnectionType();
	}

	@Override
	public float getStrength(){
		checkInited();
		return super.getStrength();
	}

	@Override
	public int getInPath(){
		checkInited();
		return super.getInPath();
	}

	@Override
	public int getCreatorUser(){
		checkInited();
		return super.getCreatorUser();
	}

	@Override
	public int getCreatorGroup(){
		checkInited();
		return super.getCreatorGroup();
	}

	@Override
	public boolean isContextEdge(){
		checkInited();
		return super.isContextEdge();
	}

	@Override
	public Node getToNode(){
		checkInited();
		return super.getToNode();
	}

	@Override
	public Node getFromNode(){
		checkInited();
		return super.getFromNode();
	}

	private void checkInited(){
		if(!inited)
			loader.loadEdge(ID, this);
		inited = true;
	}

//	@Override
//	public boolean equals(Object o){
//		if (this == o) return true;
//		if (!(o instanceof ProxyEdge)) return false;
//
//		ProxyEdge proxyEdge = (ProxyEdge) o;
//		return proxyEdge.ID == ID;
//	}
//
//	@Override
//	public int hashCode(){
//		return ID;
//	}
}
