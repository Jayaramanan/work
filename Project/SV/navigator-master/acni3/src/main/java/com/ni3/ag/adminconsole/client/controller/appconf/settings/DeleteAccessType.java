/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.shared.language.TextID;

public enum DeleteAccessType{
	DeleteAccessNone("0"), DeleteAccessUser("1"), DeleteAccessGroup("2"), DeleteAccessAll("3");

	private String strValue;

	DeleteAccessType(String val){
		strValue = val;
	}

	public String getValue(){
		return strValue;
	}

	public static DeleteAccessType getAccessType(String val){
		for (DeleteAccessType type : values()){
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
