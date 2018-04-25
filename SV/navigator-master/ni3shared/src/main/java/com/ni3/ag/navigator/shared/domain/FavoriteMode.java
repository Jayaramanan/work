package com.ni3.ag.navigator.shared.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FavoriteMode{
	// TODO: check that UNKNOWN is needed
	UNKNOWN(0), FAVORITE(1), QUERY(2), TOPIC(3);

	private static final Map<Integer, FavoriteMode> LOOKUP_TABLE = new HashMap<Integer, FavoriteMode>(4);

	static{
		for (final FavoriteMode stringEnum : EnumSet.allOf(FavoriteMode.class)){
			LOOKUP_TABLE.put(stringEnum.getValue(), stringEnum);
		}
	}

	private int value;

	private FavoriteMode(final int value){
		this.value = value;
	}

	public static FavoriteMode getByValue(final int value){
		final FavoriteMode mode = LOOKUP_TABLE.get(value);
		return mode != null ? mode : UNKNOWN;
	}

	public int getValue(){
		return value;
	}
}
