/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class MapJob implements Serializable{

	private static final long serialVersionUID = -3434765094318555370L;
	public static final String STATUS = "status";
	public static final String TIME_START = "timeStart";

	private Integer id;
	private User user;
	private Integer jobType;
	private Integer status;
	private Date timeStart;
	private Date timeEnd;
	private User triggeredBy;
	private BigDecimal x1;
	private BigDecimal x2;
	private BigDecimal y1;
	private BigDecimal y2;
	private String scale;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
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

	public BigDecimal getX1(){
		return x1;
	}

	public void setX1(BigDecimal x1){
		this.x1 = x1;
	}

	public BigDecimal getX2(){
		return x2;
	}

	public void setX2(BigDecimal x2){
		this.x2 = x2;
	}

	public BigDecimal getY1(){
		return y1;
	}

	public void setY1(BigDecimal y1){
		this.y1 = y1;
	}

	public BigDecimal getY2(){
		return y2;
	}

	public void setY2(BigDecimal y2){
		this.y2 = y2;
	}

	public String getScale(){
		return scale;
	}

	public void setScale(String scale){
		this.scale = scale;
	}

	public User getUser(){
		return user;
	}

	public void setUser(User user){
		this.user = user;
	}

	public Integer getJobType(){
		return jobType;
	}

	public void setJobType(Integer jobType){
		this.jobType = jobType;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof MapJob))
			return false;
		if (o == this)
			return true;
		MapJob dt = (MapJob) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}
}
