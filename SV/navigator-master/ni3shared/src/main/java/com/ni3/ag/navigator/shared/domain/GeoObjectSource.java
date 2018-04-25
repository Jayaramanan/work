/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

public enum GeoObjectSource{

	GRAPH("Graph"), MATRIX("Matrix"), DATABASE("Database");

	private String str;

	private GeoObjectSource(String str){
		this.str = str;
	}

	public String getValue(){
		return str;
	}

	@Override
	public String toString(){
		return str;
	}
}
