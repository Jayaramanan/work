package com.ni3.ag.navigator.shared.constants;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ObjectAction{

	//@formatter:off
	UPDATE_NODE("UpdateNode"),
	UPDATE_EDGE("UpdateEdge"),
	DELETE("Delete"),
	INSERT_NODE("InsertNode"),
	INSERT_EDGE("InsertEdge"),
	MERGE_NODE("MergeNode"),
	CHECK_CAN_DELETE_NODE("CheckCanDeleteNode"),
	SET_CONTEXT("SetContext"),
	CLEAR_CONTEXT("ClearContext"),
	CLONE_CONTEXT("CloneContext")
	;
	//@formatter:on

	private static final Map<String, ObjectAction> LOOKUP_TABLE = new HashMap<String, ObjectAction>(7);
	private String value;

	static{
		for (final ObjectAction stringEnum : EnumSet.allOf(ObjectAction.class)){
			LOOKUP_TABLE.put(stringEnum.getValue(), stringEnum);
		}
	}

	ObjectAction(final String value){
		this.value = value;
	}

	public static ObjectAction getByValue(final String value){
		return LOOKUP_TABLE.get(value);
	}

	public String getValue(){
		return value;
	}

}
