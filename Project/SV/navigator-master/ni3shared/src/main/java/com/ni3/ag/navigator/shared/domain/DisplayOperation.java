package com.ni3.ag.navigator.shared.domain;

public enum DisplayOperation{
	Sum(1),
	Avg(2),
	Min(3),
	Max(4),
	Count(5);

	DisplayOperation(int val){
		this.val = val;
	}

	private int val;

	public int toInt(){
		return val;
	}

	public static DisplayOperation fromInt(int displayOperation){
		for(DisplayOperation diop : values()){
			if(diop.toInt() == displayOperation)
				return diop;
		}
		return null;
	}
}
