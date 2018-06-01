/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

public enum InMatrixType{
	Hidden(0), Fixed(1), Displayed(2);

	private Integer intValue;

	InMatrixType(Integer val){
		intValue = val;
	}

	public Integer getValue(){
		return intValue;
	}

	public static InMatrixType getInMatrixType(Integer val){
		for (InMatrixType type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}
}
