/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.domain;

public enum ReportType{
	DYNAMIC_REPORT(1), STATIC_REPORT(2),

	//this is a special type requested by Sharon - Persons and Orgs are merged on the same page, Activities are ignored
	MERGED(3);

	private int value;

	ReportType(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	public static ReportType fromValue(Integer val){
		ReportType result = DYNAMIC_REPORT;
		if (val != null){
			for (ReportType rt : values()){
				if (rt.getValue() == val)
					return rt;
			}
		}
		return result;
	}
}
