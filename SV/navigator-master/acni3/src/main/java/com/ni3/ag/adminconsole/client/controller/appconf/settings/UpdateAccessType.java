/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public enum UpdateAccessType{
	UpdateAccessNone("0"), UpdateAccessUser("1"), UpdateAccessGroup("2"), UpdateAccessAll("3");

	private String strValue;

	UpdateAccessType(String val){
		strValue = val;
	}

	public String getValue(){
		return strValue;
	}

	public static UpdateAccessType getAccessType(String val){
		for (UpdateAccessType type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}

	@Override
	public String toString(){
		return Translation.get(TextID.valueOf(name()));
	}
}
