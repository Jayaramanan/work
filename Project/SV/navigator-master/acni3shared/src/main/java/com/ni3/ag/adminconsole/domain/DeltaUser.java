/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;

public class DeltaUser implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String TARGET_USER = "targetUser";
	public static final String ID = "id";
	public static final String PROCESSED = "processed";

	private Long id;
	private Date created;
	private DeltaHeader deltaHeader;
	private User targetUser;
	private Integer processed;

	public Long getId(){
		return id;
	}

	public void setId(Long id){
		this.id = id;
	}

	public Date getCreated(){
		return created;
	}

	public void setCreated(Date created){
		this.created = created;
	}

	public DeltaHeader getDeltaHeader(){
		return deltaHeader;
	}

	public void setDeltaHeader(DeltaHeader deltaHeader){
		this.deltaHeader = deltaHeader;
	}

	public User getTargetUser(){
		return targetUser;
	}

	public void setTargetUser(User targetUser){
		this.targetUser = targetUser;
	}

	public Integer getProcessed(){
		return processed;
	}

	public void setProcessed(Integer processed){
		this.processed = processed;
	}

}
