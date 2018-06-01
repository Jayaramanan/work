/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class UserDataModel extends AbstractModel{

	private Integer nodeCount;
	private Integer edgeCount;

	public Integer getNodeCount(){
		return nodeCount;
	}

	public void setNodeCount(Integer count){
		this.nodeCount = count;
	}

	public Integer getEdgeCount(){
		return edgeCount;
	}

	public void setEdgeCount(Integer count){
		this.edgeCount = count;
	}

}
