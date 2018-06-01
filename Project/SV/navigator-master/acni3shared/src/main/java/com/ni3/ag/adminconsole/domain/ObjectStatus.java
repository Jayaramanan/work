package com.ni3.ag.adminconsole.domain;
public enum ObjectStatus{
	Normal(0), Locked(1), Deleted(3), Merged(4);

	private int val;

	ObjectStatus(int val){
		this.val = val;
	}

	public int toInt(){
		return val;
	}

	public static ObjectStatus fromInt(int anInt){
		for (ObjectStatus os : values())
			if (os.toInt() == anInt)
				return os;
		return null;
	}
}