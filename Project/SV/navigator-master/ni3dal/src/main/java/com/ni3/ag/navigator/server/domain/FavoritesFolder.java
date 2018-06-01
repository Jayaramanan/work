package com.ni3.ag.navigator.server.domain;

import com.ni3.ag.navigator.shared.domain.User;

public class FavoritesFolder{

	private int id;
	private int parentId;
	private String folderName;
	private int schemaId;
	private int creatorId;
	private boolean groupFolder;
	private int sortOrder;

	public FavoritesFolder(){
	}

	public FavoritesFolder(String name){
		this.folderName = name;
	}

	public FavoritesFolder(int id, String name){
		this.folderName = name;
		this.id = id;
	}

	public FavoritesFolder(String name, FavoritesFolder parentFolder, User creator, int schemaId, boolean groupFolder,
	        int sort){
		this.folderName = name;
		if (parentFolder != null){
			this.parentId = parentFolder.getId();
		}
		this.creatorId = creator.getId();
		this.schemaId = schemaId;
		this.groupFolder = groupFolder;
		this.sortOrder = sort;
	}

	public FavoritesFolder(int id, String name, FavoritesFolder parentFolder){
		this.folderName = name;
		this.parentId = parentFolder.getId();
		this.id = id;
	}

	public int getCreatorId(){
		return creatorId;
	}

	public String getFolderName(){
		return folderName;
	}

	public Boolean getGroupFolder(){
		return groupFolder;
	}

	public int getId(){
		return id;
	}

	public int getParentId(){
		return parentId;
	}

	public int getSchemaId(){
		return schemaId;
	}

	public int getSortOrder(){
		return sortOrder;
	}

	public void setCreatorId(final int creatorId){
		this.creatorId = creatorId;
	}

	public void setFolderName(final String folderName){
		this.folderName = folderName;
	}

	public void setGroupFolder(final Boolean groupFolder){
		this.groupFolder = groupFolder;
	}

	public void setId(final int id){
		this.id = id;
	}

	public void setParentId(final int parentId){
		this.parentId = parentId;
	}

	public void setSchemaId(final int schemaId){
		this.schemaId = schemaId;
	}

	public void setSortOrder(final int sortOrder){
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString(){
		return "FavoritesFolder [id=" + id + ", parentId=" + parentId + ", folderName=" + folderName + ", schemaId="
		        + schemaId + ", creatorId=" + creatorId + ", groupFolder=" + groupFolder + ", sortOrder=" + sortOrder + "]";
	}

}
