package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;

public class ObjectDefinitionGroup implements Serializable{
	private ObjectDefinition object;
	private Integer groupId;
	private boolean canRead;
	private boolean canCreate;
	private boolean canUpdate;
	private boolean canDelete;

	public int getCanDelete_(){
		return canDelete ? 1 : 0;
	}

	public void setCanDelete_(int canDelete_){
		this.canDelete = canDelete_ != 0;
	}

	public int getCanUpdate_(){
		return canUpdate ? 1 : 0;
	}

	public void setCanUpdate_(int canUpdate_){
		this.canUpdate = canUpdate_ != 0;
	}

	public int getCanCreate_(){
		return canCreate ? 1 : 0;
	}

	public void setCanCreate_(int canCreate_){
		this.canCreate = canCreate_ != 0;
	}

	public int getCanRead_(){
		return canRead ? 1 : 0;
	}

	public void setCanRead_(int canRead_){
		this.canRead = canRead_ != 0;
	}

	public ObjectDefinition getObject(){
		return object;
	}

	public void setObject(ObjectDefinition object){
		this.object = object;
	}

	public Integer getGroupId(){
		return groupId;
	}

	public boolean isCanCreate(){
		return canCreate;
	}

	public void setCanCreate(boolean canCreate){
		this.canCreate = canCreate;
	}

	public boolean isCanUpdate(){
		return canUpdate;
	}

	public void setCanUpdate(boolean canUpdate){
		this.canUpdate = canUpdate;
	}

	public boolean isCanDelete(){
		return canDelete;
	}

	public void setCanDelete(boolean canDelete){
		this.canDelete = canDelete;
	}

	public void setGroupId(Integer groupId){
		this.groupId = groupId;
	}

	public boolean isCanRead(){
		return canRead;
	}

	public void setCanRead(boolean canRead){
		this.canRead = canRead;
	}

	public void setCanReadInt(int canRead){
		this.canRead = !(canRead == 0);
	}

}
