package com.ni3.ag.navigator.shared.constants;

import com.ni3.ag.navigator.shared.proto.NResponse.Attribute.EditOption;

public enum EditingOption{
	NotVisible(0), ReadOnly(1), Editable(2), Mandatory(3);

	private int value;

	EditingOption(int value){
		this.value = value;
	}

	public int getValue(){
		return value;
	}

	public static EditingOption fromValue(Integer val){
		if (val == null)
			return null;
		for (EditingOption l : values()){
			if (l.getValue() == val)
				return l;
		}
		return NotVisible;
	}

	public static EditingOption fromProtoBufValue(EditOption editLock){
		switch (editLock){
			case NOT_VISIBLE:
				return NotVisible;
			case READ_ONLY:
				return ReadOnly;
			case READ_WRITE:
				return Editable;
			case MANDATORY:
				return Mandatory;
		}
		return NotVisible;
	}
}
