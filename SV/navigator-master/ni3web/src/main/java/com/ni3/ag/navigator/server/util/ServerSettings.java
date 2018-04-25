/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.util;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class ServerSettings{
	private static final Logger log = Logger.getLogger(ServerSettings.class);

	private static final String PROPERTY_FILE_NAME = "/Ni3Web.properties";

	public static Properties loadPropertyFile(){
		Properties prop = new Properties();
		try{
			prop.load(ServerSettings.class.getResourceAsStream(PROPERTY_FILE_NAME));
			log.info("Loading datasource parameters from " + PROPERTY_FILE_NAME);
		} catch (IOException e){
			log.error("IOException, could not load property file " + PROPERTY_FILE_NAME, e);
		}
		return prop;
	}
}
