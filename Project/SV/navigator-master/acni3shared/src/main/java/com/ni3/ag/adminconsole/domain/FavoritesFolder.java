/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.List;

public class FavoritesFolder implements Serializable{
	private static final long serialVersionUID = -3614331468756764612L;
	public static final String ID = "id";

	private Integer id;
	private String folderName;
	private Schema schema;
	private User creator;
	private FavoritesFolder parent;
	private Integer _groupFolder;
	private Integer sort;
	private List<Favorites> favorites;
	private List<FavoritesFolder> childFolders;

	public List<FavoritesFolder> getChildFolders(){
		return childFolders;
	}

	public void setChildFolders(List<FavoritesFolder> childFolders){
		this.childFolders = childFolders;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getFolderName(){
		return folderName;
	}

	public void setFolderName(String folderName){
		this.folderName = folderName;
	}

	public Schema getSchema(){
		return schema;
	}

	public void setSchema(Schema schema){
		this.schema = schema;
	}

	public User getCreator(){
		return creator;
	}

	public void setCreator(User creator){
		this.creator = creator;
	}

	public FavoritesFolder getParent(){
		return parent;
	}

	public void setParent(FavoritesFolder parent){
		this.parent = parent;
	}

	public Integer get_groupFolder(){
		return _groupFolder;
	}

	public void set_groupFolder(Integer _groupFolder){
		this._groupFolder = _groupFolder;
	}

	public boolean isGroupFolder(){
		return new Integer(1).equals(_groupFolder);
	}

	public void setGroupFolder(boolean b){
		_groupFolder = b ? 1 : 0;
	}

	public Integer getSort(){
		return sort;
	}

	public void setSort(Integer sort){
		this.sort = sort;
	}

	public List<Favorites> getFavorites(){
		return favorites;
	}

	public void setFavorites(List<Favorites> favorites){
		this.favorites = favorites;
	}

}
