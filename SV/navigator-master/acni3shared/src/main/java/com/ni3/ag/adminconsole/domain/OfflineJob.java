/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;

import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;

public class OfflineJob implements Serializable, Comparable<OfflineJob>{
	private static final long serialVersionUID = -771508665696414862L;
	public static final String STATUS = "status";
	public static final String JOB_TYPE = "jobType";
	public static final String TIME_START = "timeStart";
	public static final String USER_ID = "userIds";
	public static final String ID = "id";
	public static final String USER_ID_SEPARATOR = ",";

	private Integer id;
	private String userIds;
	private Integer jobType;
	private Integer status;
	private Date timeStart;
	private Date timeEnd;
	private User triggeredBy;
	private Boolean withFirstDegreeObjects;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public void setWithFirstDegreeObjects(Boolean withFirstDegreeObjects){
		this.withFirstDegreeObjects = withFirstDegreeObjects;
	}

	public Boolean getWithFirstDegreeObjects(){
		return withFirstDegreeObjects;
	}

	public String getUserIds(){
		return userIds;
	}

	public void setUserIds(String userIds){
		this.userIds = userIds;
	}

	public Integer getJobType(){
		return jobType;
	}

	public void setJobType(Integer jobType){
		this.jobType = jobType;
	}

	public Integer getStatus(){
		return status;
	}

	public void setStatus(Integer status){
		this.status = status;
	}

	public Date getTimeStart(){
		return timeStart;
	}

	public void setTimeStart(Date timeStart){
		this.timeStart = timeStart;
	}

	public Date getTimeEnd(){
		return timeEnd;
	}

	public void setTimeEnd(Date timeEnd){
		this.timeEnd = timeEnd;
	}

	public User getTriggeredBy(){
		return triggeredBy;
	}

	public void setTriggeredBy(User triggeredBy){
		this.triggeredBy = triggeredBy;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof OfflineJob))
			return false;
		if (o == this)
			return true;
		OfflineJob dt = (OfflineJob) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

	@Override
	public int compareTo(OfflineJob o){
		if (this.status == null || OfflineJobStatus.getStatus(status) == null)
			return -1;
		else if (o == null || o.getStatus() == null || OfflineJobStatus.getStatus(status) == null)
			return 1;

		int result = OfflineJobStatus.getStatus(status).compareTo(OfflineJobStatus.getStatus(o.getStatus()));
		if (result == 0){
			if (this.getTimeStart() == null)
				result = -1;
			else if (o.getTimeStart() == null)
				result = 1;
			else
				result = o.getTimeStart().compareTo(this.getTimeStart());
		}
		return result;
	}
}
