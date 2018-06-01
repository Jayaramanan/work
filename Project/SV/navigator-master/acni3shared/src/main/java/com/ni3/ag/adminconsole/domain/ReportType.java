package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum ReportType{
	DYNAMIC_REPORT(TextID.DynamicReport, 1), STATIC_REPORT(TextID.StaticReport, 2);
	private int value;
	private TextID textId;

	ReportType(TextID textId, int value){
		this.value = value;
		this.textId = textId;
	}

	public int getValue(){
		return value;
	}

	public TextID getTextID(){
		return textId;
	}

	public static ReportType fromValue(Integer val){
		ReportType result = DYNAMIC_REPORT;
		if (val != null){
			for (ReportType rt : values()){
				if (rt.getValue() == val)
					return rt;
			}
		}
		return result;
	}
}