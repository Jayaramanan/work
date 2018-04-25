/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class GroupSetting implements Setting, Serializable{
	private static final long serialVersionUID = 1L;

	private Group group;
	private String section;
	private String prop;
	private String value;
	private boolean isNew;

	public GroupSetting(){

	}

	public GroupSetting(Group g, String section, String prop, String value){
		this.group = g;
		this.section = section;
		this.prop = prop;
		this.value = value;
	}

	public void setNew(boolean isNew){
		this.isNew = isNew;
	}

	public boolean isNew(){
		return isNew;
	}

	public Group getGroup(){
		return group;
	}

	public void setGroup(Group group){
		this.group = group;
	}

	public String getSection(){
		return section;
	}

	public void setSection(String section){
		this.section = section;
	}

	public String getProp(){
		return prop;
	}

	public void setProp(String prop){
		this.prop = prop;
	}

	public String getValue(){
		return value;
	}

	public void setValue(String value){
		this.value = value;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof GroupSetting))
			return false;
		if (o == this)
			return true;
		GroupSetting gs = (GroupSetting) o;
		if (getGroup() == null || gs.getGroup() == null)
			return false;
		if (getProp() == null || gs.getProp() == null)
			return false;
		if (getSection() == null || gs.getSection() == null)
			return false;
		if (getValue() == null || gs.getValue() == null)
			return false;
		return getGroup().equals(gs.getGroup()) && getSection().equals(gs.getSection()) && getProp().equals(gs.getProp())
		        && getValue().equals(gs.getValue());
	}

}
