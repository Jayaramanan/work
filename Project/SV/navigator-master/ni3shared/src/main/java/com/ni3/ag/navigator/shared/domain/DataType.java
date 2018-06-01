package com.ni3.ag.navigator.shared.domain;

public enum DataType{

	TEXT(1), INT(2), BOOL(3), DECIMAL(4), URL(5), DATE(6);

	DataType(int val){
		this.val = val;
	}

	private int val;

	public int toInt(){
		return val;
	}

	public static DataType fromInt(int lineStyle){
		for (DataType ct : values()){
			if (ct.toInt() == lineStyle)
				return ct;
		}
		return null;
	}
	
	/** how dates are stored in database */
	public static final String DB_DATE_FORMAT = "yyyyMMdd";
	
	/** how dates are sent/received from/to Salesforce API */
	public static final String SALESFORCE_DATE_FORMAT = "yyyy-MM-dd";
}
