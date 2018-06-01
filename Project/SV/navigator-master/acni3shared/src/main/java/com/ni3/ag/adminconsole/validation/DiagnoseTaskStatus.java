/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

public enum DiagnoseTaskStatus{
	Ok(1), Warning(2), Error(3), NotChecked(4);

	private int sort;

	DiagnoseTaskStatus(int sort){
		this.sort = sort;
	}

	public int getSort(){
		return sort;
	}

}
