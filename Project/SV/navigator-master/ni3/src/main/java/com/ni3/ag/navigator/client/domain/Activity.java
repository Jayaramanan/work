/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

import java.util.Date;

import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;

public class Activity{
	private Date dateTime;
	private User user;
	private int objectId;
	private String objectName;
	private DeltaType deltaType;
	private String text;
	private long id;

	public Activity()
	{
	}

	public Activity(Date dateTime, User user, int objectId, String objectName, DeltaType deltaType)
	{
		this.dateTime = dateTime;
		this.objectId = objectId;
		this.objectName = objectName;
		this.deltaType = deltaType;
	}

	public Date getDateTime()
	{
		return dateTime;
	}

	public void setDateTime(Date dateTime)
	{
		this.dateTime = dateTime;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public int getObjectId()
	{
		return objectId;
	}

	public void setObjectId(int objectId)
	{
		this.objectId = objectId;
	}

	public DeltaType getDeltaType()
	{
		return deltaType;
	}

	public String getObjectName()
	{
		return objectName;
	}

	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	public void setDeltaType(DeltaType deltaType)
	{
		this.deltaType = deltaType;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setId(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}
}
