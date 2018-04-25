/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.constants;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum FavoritesAction{
	//@formatter:off
	CreateFolder("CreateFolder"),
	UpdateFolder("UpdateFolder"),
	DeleteFolder("DeleteFolder"),
	FavoriteCreate("FavoriteCreate"),
	FavoriteUpdate("FavoriteUpdate"),
	FavoriteDelete("FavoriteDelete"),
	FavoriteFolderUpdate("FavoriteFolderUpdate"),
	MoveToGroup("MoveToGroup"),
	FavoriteNameUpdate("FavoriteNameUpdate"),
	CopyFavorite("CopyFavorite"),
	ValidateFavoriteVersion("ValidateFavoriteVersion");
	//@formatter:on

	private static final Map<String, FavoritesAction> LOOKUP_TABLE = new HashMap<String, FavoritesAction>(12);
	private String value;

	static{
		for (final FavoritesAction stringEnum : EnumSet.allOf(FavoritesAction.class)){
			LOOKUP_TABLE.put(stringEnum.getValue(), stringEnum);
		}
	}

	FavoritesAction(final String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}

	public static FavoritesAction getByValue(final String value){
		return LOOKUP_TABLE.get(value);
	}

}
