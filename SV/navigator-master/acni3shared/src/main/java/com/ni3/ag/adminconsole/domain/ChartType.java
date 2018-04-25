package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum ChartType{
	PIE(1, TextID.Pie), STACKED(2, TextID.Stacked), BAR(3, TextID.Bar);

	ChartType(int val, TextID textId){
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

	public static ChartType fromInt(int chartType){
		for (ChartType ct : values()){
			if (ct.toInt() == chartType)
				return ct;
		}
		return null;
	}
}