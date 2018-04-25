/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.util.Map;

public class NodeMetaphor{
	private Map<String, MetaphorIcon> metaphors;
	private MetaphorIcon assignedMetaphor;

	public NodeMetaphor(){
	}

	public MetaphorIcon getAssignedMetaphor(){
		return assignedMetaphor;
	}

	public void setAssignedMetaphor(MetaphorIcon assignedMetaphor){
		this.assignedMetaphor = assignedMetaphor;
	}

	public void setMetaphors(Map<String, MetaphorIcon> metaphors){
		this.metaphors = metaphors;
	}

	public Map<String, MetaphorIcon> getMetaphors(){
		return metaphors;
	}

	public MetaphorIcon getMetaphor(String metaphorSet){
		if (assignedMetaphor != null){
			return assignedMetaphor;
		} else{
			return metaphors.get(metaphorSet);
		}
	}
}
