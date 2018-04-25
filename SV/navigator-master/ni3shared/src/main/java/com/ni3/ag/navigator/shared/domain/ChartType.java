package com.ni3.ag.navigator.shared.domain;

public enum ChartType{
	Pie(1),
	Stacked(2),
	Bar(3);

	ChartType(int val){
		this.val = val;
	}

	private int val;

	public int toInt(){
		return val;
	}

	public static ChartType fromInt(int chartType){
		for(ChartType ct : values()){
			if(ct.toInt() == chartType)
				return ct;
		}
		return null;
	}
}
