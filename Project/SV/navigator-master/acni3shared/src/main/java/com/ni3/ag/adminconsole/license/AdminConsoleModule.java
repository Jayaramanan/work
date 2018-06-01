/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

public enum AdminConsoleModule{
	BaseModule(LicenseData.USER_COUNT_PROPERTY), ETLModule(LicenseData.ACETL_MODULE);

	private String value;

	AdminConsoleModule(String val){
		value = val;
	}

	public String getValue(){
		return value;
	}

	public static AdminConsoleModule getACModule(String val){
		for (AdminConsoleModule type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}
}
