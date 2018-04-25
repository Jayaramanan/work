/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum LineStyle{
	TRANSPARENT(0, TextID.Transparent), DOTTED(1, TextID.Dotted), Dashed(2, TextID.Dashed), DASH_DOT(3, TextID.DashDot), FULL(4,
	        TextID.Full);

	LineStyle(int val, TextID textId){
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

	public static LineStyle fromInt(int lineStyle){
		for (LineStyle ct : values()){
			if (ct.toInt() == lineStyle)
				return ct;
		}
		return null;
	}

	public static LineStyle fromLabel(String label){
		for (LineStyle ot : values()){
			if (ot.getTextId().getKey().equalsIgnoreCase(label))
				return ot;
		}
		return null;
	}
}
