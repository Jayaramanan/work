/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum DataType{
	
	TEXT(1, TextID.Text), INT(2, TextID.Int), BOOL(3, TextID.Bool), DECIMAL(4, TextID.Decimal), URL(5,
	        TextID.URL), DATE(6, TextID.Date);

	DataType(int val, TextID textId){
		this.val = val;
		this.textId = textId;
	}

	private int val;
	private TextID textId;

	public int toInt(){
		return val;
	}

	public TextID getTextId(){
		return textId;
	}

	public static DataType fromInt(int lineStyle){
		for (DataType ct : values()){
			if (ct.toInt() == lineStyle)
				return ct;
		}
		return null;
	}

	public static DataType fromLabel(String label){
		for (DataType ot : values()){
			if (ot.getTextId().getKey().equalsIgnoreCase(label))
				return ot;
		}
		return null;
	}

	/** how dates are stored in database */
	public static final String DB_DATE_FORMAT = "yyyyMMdd";
	/** how dates are shown in application by default */
	public static final String DISPLAY_DATE_FORMAT = "dd/MM/yyyy";

	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
}
