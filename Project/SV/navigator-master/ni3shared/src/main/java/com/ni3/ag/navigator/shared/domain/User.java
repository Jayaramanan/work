/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.io.Serializable;

public class User implements Serializable{
	private static final long serialVersionUID = 986705444800506151L;

	// TODO: change Integer -> int, and Boolean -> boolean
	private Integer id;
	private String firstName;
	private String lastName;
	private String userName;
	private String password;
	private String SID;
	private String eMail;
	private Boolean active;
	private Boolean hasOfflineClient;
	private String etlUser;
	private String etlPassword;

	public User(){
	}

	public User(int id){
		this.id = id;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getSID(){
		return SID;
	}

	public void setSID(String sID){
		SID = sID;
	}

	public String geteMail(){
		return eMail;
	}

	public void seteMail(String eMail){
		this.eMail = eMail;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getUserName(){
		return userName;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public String toString(){
		return userName + " password:" + password + ": SID:" + SID;
	}

	public String getEtlUser(){
		return etlUser;
	}

	public void setEtlUser(String etlUser){
		this.etlUser = etlUser;
	}

	public String getEtlPassword(){
		return etlPassword;
	}

	public void setEtlPassword(String etlPassword){
		this.etlPassword = etlPassword;
	}

	public Boolean isActive(){
		return active;
	}

	public void setActive(Boolean active){
		this.active = active;
	}

	public Boolean getHasOfflineClient(){
		return hasOfflineClient;
	}

	public void setHasOfflineClient(Boolean hasOfflineClient){
		this.hasOfflineClient = hasOfflineClient;
	}

	public int compareTo(User o){
		if (this.getUserName() == null && (o == null || o.getUserName() == null)){
			return 0;
		} else if (this.getUserName() == null){
			return -1;
		} else if (o == null || o.getUserName() == null){
			return 1;
		}
		return getUserName().compareTo(o.getUserName());
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (getId() == null || !(o instanceof User))
			return false;
		return getId().equals(((User) o).getId());
	}
}
