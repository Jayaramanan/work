/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.io.Serializable;

public class DiagnoseTaskResult implements Serializable, Comparable<DiagnoseTaskResult>{

	public static final int USER_ID_FIX_PARAM = 0, SCHEMA_ID_FIX_PARAM = 1;

	private static final long serialVersionUID = 1L;
	private String description;
	private DiagnoseTaskStatus status;
	private Object[] fixParams;
	private String clazz;
	private boolean fixable;
	private String errorDescription;
	private String actionDescription;

	public DiagnoseTaskResult(String clazz, String descr, boolean fixable, DiagnoseTaskStatus status,
	        String errorDescription, String actionDescription){
		this.description = descr;
		this.status = status;
		this.clazz = clazz;
		this.fixable = fixable;
		this.errorDescription = errorDescription;
		this.actionDescription = actionDescription;
	}

	public String getErrorDescription(){
		return errorDescription;
	}

	public void setErrorDescription(String val){
		this.errorDescription = val;
	}

	public boolean isFixable(){
		return fixable;
	}

	public void setFixable(boolean fixable){
		this.fixable = fixable;
	}

	public DiagnoseTaskStatus getStatus(){
		return status;
	}

	public void setStatus(DiagnoseTaskStatus status){
		this.status = status;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String myDescription){
		this.description = myDescription;
	}

	public Object[] getFixParams(){
		return fixParams;
	}

	public void setFixParams(Object[] fixParams){
		this.fixParams = fixParams;
	}

	public String getTaskClass(){
		return this.clazz;
	}

	public String getActionDescription(){
		return actionDescription;
	}

	public void setActionDescription(String actionDescription){
		this.actionDescription = actionDescription;
	}

	@Override
	public int compareTo(DiagnoseTaskResult o){
		DiagnoseTaskResult dtr = (DiagnoseTaskResult) o;
		return status.getSort() - dtr.getStatus().getSort();
	}
}
