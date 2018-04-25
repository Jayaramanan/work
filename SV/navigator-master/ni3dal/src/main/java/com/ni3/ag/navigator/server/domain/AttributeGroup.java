package com.ni3.ag.navigator.server.domain;

import java.io.Serializable;

public class AttributeGroup implements Serializable{

	private Attribute attribute;
	private Integer groupId;
	private Integer canRead_;
	private Integer editingLock;
	private Integer editingUnlock;

	public Attribute getAttribute(){
		return attribute;
	}

	public void setAttribute(Attribute attribute){
		this.attribute = attribute;
	}

	public Integer getCanRead_(){
		return canRead_;
	}

	public void setCanRead_(Integer canRead_){
		this.canRead_ = canRead_;
	}

	public Boolean getCanRead(){
		return canRead_ != null && canRead_ != 0;
	}

	public Integer getEditingLock(){
		return editingLock != null ? editingLock : 0;
	}

	public Integer getEditingUnlock(){
		return editingUnlock != null ? editingUnlock : 0;
	}

	public Integer getGroupId(){
		return groupId;
	}

	public void setCanRead(final Boolean canRead){
		this.canRead_ = canRead != null && canRead ? 1 : 0;
	}

	public void setEditingLock(final Integer editingLock){
		this.editingLock = editingLock;
	}

	public void setEditingUnlock(final Integer editingUnlock){
		this.editingUnlock = editingUnlock;
	}

	public void setGroupId(final Integer groupId){
		this.groupId = groupId;
	}

	@Override
	public String toString(){
		return "AttributeGroup [attributeId=" + attribute.getId() + ", groupId=" + groupId + ", canRead=" + getCanRead()
		        + ", editingLock=" + editingLock + ", editingUnlock=" + editingUnlock + "]";
	}

}
