/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.lifecycle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.shared.service.DataGroup;

public class CacheInvalidationStore{

	private static CacheInvalidationStore instance;

	private Map<String, Set<DataGroup>> invalidationSet;

	private CacheInvalidationStore(){
		invalidationSet = new HashMap<String, Set<DataGroup>>();
	}

	public static synchronized CacheInvalidationStore getInstance(){
		if (instance == null)
			instance = new CacheInvalidationStore();
		return instance;
	}

	public void setInvalidationRequired(DataGroup gr, Boolean required){
		String dbid = getDatabaseIdentifier();
		check(dbid);
		if (required){
			invalidationSet.get(dbid).add(gr);
		} else{
			invalidationSet.get(dbid).remove(gr);
		}
	}

	public boolean isInvalidationRequired(DataGroup gr){
		String dbid = getDatabaseIdentifier();
		check(dbid);
		return invalidationSet.get(dbid).contains(dbid);
	}

	public boolean isAnyInvalidationRequired(){
		String dbid = getDatabaseIdentifier();
		check(dbid);
		return !invalidationSet.get(dbid).isEmpty();
	}

	public void resetAnyInvalidationRequired(){
		String dbid = getDatabaseIdentifier();
		invalidationSet.put(dbid, new HashSet<DataGroup>());
	}

	public Set<DataGroup> getAllInvalidationRequered(){
		String dbid = getDatabaseIdentifier();
		check(dbid);
		return invalidationSet.get(dbid);
	}

	private void check(String dbid){
		if (!invalidationSet.containsKey(dbid))
			invalidationSet.put(dbid, new HashSet<DataGroup>());
	}

	private String getDatabaseIdentifier(){
		ThreadLocalStorage idStorage = ThreadLocalStorage.getInstance();
		String dbid = idStorage.getCurrentDatabaseInstanceId();
		return dbid;
	}
}
