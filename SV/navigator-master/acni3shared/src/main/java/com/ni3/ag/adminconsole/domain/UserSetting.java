/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class UserSetting implements Setting, Serializable{
	private static final long serialVersionUID = 1L;

	private User user;
	private String section;
	private String prop;
	private String value;
	private boolean isNew;

	public UserSetting(){

	}

	public UserSetting(User u, String section, String prop, String value){
		this.user = u;
		this.section = section;
		this.prop = prop;
		this.value = value;
	}

	public void setNew(boolean isNew){
		this.isNew = isNew;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
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

	public boolean isNew(){
		return isNew;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof UserSetting))
			return false;
		if (o == this)
			return true;
		UserSetting us = (UserSetting) o;
		if (getUser() == null || us.getUser() == null)
			return false;
		if (getSection() == null || us.getSection() == null)
			return false;
		if (getProp() == null || us.getProp() == null)
			return false;
		return getUser().equals(us.getUser()) && getSection().equals(us.getSection()) && getProp().equals(us.getProp());
	}

	public String toString(){
		return "[ " + section + " | " + prop + " : " + value + " ]" + " new: " + isNew + " @user: " + user;
	}
}
