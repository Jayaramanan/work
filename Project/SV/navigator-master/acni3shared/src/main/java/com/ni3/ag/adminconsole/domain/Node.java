/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.math.BigDecimal;

public class Node implements java.io.Serializable{

	private static final long serialVersionUID = 413855988807685748L;
	public static final String OBJECT_DEFINITION_ID = "objectDefinition.id";
	private Integer id;
	private ObjectDefinition objectDefinition;
	private BigDecimal lon;
	private BigDecimal lat;
	private String iconName;

	public Node(){
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getIconName(){
		return iconName;
	}

	public void setIconName(String iconName){
		this.iconName = iconName;
	}

	public BigDecimal getLat(){
		return lat;
	}

	public void setLat(BigDecimal lat){
		this.lat = lat;
	}

	public BigDecimal getLon(){
		return lon;
	}

	public void setLon(BigDecimal lon){
		this.lon = lon;
	}

	public ObjectDefinition getObjectDefinition(){
		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition){
		this.objectDefinition = objectDefinition;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj)
			return true;
		if (getId() == null || !(obj instanceof Node))
			return false;
		return getId().equals(((Node) obj).getId());
	}
}
