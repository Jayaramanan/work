package com.ni3.ag.navigator.shared.constants;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum QueryType{

	SIMPLE(0), NODE(1), LINKED_NODES(2), NODE_WITH_CONNECTIONS(3);

	private static final Map<Integer, QueryType> LOOKUP_TABLE = new HashMap<Integer, QueryType>(5);

	static{
		for (final QueryType stringEnum : EnumSet.allOf(QueryType.class)){
			LOOKUP_TABLE.put(stringEnum.getValue(), stringEnum);
		}
	}

	private int value;

	private QueryType(final int value){
		this.value = value;
	}

	public static QueryType getByValue(final int value){
		return LOOKUP_TABLE.get(value);
	}

	public int getValue(){
		return value;
	}

}
