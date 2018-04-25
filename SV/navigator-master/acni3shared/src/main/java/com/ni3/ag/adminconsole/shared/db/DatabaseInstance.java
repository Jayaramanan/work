/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.db;

import java.io.Serializable;
import java.util.List;

import com.ni3.ag.adminconsole.domain.User;

public class DatabaseInstance implements Serializable{
	private static final long serialVersionUID = 4645740447473452916L;
	private String instanceId;
	private boolean connected = false;
	/* Inited instance is the one that has been added to the routing datasource on the server */
	private boolean inited = false;

	private User instanceUser;
	private String tabSwitchDefaultAction;
	private String navigatorHost;
	private String mapPath;
	private String docrootPath;
	private String rasterServerUrl;
	private String deltaThreshold;
	private String deltaOutThreshold;
	private List<String> datasourceNames;
	private String modulePath;

	public DatabaseInstance(String instanceId){
		this.instanceId = instanceId;
	}

	public void setInited(boolean inited){
		this.inited = inited;
	}

	public boolean isInited(){
		return inited;
	}

	public void setUser(User user){
		this.instanceUser = user;
	}

	public User getUser(){
		return instanceUser;
	}

	public boolean isConnected(){
		return connected;
	}

	/**
	 * This should not be used; use SessionData.setDatabaseInstanceConnected instead
	 * 
	 * @param connected
	 */
	@Deprecated
	public void setConnected(boolean connected){
		this.connected = connected;
	}

	@Override
	public String toString(){
		return instanceId;
	}

	public String getDatabaseInstanceId(){
		return instanceId;
	}

	public String getTabSwitchDefaultAction(){
		return tabSwitchDefaultAction;
	}

	public void setTabSwitchDefaultAction(String tabSwitchDefaultAction){
		this.tabSwitchDefaultAction = tabSwitchDefaultAction;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		if (instanceId == null || !(obj instanceof DatabaseInstance)){
			return false;
		}
		return instanceId.equals(((DatabaseInstance) obj).toString());
	}

	public void setNavigatorHost(String navigatorHost){
		this.navigatorHost = navigatorHost;
	}

	public String getNavigatorHost(){
		return navigatorHost;
	}

	public void setDatasourceNames(List<String> datasourceNames){
		this.datasourceNames = datasourceNames;
	}

	public List<String> getDatasourceNames(){
		return datasourceNames;
	}

	public String getMapPath(){
		return mapPath;
	}

	public void setMapPath(String mapPath){
		this.mapPath = mapPath;
	}

	public String getDocrootPath(){
		return docrootPath;
	}

	public void setDocrootPath(String path){
		this.docrootPath = path;
	}

	public void setRasterServerUrl(String rasterServerUrl){
		this.rasterServerUrl = rasterServerUrl;
	}

	public String getRasterServerUrl(){
		return rasterServerUrl;
	}

	public String getDeltaThreshold(){
		return deltaThreshold;
	}

	public void setDeltaThreshold(String deltaThreshold){
		this.deltaThreshold = deltaThreshold;
	}

	public void setDeltaOutThreshold(String deltaOutThreshold){
		this.deltaOutThreshold = deltaOutThreshold;
	}

	public String getDeltaOutThreshold(){
		return deltaOutThreshold;
	}

	public void setInstanceId(String id){
		this.instanceId = id;
	}

	public void setModulePath(String modulePath){
		this.modulePath = modulePath;
    }
	
	public String getModulePath(){
		return modulePath;
	}
}
