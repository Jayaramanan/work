/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.math.BigDecimal;

public class Edge implements java.io.Serializable{

	private static final long serialVersionUID = -6379753625571549844L;
	public static final String EDGE_TYPE_ID = "edgeType.id";
	private Integer id;
	private Integer fromId;
	private Integer toId;
	private Integer connectionType;
	private BigDecimal strength;
	private Integer directed;
	private Integer inPath;
	private String comment;
	private Integer userId;
	private ObjectDefinition edgeType;

	public Edge(){
	}

	public String getComment(){
		return comment;
	}

	public void setComment(String comment){
		this.comment = comment;
	}

	public Integer getConnectionType(){
		return connectionType;
	}

	public void setConnectionType(Integer connectionType){
		this.connectionType = connectionType;
	}

	public Integer getDirected(){
		return directed;
	}

	public void setDirected(Integer directed){
		this.directed = directed;
	}

	public Integer getFromId(){
		return fromId;
	}

	public void setFromId(Integer fromId){
		this.fromId = fromId;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getInPath(){
		return inPath;
	}

	public void setInPath(Integer inPath){
		this.inPath = inPath;
	}

	public BigDecimal getStrength(){
		return strength;
	}

	public void setStrength(BigDecimal strength){
		this.strength = strength;
	}

	public Integer getToId(){
		return toId;
	}

	public void setToId(Integer toId){
		this.toId = toId;
	}

	public Integer getUserId(){
		return userId;
	}

	public void setUserId(Integer userId){
		this.userId = userId;
	}

	public ObjectDefinition getEdgeType(){
		return edgeType;
	}

	public void setEdgeType(ObjectDefinition edgeType){
		this.edgeType = edgeType;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj)
			return true;
		if (getId() == null || !(obj instanceof Edge))
			return false;
		return getId().equals(((Edge) obj).getId());
	}
}
