/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class Context implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private ObjectDefinition objectDefinition;
	private String name;
	private String tableName;
	private ObjectAttribute pkAttribute;
	private List<ContextAttribute> contextAttributes;

	public Context(){
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public ObjectDefinition getObjectDefinition(){
		return objectDefinition;
	}

	public void setObjectDefinition(ObjectDefinition objectDefinition){
		this.objectDefinition = objectDefinition;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getTableName(){
		return tableName;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public ObjectAttribute getPkAttribute(){
		return pkAttribute;
	}

	public void setPkAttribute(ObjectAttribute pkAttribute){
		this.pkAttribute = pkAttribute;
	}

	public List<ContextAttribute> getContextAttributes(){
		return contextAttributes;
	}

	public void setContextAttributes(List<ContextAttribute> contextAttributes){
		this.contextAttributes = contextAttributes;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof Context))
			return false;
		if (o == this)
			return true;
		Context dt = (Context) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

}
