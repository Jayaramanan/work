/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class MapJobModel extends AbstractModel{
	private Map<DatabaseInstance, List<MapJob>> jobMap = new HashMap<DatabaseInstance, List<MapJob>>();
	private Map<DatabaseInstance, List<User>> userMap = new HashMap<DatabaseInstance, List<User>>();
	private Map<DatabaseInstance, com.ni3.ag.adminconsole.domain.Map> mapsMap = new HashMap<DatabaseInstance, com.ni3.ag.adminconsole.domain.Map>();
	private Map<DatabaseInstance, String> rasterServerMap = new HashMap<DatabaseInstance, String>();
	private Map<DatabaseInstance, Map<Integer, String>> userZoomMap = new HashMap<DatabaseInstance, Map<Integer, String>>();
	private List<MapJob> deletedJobs = new ArrayList<MapJob>();
	private MapJob currentJob;

	public List<MapJob> getJobs(){
		return jobMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<MapJob>> getJobMap(){
		return jobMap;
	}

	public void setJobs(List<MapJob> jobs){
		jobMap.put(currentDatabaseInstance, jobs);
	}

	public List<User> getUsers(){
		return userMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<User>> getUserMap(){
		return userMap;
	}

	public void setUsers(List<User> users){
		userMap.put(currentDatabaseInstance, users);
	}

	public String getRasterServer(){
		return rasterServerMap.get(currentDatabaseInstance);
	}

	public void setRasterServer(String rasterServer){
		rasterServerMap.put(currentDatabaseInstance, rasterServer);
	}

	public Map<Integer, String> getUserZooms(){
		return userZoomMap.get(currentDatabaseInstance);
	}

	public void setUserZooms(Map<Integer, String> map){
		userZoomMap.put(currentDatabaseInstance, map);
	}

	public com.ni3.ag.adminconsole.domain.Map getMap(){
		return mapsMap.get(currentDatabaseInstance);
	}

	public void setMap(com.ni3.ag.adminconsole.domain.Map map){
		mapsMap.put(currentDatabaseInstance, map);
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return jobMap.containsKey(instance);
	}

	public List<MapJob> getDeletedJobs(){
		return deletedJobs;
	}

	public void clearDeletedJobs(){
		deletedJobs.clear();
	}

	public MapJob getCurrentJob(){
		return currentJob;
	}

	public void setCurrentJob(MapJob currentJob){
		this.currentJob = currentJob;
	}

}
