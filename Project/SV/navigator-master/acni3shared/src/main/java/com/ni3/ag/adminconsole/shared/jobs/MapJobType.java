/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.jobs;

public enum MapJobType{
	MapExtraction(0);

	private Integer intValue;

	MapJobType(Integer val){
		intValue = val;
	}

	public Integer getValue(){
		return intValue;
	}

	public static MapJobType getJobType(Integer val){
		for (MapJobType type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}
}