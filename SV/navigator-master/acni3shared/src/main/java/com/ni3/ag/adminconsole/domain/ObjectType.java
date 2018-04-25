/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum ObjectType{
	NODE(2, TextID.Node), EDGE(4, TextID.Edge), CONTEXT_EDGE(6, TextID.ContextEdge);

	ObjectType(int val, TextID textId){
		this.val = val;
		this.textId = textId;
	}

	private int val;
	private TextID textId;

	public int toInt(){
		return val;
	}

	public String getLabel(){
		return textId.getKey();
	}

	public TextID getTextId(){
		return textId;
	}

	public static ObjectType fromInt(int objectType){
		for (ObjectType ot : values()){
			if (ot.toInt() == objectType)
				return ot;
		}
		return null;
	}

	public static ObjectType fromLabel(String label){
		for (ObjectType ot : values()){
			if (ot.getLabel().equals(label))
				return ot;
		}
		return null;
	}
}
