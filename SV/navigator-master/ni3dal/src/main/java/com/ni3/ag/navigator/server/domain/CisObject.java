package com.ni3.ag.navigator.server.domain;

import com.ni3.ag.navigator.shared.constants.ObjectStatus;
import java.util.Date;

public class CisObject{

	private int id;
	private int type;
	private int userId;
	private ObjectStatus status;
	private int creatorId;
	private Date lastModified;

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setTypeId(int type){
		this.type = type;
	}

	public int getTypeId(){
		return type;
	}

	public void setUserId(int userId){
		this.userId = userId;
	}

	public void setStatus(ObjectStatus objectStatus){
		this.status = objectStatus;
	}

	public void setCreatorId(int creatorId){
		this.creatorId = creatorId;
	}

	public int getCreatorId(){
		return creatorId;
	}

	public void setLastModified(Date lastModified){
		this.lastModified = lastModified;
	}

	public ObjectStatus getStatus(){
		return status;
	}
}
