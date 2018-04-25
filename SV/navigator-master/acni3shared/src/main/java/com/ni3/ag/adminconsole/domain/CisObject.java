/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

public class CisObject implements java.io.Serializable{

	private static final long serialVersionUID = -7443048841230853080L;
	public static final String ID = "id";

	private Integer id;
	private Integer status;
	private Integer userId;
	private ObjectDefinition objectType;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getStatus(){
		return status;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getUserId(){
		return userId;
	}

	public void setUserId(Integer userId){
		this.userId = userId;
	}

	public ObjectDefinition getObjectType(){
		return objectType;
	}

	public void setObjectType(ObjectDefinition objectType){
		this.objectType = objectType;
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (getId() == null || !(obj instanceof CisObject)){
			return false;
		}

		return getId().equals(((CisObject) obj).getId());
	}
}
