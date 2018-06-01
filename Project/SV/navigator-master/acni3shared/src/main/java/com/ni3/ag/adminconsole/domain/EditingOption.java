/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.domain;

import com.ni3.ag.adminconsole.shared.language.TextID;

public enum EditingOption{
	NotVisible(0, TextID.NotVisible), ReadOnly(1, TextID.ReadOnly), Editable(2, TextID.Editable), Mandatory(3, TextID.Mandatory);

	private int value;
	private TextID labelId;

	EditingOption(int value, TextID id){
		this.value = value;
		this.labelId = id;
	}

	public int getValue(){
		return value;
	}

	public TextID getLabelID(){
		return labelId;
	}

	public static EditingOption fromValue(Integer val){
		if (val == null)
			return null;
		for (EditingOption l : values()){
			if (l.getValue() == val)
				return l;
		}
		return null;
	}
}
