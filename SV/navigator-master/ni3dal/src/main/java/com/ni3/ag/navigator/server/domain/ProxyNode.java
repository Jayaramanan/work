package com.ni3.ag.navigator.server.domain;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.services.NodeLoader;

public class ProxyNode extends Node{
	private NodeLoader loader;
	private boolean inited;

	public ProxyNode(int id, NodeLoader loader){
		this.ID = id;
		this.loader = loader;
		setInEdges(new ArrayList());
		setOutEdges(new ArrayList());
	}

	private void checkInited(){
		if (!inited)
			loader.loadNode(ID, this);
		inited = true;
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
	public List<Edge> getOutEdges(){
		checkInited();
		return super.getOutEdges();
	}

	@Override
	public List<Edge> getInEdges(){
		checkInited();
		return super.getInEdges();
	}

	@Override
	public int getEdgeCount(){
		checkInited();
		return super.getEdgeCount();
	}
}
