/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.jobs;

public enum JobType{
	Export(0);

	private Integer intValue;

	JobType(Integer val){
		intValue = val;
	}

	public Integer getValue(){
		return intValue;
	}

	public static JobType getJobType(Integer val){
		for (JobType type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}
}
