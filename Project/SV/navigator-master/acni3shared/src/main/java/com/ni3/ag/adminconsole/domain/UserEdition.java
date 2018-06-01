/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class UserEdition implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private User user;
	private String edition;
	private String checksum;
	private Integer expiring_;
	private boolean toDelete = false;

	UserEdition(){
	}

	public UserEdition(User user, String edition, String checksum){
		this.user = user;
		this.edition = edition;
		this.checksum = checksum;
		this.setExpiring_(0);
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public void setToDelete(boolean b){
		this.toDelete = b;
	}

	public boolean isToDelete(){
		return toDelete;
	}

	private void setExpiring_(Integer expiring_){
		this.expiring_ = expiring_;
	}

	private Integer getExpiring_(){
		return expiring_;
	}

	public boolean isExpiring(){
		return getExpiring_() != null && getExpiring_() == 1;
	}

	public void setIsExpiring(boolean b){
		if (b)
			expiring_ = 1;
		else
			expiring_ = 0;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
	}

	public String getEdition(){
		return edition;
	}

	public void setEdition(String edition){
		this.edition = edition;
	}

	public String getChecksum(){
		return checksum;
	}

	public void setChecksum(String checksum){
		this.checksum = checksum;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof UserEdition))
			return false;

		UserEdition oc = (UserEdition) o;
		if (oc.getId() == null || getId() == null)
			return false;
		return getId().equals(oc.getId());
	}
}
