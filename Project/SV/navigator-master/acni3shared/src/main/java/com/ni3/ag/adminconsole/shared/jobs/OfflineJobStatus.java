/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.jobs;

public enum OfflineJobStatus{
	Scheduled(0, "Scheduled"), ScheduledWaiting(10, "Scheduled, waiting for stable database state"), InProgress(1,
	        "In progress"), InProgress1(11, "In progress (1/8)"), InProgress2(12, "In progress (2/8)"), InProgress3(13,
	        "In progress (3/8)"), InProgress4(14, "In progress (4/8)"), InProgress5(15, "In progress (5/8)"), InProgress6(
	        16, "In progress (6/8)"), InProgress7(17, "In progress (7/8)"), InProgress8(18, "In progress (8/8)"), Ok(2, "Ok"), ErrorNoTempDataSource(
	        3, "Error, temp datasource not configured"), ErrorUnknownError(4, "Error, unknown"), ErrorDatabaseVersionIsInvalid(
	        5, "Error, invalid database version"), ErrorDatabaseIntegrity(6, "Error in database integrity"), ErrorDumpDatabase(
	        7, "Error creating dump"), ErrorCreateModule(8, "Error creating module");

	private Integer intValue;
	private String label;

	OfflineJobStatus(Integer val, String label){
		intValue = val;
		this.label = label;
	}

	public Integer getValue(){
		return intValue;
	}

	public String getLabel(){
		return label;
	}

	public static OfflineJobStatus getStatus(Integer val){
		for (OfflineJobStatus status : values()){
			if (status.getValue().equals(val)){
				return status;
			}
		}
		return null;
	}

	@Override
	public String toString(){
		return getLabel();
	}
}
