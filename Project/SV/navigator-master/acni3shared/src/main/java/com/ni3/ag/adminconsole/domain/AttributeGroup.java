/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class AttributeGroup implements Serializable, Cloneable{

	private static final long serialVersionUID = 1746477158384127564L;

	private ObjectAttribute objectAttribute;
	private Group group;
	private Integer canRead_;
	private Integer editingOption_;
	private Integer editingOptionLocked_;

	AttributeGroup(){
	}

	public AttributeGroup(ObjectAttribute attr, Group group){
		setObjectAttribute(attr);
		setGroup(group);
	}

	public ObjectAttribute getObjectAttribute(){
		return objectAttribute;
	}

	public void setObjectAttribute(ObjectAttribute objectAttribute){
		this.objectAttribute = objectAttribute;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public Boolean isCanRead(){
		return getCanRead_() != null && getCanRead_().equals(1);
	}

	public void setCanRead(Boolean canRead){
		setCanRead_(canRead ? 1 : 0);
	}

	public EditingOption getEditingOption(){
		return EditingOption.fromValue(getEditingOption_());
	}

	public void setEditingOption(EditingOption option){
		setEditingOption_(option != null ? option.getValue() : 0);
	}

	private Integer getCanRead_(){
		return canRead_;
	}

	private void setCanRead_(Integer canRead){
		canRead_ = canRead;
	}

	public Integer getEditingOption_(){
		return editingOption_;
	}

	public void setEditingOption_(Integer editingOption){
		editingOption_ = editingOption;
	}

	public Integer getEditingOptionLocked_(){
		return editingOptionLocked_;
	}

	public void setEditingOptionLocked_(Integer editingOptionLocked){
		editingOptionLocked_ = editingOptionLocked;
	}

	public AttributeGroup clone() throws CloneNotSupportedException{
		return (AttributeGroup) super.clone();
	}

	public EditingOption getEditingOptionLocked(){
		return EditingOption.fromValue(getEditingOptionLocked_());
	}

	public void setEditingOptionLocked(EditingOption canUpdateLocked){
		setEditingOptionLocked_(canUpdateLocked != null ? canUpdateLocked.getValue() : 0);
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof AttributeGroup))
			return false;
		if (o == this)
			return true;
		AttributeGroup ag = (AttributeGroup) o;
		if (getGroup() != null && ag.getGroup() != null && !getGroup().equals(ag.getGroup()))
			return false;
		if (getGroup() != null && ag.getGroup() == null)
			return false;
		if (getGroup() == null && ag.getGroup() != null)
			return false;
		if (getObjectAttribute() != null && ag.getObjectAttribute() != null
		        && !getObjectAttribute().equals(ag.getObjectAttribute()))
			return false;
		if (getObjectAttribute() == null && ag.getObjectAttribute() != null)
			return false;
		if (getObjectAttribute() != null && ag.getObjectAttribute() == null)
			return false;

		return true;
	}

	public AttributeGroup clone(ObjectAttribute oa, Group group) throws CloneNotSupportedException{
		AttributeGroup ag = clone();
		ag.setObjectAttribute(oa);
		ag.setGroup(group);
		return ag;
	}
}
