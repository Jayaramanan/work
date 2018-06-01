/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Module implements Serializable{
	public static final long serialVersionUID = 4699389755568675944L;

	public static final String NAME_COLUMN = "name";

	public static final String START_SCRIPTS = "Startup scripts";
	public static final String JETTY = "jetty";
	public static final String JETTY_CONFIG = "jetty config";
	public static final String GIS_EXECUTABLE = "GIS executable";
	public static final String GIS_CONFIG = "GIS config";
	public static final String MAPS = "Maps";
	public static final String JRE = "JRE";
	public static final String CLIENT_JAR = "Client jar";
	public static final String RDBMS_ENGINE = "RDBMS engine";
	public static final String SERVER_SIDE_WAR = "Server-side (war)";
	public static final String SERVER_CACHE = "server cache";
	public static final String DB_DATA = "DB data";
	public static final String DB_DUMP = "DB dump";
	public static final String DB_SCRIPT = "DB script";
	public static final String STARTER = "Starter";

	public static final String[] NAMES = { START_SCRIPTS, JETTY, JETTY_CONFIG, GIS_EXECUTABLE, GIS_CONFIG, MAPS, JRE,
	        CLIENT_JAR, RDBMS_ENGINE, SERVER_SIDE_WAR, SERVER_CACHE, DB_DATA, DB_DUMP, DB_SCRIPT, STARTER };

	public static final String DB_DUMP_PARAM_USER_ID = "db.dump.user.id";
	public static final String DB_DUMP_PARAM_MAX_USER_DELTA = "db.dump.delta.maxId";
	public static final String DB_DUMP_PARAM_LOCKED = "db.dump.module.locked";

	public static final Map<String, String> PARAMS = new HashMap<String, String>();
	static{
		PARAMS.put(DB_DUMP_PARAM_LOCKED, "int");
		PARAMS.put(DB_DUMP_PARAM_MAX_USER_DELTA, "int");
		PARAMS.put(DB_DUMP_PARAM_USER_ID, "int");
	}

	private Integer id;
	private String path;
	private String hash;
	private String version;
	private String archivePassword;
	private String name;
	private String params;
	private List<ModuleUser> currentUserModules;
	private List<ModuleUser> targetUserModules;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getPath(){
		return path;
	}

	public void setPath(String path){
		this.path = path;
	}

	public String getHash(){
		return hash;
	}

	public void setHash(String hash){
		this.hash = hash;
	}

	public String getVersion(){
		return version;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getArchivePassword(){
		return archivePassword;
	}

	public void setArchivePassword(String archivePassword){
		this.archivePassword = archivePassword;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getParams(){
		return params;
	}

	public void setParams(String params){
		this.params = params;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Module))
			return false;
		Module m = (Module) obj;
		return m.getId() != null && m.getId().equals(getId());
	}

	public List<ModuleUser> getCurrentUserModules(){
		return currentUserModules;
	}

	public void setCurrentUserModules(List<ModuleUser> currentUserModules){
		this.currentUserModules = currentUserModules;
	}

	public List<ModuleUser> getTargetUserModules(){
		return targetUserModules;
	}

	public void setTargetUserModules(List<ModuleUser> targetUserModules){
		this.targetUserModules = targetUserModules;
	}

	/**
	 * This method should return "name" as the first characters to allow key selection in drop-downs
	 * See AC-1459 
	 */
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(name).append("\n");
		sb.append("---------------MODULE--------------\n");
		sb.append("ID=").append(id).append("\n");
		sb.append("name=").append(name).append("\n");
		sb.append("Path=").append(path).append("\n");
		sb.append("Hash=").append(hash).append("\n");
		sb.append("Version=").append(version).append("\n");
		sb.append("arc pass=").append(archivePassword).append("\n");
		sb.append("***********************************\n");
		return sb.toString();
	}
}
