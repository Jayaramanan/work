/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.util;

import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.IAmDAO;

public class DatabaseVersionValidator{
	private static final Logger log = Logger.getLogger(DatabaseVersionValidator.class);
	private static final String DB_VERSION_PROPERTY = "com.ni3.ag.navigator.db.version";
	private static final String DATABASE_VERSION_FILE_NAME = "/database.version";
	private Properties properties;

	public DatabaseVersionValidator(){
	}

	public boolean validateDatabaseVersions(){
		String expected = getExpectedVersion();

		if (expected == null){
			log.error("Error! No expected version found in dabase.version config");
			return false;
		}

		return validateDatabaseVersion(expected);
	}

	private boolean validateDatabaseVersion(String expected){
		String actual = getActualVersion();
		if (actual == null){
			log.error("Error! No actual version found in sys_iam table");
			return false;
		}

		int[] realVersion = parseVersion(actual);
		int[] expectedVersion = parseVersion(expected);

		if (realVersion[0] != expectedVersion[0] || realVersion[1] != expectedVersion[1]){
			log.error("Error! Wrong database version: expected - " + expected + ", actual - " + actual);
			return false;
		} else if (realVersion[2] != expectedVersion[2]){
			log.warn("Warning! Different database version: expected - " + expected + ", actual - " + actual);
		} else{
			log.info("Expected version - " + expected + ", actual version - " + actual);
		}
		return true;
	}

	private int[] parseVersion(String version){
		StringTokenizer strt = new StringTokenizer(version, ".");
		String astr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String bstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		String cstr = strt.hasMoreTokens() ? strt.nextToken() : "-1";
		return new int[] { Integer.parseInt(astr), Integer.parseInt(bstr), Integer.parseInt(cstr) };
	}

	private String getActualVersion(){
		IAmDAO iamDAO = NSpringFactory.getInstance().getIAmDao();
		String version = iamDAO.getVersion();
		return version;
	}

	private String getExpectedVersion(){
		return getProperties().getProperty(DB_VERSION_PROPERTY);
	}

	private Properties getProperties(){
		if (properties != null){
			return properties;
		}
		properties = new Properties();
		String resourceName = DATABASE_VERSION_FILE_NAME;
		try{
			properties.load(ServerSettings.class.getResourceAsStream(resourceName));
		} catch (IOException e){
			log.error("database.version file not found", e);
		}
		return properties;
	}
}
