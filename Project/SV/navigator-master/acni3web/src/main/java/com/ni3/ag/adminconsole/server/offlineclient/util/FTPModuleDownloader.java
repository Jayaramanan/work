/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.server.Ni3FTPHelper;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class FTPModuleDownloader implements ModuleDownloader{
	private static final Logger log = Logger.getLogger(FTPModuleDownloader.class);
	private String modulePath;

	public FTPModuleDownloader(String path){
		modulePath = path;
	}

	@Override
	public void downloadModule(OutputStream os, String fileName) throws ACException{
		if (!modulePath.endsWith("/"))
			modulePath += "/";
		Ni3FTPHelper ftpHelper = new Ni3FTPHelper(modulePath);
		try{
			ftpHelper.connect();
			ftpHelper.downloadFile(os, fileName);
			ftpHelper.disconnect();
		} catch (IOException e){
			log.error("Error connecting to server " + modulePath, e);
			throw new ACException(TextID.MsgEmpty, new String[] { "Error connect to server: " + modulePath + "\n" + e
			        + " | " + e.getMessage() });
		}
	}

	@Override
	public long getFileLength(String fileName) throws ACException{
		Ni3FTPHelper ftpHelper = new Ni3FTPHelper(modulePath);
		long len = -1;
		try{
			ftpHelper.connect();
			len = ftpHelper.getFileLength(fileName);

		} catch (IOException e){
			log.error("Error connecting to server " + modulePath, e);
			throw new ACException(TextID.MsgEmpty, new String[] { "Error connect to server: " + modulePath + "\n" + e
			        + " | " + e.getMessage() });
		} finally{
			ftpHelper.disconnect();
		}
		return len;
	}

}
