/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.datasource;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.validation.ACException;

public class InstanceDescriptor{
	private String dbid;
	private String dbType;
	private String navigatorHost;
	private String mapPath;
	private String docrootPath;
	private String rasterServer;
	private DeltaThreshold sysDeltaThreshold;
	private DeltaThreshold sysDeltaOutThreshold;
	private String currentDataSourceId;
	private List<String> dsIdentifiers;
	private List<String> dataSourceNames;
	private boolean visible;
	private String offlineModulePath;

	public InstanceDescriptor(String dbid, String navigatorHost){
		this.dbid = dbid;
		this.navigatorHost = navigatorHost;
		this.dsIdentifiers = new ArrayList<String>();
		this.dataSourceNames = new ArrayList<String>();
		visible = true;
	}

	public String getDBID(){
		return dbid;
	}

	public String getDataSourceName(){
		int index = dsIdentifiers.indexOf(currentDataSourceId);
		if (index < 0)
			index = 0;
		return dataSourceNames.get(index);
	}

	public List<String> getDataSourceNames(){
		return dataSourceNames;
	}

	public void setDatabaseType(String type){
		this.dbType = type;
	}

	public String getDatabaseType(){
		return dbType;
	}

	public String getNavigatorHost(){
		return navigatorHost;
	}

	public void addDataSource(String dataSourceName, String dsIdentifier){
		currentDataSourceId = dsIdentifier;
		dsIdentifiers.add(dsIdentifier);
		if (dataSourceName != null)
			dataSourceNames.add(dataSourceName);
	}

	public boolean isClusteredInstance(){
		return dsIdentifiers.size() > 1;
	}

	public String getDsIdentifier(){
		return currentDataSourceId;
	}

	public void setCurrentDsIdentifier(String dsid){
		if (dsIdentifiers.contains(dsid))
			currentDataSourceId = dsid;
	}

	public boolean hasDatasource(){
		return dsIdentifiers != null && !dsIdentifiers.isEmpty();
	}

	public List<String> getDsIdentifiers(){
		return dsIdentifiers;
	}

	public boolean isVisible(){
		return visible;
	}

	public void setVisible(boolean v){
		visible = v;
	}

	public String getMapPath(){
		return mapPath;
	}

	public void setMapPath(String mapPath){
		this.mapPath = mapPath;
	}

	public void setRasterServer(String rasterServer){
		this.rasterServer = rasterServer;
	}

	public String getRasterServer(){
		return rasterServer;
	}

	public void setDocrootPath(String docrootPath){
		this.docrootPath = docrootPath;
	}

	public String getDocrootPath(){
		return docrootPath;
	}

	public void setDeltaThreshold(String thresholdProperty) throws ACException{
		this.sysDeltaThreshold = new DeltaThreshold(thresholdProperty);
	}

	public DeltaThreshold getDeltaThreshold(){
		return sysDeltaThreshold;
	}

	public void setDeltaOutThreshold(String thresholdProperty) throws ACException{
		this.sysDeltaOutThreshold = new DeltaThreshold(thresholdProperty);
	}

	public DeltaThreshold getDeltaOutThreshold(){
		return sysDeltaOutThreshold;
	}

	public void setModulePath(String modulePath){
	    offlineModulePath = modulePath;	    
    }

	public String getModulePath(){
		return offlineModulePath;
    }

}
