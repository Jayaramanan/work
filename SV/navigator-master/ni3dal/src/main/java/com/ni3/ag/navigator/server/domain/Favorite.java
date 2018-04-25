package com.ni3.ag.navigator.server.domain;

import com.ni3.ag.navigator.shared.domain.FavoriteMode;

public class Favorite{

	private int id;
	private String description;
	private int schemaId;
	private String data;
	private String layout;
	private String name;
	private int creatorId;
	private int folderId;
	private boolean groupFavorite;
	private FavoriteMode mode;
	private String dbVersion;

	public Favorite(String description, Integer schemaId, String data, String layout, String name, Integer creatorId,
	        Integer folderId, Boolean groupFavorite, FavoriteMode mode){
		this.description = description;
		this.schemaId = schemaId;
		this.data = data;
		this.layout = layout;
		this.name = name;
		this.creatorId = creatorId;
		this.folderId = folderId;
		this.groupFavorite = groupFavorite;
		this.mode = mode;
	}

	public Favorite(){

	}

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public void setSchemaId(int schemaId){
		this.schemaId = schemaId;
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

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public int getCreatorId(){
		return creatorId;
	}

	public void setCreatorId(int creatorId){
		this.creatorId = creatorId;
	}

	public int getFolderId(){
		return folderId;
	}

	public void setFolderId(int folderId){
		this.folderId = folderId;
	}

	public Boolean getGroupFavorite(){
		return groupFavorite;
	}

	public void setGroupFavorite(Boolean groupFavorite){
		this.groupFavorite = groupFavorite;
	}

	public FavoriteMode getMode(){
		return mode;
	}

	public void setMode(FavoriteMode mode){
		this.mode = mode;
	}

	public String getDbVersion(){
		return dbVersion;
	}

	public void setDbVersion(String dbVersion){
		this.dbVersion = dbVersion;
	}

}
