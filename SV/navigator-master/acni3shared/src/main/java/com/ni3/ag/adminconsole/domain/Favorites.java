/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class Favorites implements Serializable{
	private static final long serialVersionUID = 1L;

	public static final String SCHEMA = "schema";
	public static final String ID = "id";
	public static final String CREATOR = "creator";
	public static final String DB_VERSION = "dbVersion";
	public static final String MODE = "mode";

	public static final Integer QUERY_MODE = 2;

	private Integer id;
	private Schema schema;
	private String description;
	private String data;
	private String layout;
	private User creator;
	private FavoritesFolder folder;
	private Integer groupFavorite_;
	private Integer mode;
	private String dbVersion;
	private String name;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getData(){
		return data;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getLayout(){
		return layout;
	}

	public void setLayout(String layout){
		this.layout = layout;
	}

	public User getCreator(){
		return creator;
	}

	public void setCreator(User creator){
		this.creator = creator;
	}

	public FavoritesFolder getFolder(){
		return folder;
	}

	public void setFolder(FavoritesFolder folder){
		this.folder = folder;
	}

	private Integer getGroupFavorite_(){
		return groupFavorite_;
	}

	private void setGroupFavorite_(Integer groupFavorite){
		groupFavorite_ = groupFavorite;
	}

	public boolean isGroupFavorite(){
		return getGroupFavorite_() != null && getGroupFavorite_().intValue() == 1;
	}

	public void setGroupFavorite(Boolean groupFavorite){
		setGroupFavorite_(groupFavorite ? 1 : 0);
	}

	public Integer getMode(){
		return mode;
	}

	public void setMode(Integer mode){
		this.mode = mode;
	}

	public String getDbVersion(){
		return dbVersion;
	}

	public void setDbVersion(String dbVersion){
		this.dbVersion = dbVersion;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof Favorites))
			return false;
		if (o == this)
			return true;
		Favorites fv = (Favorites) o;
		if (getId() == null || fv.getId() == null)
			return false;
		return getId().equals(fv.getId());
	}

}
