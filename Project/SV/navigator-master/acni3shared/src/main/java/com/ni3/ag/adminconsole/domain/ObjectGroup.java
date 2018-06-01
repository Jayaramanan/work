/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ObjectGroup implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public static final String OBJECT = "object";
	public static final String GROUP = "group";

	private ObjectDefinition object;
	private Group group;
	private Integer canRead_;
	private Integer canCreate_;
	private Integer canUpdate_;
	private Integer canDelete_;

	ObjectGroup(){
	}

	public ObjectGroup(ObjectDefinition od, Group group){
		setObject(od);
		setGroup(group);
	}

	public ObjectDefinition getObject(){
		return object;
	}

	public void setObject(ObjectDefinition object){
		this.object = object;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public Boolean isCanRead(){
		return getCanRead_() != null && getCanRead_() == 1;
	}

	public void setCanRead(Boolean canRead){
		setCanRead_(canRead ? 1 : 0);
	}

	public Boolean isCanCreate(){
		return getCanCreate_() != null && getCanCreate_() == 1;
	}

	public void setCanCreate(Boolean canCreate){
		setCanCreate_(canCreate ? 1 : 0);
	}

	public Boolean isCanUpdate(){
		return getCanUpdate_() != null && getCanUpdate_() == 1;
	}

	public void setCanUpdate(Boolean canUpdate){
		setCanUpdate_(canUpdate ? 1 : 0);
	}

	public Boolean isCanDelete(){
		return getCanDelete_() != null && getCanDelete_() == 1;
	}

	public void setCanDelete(Boolean canDelete){
		setCanDelete_(canDelete ? 1 : 0);
	}

	private Integer getCanRead_(){
		return canRead_;
	}

	private void setCanRead_(Integer canRead){
		canRead_ = canRead;
	}

	private Integer getCanCreate_(){
		return canCreate_;
	}

	private void setCanCreate_(Integer canCreate){
		canCreate_ = canCreate;
	}

	private Integer getCanUpdate_(){
		return canUpdate_;
	}

	private void setCanUpdate_(Integer canUpdate){
		canUpdate_ = canUpdate;
	}

	private Integer getCanDelete_(){
		return canDelete_;
	}

	private void setCanDelete_(Integer canDelete){
		canDelete_ = canDelete;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof ObjectGroup))
			return false;
		if (o == this)
			return true;
		ObjectGroup oug = (ObjectGroup) o;
		if (getObject() == null || oug.getObject() == null)
			return false;
		if (getGroup() == null || oug.getGroup() == null)
			return false;
		return getGroup().equals(oug.getGroup()) && getObject().equals(oug.getObject());
	}

	public ObjectGroup clone() throws CloneNotSupportedException{
		return (ObjectGroup) super.clone();
	}

	public ObjectGroup clone(ObjectDefinition object, Group group) throws CloneNotSupportedException{
		ObjectGroup oug = clone();
		oug.setObject(object);
		oug.setGroup(group);
		return oug;
	}
}
