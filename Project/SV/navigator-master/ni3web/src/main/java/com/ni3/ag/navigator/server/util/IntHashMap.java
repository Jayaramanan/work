/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.util;

import java.util.LinkedHashMap;

public class IntHashMap extends LinkedHashMap<Integer, Integer>{
	private static final long serialVersionUID = -1522811482801966278L;
	private Integer ifNullValue = null;

	public IntHashMap(Integer ifNullValue){
		super();
		this.ifNullValue = ifNullValue;
	}

	@Override
	public Integer get(Object key){
		Integer result = super.get(key);
		return result == null ? ifNullValue : result;
	}
}
