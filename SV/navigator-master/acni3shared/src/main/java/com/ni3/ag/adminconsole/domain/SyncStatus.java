/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum SyncStatus{

	//@formatter:off
    New(1),
    Processed(2);
    // @formatter:on

	private Integer value;
	private static final Map<Integer, SyncStatus> LOOKUP_TABLE = new HashMap<Integer, SyncStatus>(2);

	static{
		for (final SyncStatus syncStatus : EnumSet.allOf(SyncStatus.class)){
			LOOKUP_TABLE.put(syncStatus.getValue(), syncStatus);
		}
	}

	SyncStatus(final Integer value){
		this.value = value;
	}

	public static SyncStatus valueOf(final Integer id){
		return LOOKUP_TABLE.get(id);
	}

	public Integer getValue(){
		return value;
	}
}
