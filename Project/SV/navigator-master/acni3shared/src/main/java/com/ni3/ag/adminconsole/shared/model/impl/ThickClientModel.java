/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class ThickClientModel extends AbstractModel{
	private Map<DatabaseInstance, List<OfflineJob>> jobMap = new HashMap<DatabaseInstance, List<OfflineJob>>();
	private Map<DatabaseInstance, List<Group>> groupMap = new HashMap<DatabaseInstance, List<Group>>();
	private List<OfflineJob> deletedJobs = new ArrayList<OfflineJob>();
	private OfflineJob currentJob;

	public List<OfflineJob> getJobs(){
		return jobMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<OfflineJob>> getJobMap(){
		return jobMap;
	}

	public void setJobs(List<OfflineJob> jobs){
		jobMap.put(currentDatabaseInstance, jobs);
	}

	public List<Group> getGroups(){
		return groupMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<Group>> getGroupMap(){
		return groupMap;
	}

	public void setGroups(List<Group> groups){
		groupMap.put(currentDatabaseInstance, groups);
	}

	public boolean isInstanceLoaded(DatabaseInstance instance){
		return jobMap.containsKey(instance);
	}

	public List<OfflineJob> getDeletedJobs(){
		return deletedJobs;
	}

	public void clearDeletedJobs(){
		deletedJobs.clear();
	}

	public OfflineJob getCurrentJob(){
		return currentJob;
	}

	public void setCurrentJob(OfflineJob currentJob){
		this.currentJob = currentJob;
	}

}
