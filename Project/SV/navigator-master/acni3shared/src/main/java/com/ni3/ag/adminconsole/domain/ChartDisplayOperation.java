package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum ChartDisplayOperation{
	SUM(1, TextID.Sum), AVG(2, TextID.Avg), MIN(3, TextID.Min), MAX(4, TextID.Max), COUNT(5, TextID.Count);

	ChartDisplayOperation(int val, TextID textId){
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

	public static ChartDisplayOperation fromInt(int displayOperation){
		for (ChartDisplayOperation diop : values()){
			if (diop.toInt() == displayOperation)
				return diop;
		}
		return null;
	}
}