/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

import java.io.Serializable;

public class ACModuleDescription implements Serializable{
	private static final long serialVersionUID = -8114192281814030171L;

	private AdminConsoleModule module;
	private int userCount = 0;
	private int usedUserCount = 0;
	private int maxNonExpiringUserCount = 0;
	private String columnName;

	public ACModuleDescription(AdminConsoleModule module){
		this.module = module;
	}

	public AdminConsoleModule getModule(){
		return module;
	}

	public void setUserCount(int userCount){
		this.userCount = userCount;
	}

	public int getUserCount(){
		return userCount;
	}

	public void setColumnName(String columnName){
		this.columnName = columnName;
	}

	public String getColumnName(){
		return columnName;
	}

	public int getUsedUserCount(){
		return usedUserCount;
	}

	public void setUsedUserCount(int usedUserCount){
		this.usedUserCount = usedUserCount;
	}

	public int getMaxNonExpiringUserCount(){
		return maxNonExpiringUserCount;
	}

	public void setMaxNonExpiringUserCount(int maxNonExpiringUserCount){
		this.maxNonExpiringUserCount = maxNonExpiringUserCount;
	}

	public String getFullColumnName(){
		return columnName + " " + usedUserCount + "(" + userCount + ")";
	}

}