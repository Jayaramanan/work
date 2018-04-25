package com.ni3.ag.navigator.server.type;

public enum Scope{
	Denied(0), DeniedByPrefilter(1), External(2), Allow(3);

	private int val;

	Scope(int val){
		this.val = val;
	}

	public int getValue(){
		return val;
	}
}
