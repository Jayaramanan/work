/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

public class NodeConnectionMergeRow{

	private DBObject edge;
	private DBObject node;
	private boolean selected;

	public NodeConnectionMergeRow(DBObject edge, DBObject node, boolean selected){
		super();
		this.edge = edge;
		this.node = node;
		this.selected = selected;
	}

	public DBObject getEdge(){
		return edge;
	}

	public void setEdge(DBObject edge){
		this.edge = edge;
	}

	public DBObject getNode(){
		return node;
	}

	public void setNode(DBObject node){
		this.node = node;
	}

	public boolean isSelected(){
		return selected;
	}

	public void setSelected(boolean selected){
		this.selected = selected;
	}

}
