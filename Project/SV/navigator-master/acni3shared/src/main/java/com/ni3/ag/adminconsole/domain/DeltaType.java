package com.ni3.ag.adminconsole.domain;

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
    OBJECT_DELETE(27),;
    //@formatter:on

	private Integer value;
	private static final Map<Integer, DeltaType> LOOKUP_TABLE = new HashMap<Integer, DeltaType>();

	static{
		for (final DeltaType deltaType : EnumSet.allOf(DeltaType.class)){
			LOOKUP_TABLE.put(deltaType.getValue(), deltaType);
		}
	}

	DeltaType(final Integer value){
		this.value = value;
	}

	public static DeltaType valueOf(final Integer id){
		return LOOKUP_TABLE.get(id);
	}

	public Integer getValue(){
		return value;
	}
}
