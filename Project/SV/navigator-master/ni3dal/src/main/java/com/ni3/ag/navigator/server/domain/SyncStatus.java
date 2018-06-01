/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

public enum SyncStatus{
	New(1), Processed(2), ProcessedWithWarning(3), Error(4);

	private int value;

	SyncStatus(int v){
		value = v;
	}

	public int intValue(){
		return value;
	}

	public static SyncStatus fromInt(int val){
		for (SyncStatus ss : values()){
			if (ss.value == val)
				return ss;
		}
		return null;
	}
}
