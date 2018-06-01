/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable, Comparable<User>{
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_USER_NAME = "def";

	public static final String HAS_OFFLINE_CLIENT = "_hasOfflineClient";
	public static final String DATE_TIME_LESS_FILTER = "dateTimeLessFilter";
	public static final String DATE_TIME_GREATER_FILTER = "dateTimeGreaterFilter";
	public static final String REMOVE_NOT_LOG_ACTIVITIES_FILTER = "removeNotLogs";

	/**
	 * sequences to redirect on a thick client
	 */
	public static final String FAVORITES_FOLDER_SEQUENCE = "seq_user_favorites_folder";
	public static final String FAVORITES_SEQUENCE = "seq_cis_favorites";
	public static final String OBJECT_SEQUENCE = "seq_objectcount";
	public static final String THEMATIC_MAP_SEQUENCE = "geo_thematicmap_id_seq";
	public static final String THEMATIC_MAP_CLUSTER_SEQUENCE = "geo_thematiccluster_id_seq";
	public static final String THEMATIC_MAP_FOLDER_SEQUENCE = "geo_thematicfolder_id_seq";
	public static final String[] SEQUENCES = new String[] { FAVORITES_FOLDER_SEQUENCE, OBJECT_SEQUENCE,
			FAVORITES_SEQUENCE, THEMATIC_MAP_SEQUENCE, THEMATIC_MAP_FOLDER_SEQUENCE, THEMATIC_MAP_CLUSTER_SEQUENCE};

	public static final String ID_PROPERTY = "id";
	public static final String USERNAME_PROPERTY = "userName";

	private Integer id;
	private String firstName;
	private String lastName;
	private String userName;
	private String password;
	private String SID;
	private Date activeFrom;
	private Date activeUntil;
	private Date lastActivity;
	private String eMail;
	private List<Group> groups;
	private List<UserSetting> settings;
	private Integer _active;
	private List<Favorites> favorites;
	private List<FavoritesFolder> favoritesFolders;
	private List<UserEdition> userEditions;
	private Integer _hasOfflineClient;
	private List<ModuleUser> userModules;
	private String etlUser;
	private String etlPassword;
	private List<UserActivity> activities;

	public List<ModuleUser> getUserModules(){
		return userModules;
	}

	public void setUserModules(List<ModuleUser> userModules){
		this.userModules = userModules;
	}

	public Integer get_hasOfflineClient(){
		return _hasOfflineClient;
	}

	public void set_hasOfflineClient(Integer hasOfflineClient){
		_hasOfflineClient = hasOfflineClient;
	}

	public boolean getHasOfflineClient(){
		return _hasOfflineClient == null ? false : _hasOfflineClient != 0;
	}

	public void setHasOfflineClient(boolean b){
		_hasOfflineClient = b ? 1 : 0;
	}

	public String getFirstName(){
		return firstName;
	}

	public void setFirstName(String firstName){
		this.firstName = firstName;
	}

	public String getLastName(){
		return lastName;
	}

	public void setLastName(String lastName){
		this.lastName = lastName;
	}

	public String getSID(){
		return SID;
	}

	public void setSID(String sID){
		SID = sID;
	}

	public Date getActiveFrom(){
		return activeFrom;
	}

	public void setActiveFrom(Date activeForm){
		this.activeFrom = activeForm;
	}

	public Date getActiveUntil(){
		return activeUntil;
	}

	public void setActiveUntil(Date activeUntil){
		this.activeUntil = activeUntil;
	}

	public Date getLastActivity(){
		return lastActivity;
	}

	public void setLastActivity(Date lastActivity){
		this.lastActivity = lastActivity;
	}

	public String geteMail(){
		return eMail;
	}

	public void seteMail(String eMail){
		this.eMail = eMail;
	}

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getUserName(){
		return userName;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public String getPassword(){
		return password;
	}

	public void setPassword(String password){
		this.password = password;
	}



	public List<Group> getGroups(){
		return groups;
	}

	public void setGroups(List<Group> groups){
		this.groups = groups;
	}

	public List<UserSetting> getSettings(){
		return settings;
	}

	public void setSettings(List<UserSetting> settings){
		this.settings = settings;
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

	public Integer get_active(){
		return _active;
	}

	public void set_active(Integer _active){
		this._active = _active;
	}

	public Boolean getActive(){
		if (_active == null)
			return false;
		return _active.intValue() != 0;
	}

	public void setActive(Boolean active){
		this._active = ((active == null) ? 0 : (active.booleanValue() ? 1 : 0));
	}

	public List<UserEdition> getUserEditions(){
		return userEditions;
	}

	public void setUserEditions(List<UserEdition> userEditions){
		this.userEditions = userEditions;
	}

	public String getEtlUser(){
		return etlUser;
	}

	public void setEtlUser(String etlUser){
		this.etlUser = etlUser;
	}

	public String getEtlPassword(){
		return etlPassword;
	}

	public void setEtlPassword(String etlPassword){
		this.etlPassword = etlPassword;
	}

	public List<UserActivity> getActivities(){
		return activities;
	}

	public void setActivities(List<UserActivity> activities){
		this.activities = activities;
	}

	public int compareTo(User o){
		if (this.getUserName() == null && (o == null || o.getUserName() == null)){
			return 0;
		} else if (this.getUserName() == null){
			return -1;
		} else if (o == null || o.getUserName() == null){
			return 1;
		}
		return getUserName().compareTo(o.getUserName());
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof User))
			return false;
		if (o == this)
			return true;
		User dt = (User) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{id=").append(id);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", SID='").append(SID).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
