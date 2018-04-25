/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.server.Ni3FTPHelper;

public class FTPModuleLister implements ModuleLister{
	private static final Logger log = Logger.getLogger(FTPModuleLister.class);
	private String modulesPath;

	public FTPModuleLister(String path){
		modulesPath = path;
	}

	@Override
	public List<String> list(){
		if (!modulesPath.endsWith("/"))
			modulesPath += "/";
		Ni3FTPHelper ftp = new Ni3FTPHelper(modulesPath);
		List<String> result = new ArrayList<String>();
		try{
			if (!ftp.connect())
				return result;
			result = ftp.list();
			ftp.disconnect();
		} catch (IOException e){
			log.error("Failed to list FTP directory", e);
		}
		return result;
	}

	@Override
	public boolean testPath(){
		boolean result = false;
		if (!modulesPath.endsWith("/"))
			modulesPath += "/";
		Ni3FTPHelper ftp = new Ni3FTPHelper(modulesPath);
		try{
			result = ftp.connect();
			ftp.disconnect();
		} catch (IOException e){
			log.error("Failed to list FTP directory", e);
		}
		return result;
	}

}
