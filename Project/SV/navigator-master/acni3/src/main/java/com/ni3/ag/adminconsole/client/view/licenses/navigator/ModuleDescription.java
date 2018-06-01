/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ModuleDescription{
	private NavigatorModule module;
	private int userCount;
	private int usedUserCount;
	private String columnName;

	public ModuleDescription(NavigatorModule module, int userCount){
		this.module = module;
		this.userCount = userCount;
		TextID id = TextID.valueOf(module.toString());
		columnName = Translation.get(id);
	}

	public NavigatorModule getModule(){
		return module;
	}

	public int getUserCount(){
		return userCount;
	}

	public String getColumnName(){
		return columnName;
	}

	public int getUsedUserCount(){
		return usedUserCount;
	}

	public void setUsedUserCount(int usedUserCount){
		this.usedUserCount = usedUserCount;
	}

	public String getFullColumnName(){
		return columnName + " " + usedUserCount + "(" + userCount + ")";
	}

}