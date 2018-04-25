package com.ni3.ag.navigator.shared.domain;

public enum LineStyle{
	TRANSPARENT(0, new float[] { 0 }), DOT(1, new float[] { 3, 3 }), Dashed(2, new float[] { 10, 10 }), DASH_DOT(3,
	        new float[] { 10, 3 }), FULL(4, new float[] { 1 });

	LineStyle(int val, float[] dashes){
		this.val = val;
		this.dashes = dashes;
	}

	private int val;
	private float[] dashes;

	public int toInt(){
		return val;
	}

	public float[] getDashes(){
		return dashes;
	}

	public static LineStyle fromInt(int lineStyle){
		for (LineStyle ct : values()){
			if (ct.toInt() == lineStyle)
				return ct;
		}
		return null;
	}
}
