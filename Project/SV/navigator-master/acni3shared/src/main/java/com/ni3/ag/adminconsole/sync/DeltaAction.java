/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.sync;

public enum DeltaAction{
	New(1), Edit(2), Delete(3);

	private int action;

	private DeltaAction(int action){
		this.action = action;
	}

	public Integer getAction(){
		return action;
	}

	public static DeltaAction getActionForValue(Integer act){
		for (DeltaAction da : DeltaAction.values()){
			if (da.getAction().equals(act))
				return da;
		}
		return null;
	}
}
