package com.ni3.ag.navigator.shared.domain;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DeltaType{

	//@formatter:off
	SETTING_UPDATE(1),
	
	FAVORITE_CREATE(10),
	FAVORITE_DELETE(11),
	FAVORITE_UPDATE(12),
	FAVORITE_COPY(13),
	
	FAVORITE_FOLDER_CREATE(20),
	FAVORITE_FOLDER_DELETE(21),
	FAVORITE_FOLDER_UPDATE(22),
	
	NODE_CREATE(23),
	NODE_UPDATE(24),
	EDGE_CREATE(25),
	EDGE_UPDATE(26),
	OBJECT_DELETE(27),
	NODE_MERGE(28),
	NODE_UPDATE_METAPHOR(29),
	NODE_UPDATE_COORDS(30),

	GEO_ANALYTICS_SAVE(40),
	GEO_ANALYTICS_DELETE(41)
	;
	//@formatter:on

	private int id;
	private static final Map<Integer, DeltaType> LOOKUP_TABLE = new HashMap<Integer, DeltaType>();

	static{
		for (final DeltaType deltaType : EnumSet.allOf(DeltaType.class)){
			LOOKUP_TABLE.put(deltaType.intValue(), deltaType);
		}
	}

	DeltaType(int v){
		id = v;
	}

	public int intValue(){
		return id;
	}

	public static DeltaType getById(final int id){
		final DeltaType mode = LOOKUP_TABLE.get(id);
		return mode;
	}
}
