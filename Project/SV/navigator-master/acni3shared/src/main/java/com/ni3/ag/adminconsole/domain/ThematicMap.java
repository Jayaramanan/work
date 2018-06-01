package com.ni3.ag.adminconsole.domain;

import java.util.List;

public class ThematicMap{
	public static final String ID = "id";

	private int id;
	private String name;
	private ThematicFolder folder;
	private Group group;
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

	public ThematicFolder getFolder(){
		return folder;
	}

	public void setFolder(ThematicFolder folder){
		this.folder = folder;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
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