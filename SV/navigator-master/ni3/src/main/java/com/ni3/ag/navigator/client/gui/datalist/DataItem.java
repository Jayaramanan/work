/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.datalist;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.gui.graph.Node;

public class DataItem{
	private boolean isChecked;
	public DBObject obj;
	private Node node;

	public DataItem(DBObject obj, boolean isChecked){
		this.obj = obj;
		this.isChecked = isChecked;
	}

	public String toString(){
		return obj.toString();
	}

	public boolean isChecked(){
		return isChecked;
	}

	public void setChecked(boolean value){
		isChecked = value;
	}

	public void setNode(Node node){
		this.node = node;
	}

	public boolean isDisplayed(){
		return isChecked && node != null && node.isActive() && !node.filteredOutByChartAF;
	}
	
	public Node getNode(){
		return node;
	}

}
