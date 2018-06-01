/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.util.List;

public class ThematicMap{
	private int id;
	private String name;
	private int folderId;
	private int groupId;
	private int layerId;
	private String attribute;
	private List<ThematicCluster> clusters;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public int getFolderId(){
		return folderId;
	}

	public void setFolderId(int folderId){
		this.folderId = folderId;
	}

	public int getGroupId(){
		return groupId;
	}

	public void setGroupId(int groupId){
		this.groupId = groupId;
	}

	public int getLayerId(){
		return layerId;
	}

	public void setLayerId(int layerId){
		this.layerId = layerId;
	}

	public String getAttribute(){
		return attribute;
	}

	public void setAttribute(String attribute){
		this.attribute = attribute;
	}

	public List<ThematicCluster> getClusters(){
		return clusters;
	}

	public void setClusters(List<ThematicCluster> clusters){
		this.clusters = clusters;
	}

}
