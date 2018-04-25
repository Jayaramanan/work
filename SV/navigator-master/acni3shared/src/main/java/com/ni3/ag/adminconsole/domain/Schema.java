/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Schema implements Serializable, Comparable<Schema>, Cloneable{
	private static final long serialVersionUID = 4654112576200342420L;

	public static final int SCHEMA_MAX_NAME_LENGTH = 15;
	public static final String SCHEMA_NAME_DB_COLUMN = "name";
	public static final String CREATED_BY = "createdBy";

	private Integer id;
	private String name;
	private String description;
	private Date creationDate;
	private User createdBy;
	private List<ObjectDefinition> objectDefinitions;
	private List<Chart> charts;
	private List<Favorites> favorites;
	private List<FavoritesFolder> favoritesFolders;
	private List<SchemaGroup> schemaGroups;

	public Schema(){
		setDescription("");
	}

	public List<ObjectDefinition> getObjectDefinitions(){
		return objectDefinitions;
	}

	public void setObjectDefinitions(List<ObjectDefinition> objectDefinitions){
		this.objectDefinitions = objectDefinitions;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public Date getCreationDate(){
		return creationDate;
	}

	public void setCreationDate(Date creation){
		creationDate = creation;
	}

	public User getCreatedBy(){
		return createdBy;
	}

	public void setCreatedBy(User createdBy){
		this.createdBy = createdBy;
	}

	public List<Chart> getCharts(){
		return charts;
	}

	public void setCharts(List<Chart> charts){
		this.charts = charts;
	}

	public List<Favorites> getFavorites(){
		return favorites;
	}

	public void setFavorites(List<Favorites> favorites){
		this.favorites = favorites;
	}

	public List<FavoritesFolder> getFavoritesFolders(){
		return favoritesFolders;
	}

	public void setFavoritesFolders(List<FavoritesFolder> favoritesFolders){
		this.favoritesFolders = favoritesFolders;
	}

	public List<SchemaGroup> getSchemaGroups(){
		return schemaGroups;
	}

	public void setSchemaGroups(List<SchemaGroup> schemaGroups){
		this.schemaGroups = schemaGroups;
	}

	/**
	 * This method should return "name" as the first characters to allow key selection in drop-downs See AC-1459
	 */
	@Override
	public String toString(){
		return name + " [Schema] id = " + id;
	}

	public int compareTo(Schema o){
		if (this.getName() == null && (o == null || o.getName() == null)){
			return 0;
		} else if (this.getName() == null){
			return -1;
		} else if (o == null || o.getName() == null){
			return 1;
		}
		return getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(java.lang.Object obj){
		if (this == obj){
			return true;
		}
		if (obj == null || !(obj instanceof Schema)){
			return false;
		}
		if (getId() == null || ((Schema) obj).getId() == null){
			return false;
		}
		return getId().intValue() == ((Schema) obj).getId().intValue();

	}

	public Schema clone() throws CloneNotSupportedException{
		return (Schema) super.clone();
	}

	public Schema clone(Integer id, List<ObjectDefinition> objectDefs, List<Chart> charts,
	        List<ReportTemplate> reportTemplates, List<Favorites> favorites, List<FavoritesFolder> favoriteFolders, User user)
	        throws CloneNotSupportedException{
		Schema s = clone();
		s.setId(id);
		s.setObjectDefinitions(objectDefs);
		s.setCharts(charts);
		s.setFavorites(favorites);
		s.setFavoritesFolders(favoriteFolders);
		s.setCreatedBy(user);
		return s;
	}
}
