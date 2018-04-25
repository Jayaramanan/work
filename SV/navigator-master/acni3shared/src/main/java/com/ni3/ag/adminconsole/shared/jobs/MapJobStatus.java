/** Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.jobs;

public enum MapJobStatus{
	Scheduled(0), Error(1), Ok(2), CopyingToModulesPath(5), Compressing(6), CopyingToMapPath(7), ProcessingMaps(8), ErrorFTPRefusedConnection(
	        100), ErrorCopyingToModulesPath(101), ErrorProcessingMaps(102);

	private Integer intValue;

	MapJobStatus(Integer val){
		intValue = val;
	}

	public Integer getValue(){
		return intValue;
	}

	public static boolean isError(Integer val){
		return val >= 100;
	}

	public static MapJobStatus getStatus(Integer val){
		for (MapJobStatus status : values()){
			if (status.getValue().equals(val)){
				return status;
			}
		}
		return null;
	}
}
