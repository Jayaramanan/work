/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.server.Ni3FTPHelper;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class FTPModuleUploader implements ModuleUploader{
	private static final Logger log = Logger.getLogger(FTPModuleUploader.class);
	private String modulePath;

	public FTPModuleUploader(String modulePath){
		this.modulePath = modulePath;
	}

	@Override
	public void uploadModule(InputStream is, String fileName) throws ACException{
		String path = modulePath;
		if (!path.endsWith("/"))
			path += "/";
		Ni3FTPHelper ftpHelper = new Ni3FTPHelper(path);
		try{
			if (!ftpHelper.connect())
				throw new ACException(TextID.MsgEmpty, new String[] { "Error connect to server: " + path });
			ftpHelper.uploadFile(fileName, is);
			ftpHelper.disconnect();
		} catch (IOException e){
			log.error("Error connecting to server " + path, e);
			throw new ACException(TextID.MsgEmpty, new String[] { "Error connect to server: " + path + "\n" + e + " | "
			        + e.getMessage() });
		}
	}

	@Override
	public void uploadModule(String backupFile) throws ACException{
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(backupFile);
			uploadModule(fis, backupFile);
		} catch (IOException e){
			log.error("Error connecting to server " + modulePath, e);
			throw new ACException(TextID.MsgEmpty, new String[] { "Error connect to server: " + modulePath + "\n" + e
			        + " | " + e.getMessage() });
		} finally{
			if (fis != null)
				try{
					fis.close();
				} catch (IOException e){
					log.error("Error closing FileInputStream", e);
				}
		}
	}

	@Override
	public boolean ping() throws IOException{
		String path = modulePath;
		if (!path.endsWith("/"))
			path += "/";
		Ni3FTPHelper ftpHelper = new Ni3FTPHelper(path);
		if (ftpHelper.connect()){
			ftpHelper.disconnect();
			return true;
		}
		return false;
	}

	@Override
	public boolean fileExists(String fileName) throws IOException{
		String path = modulePath;
		if (!path.endsWith("/"))
			path += "/";
		Ni3FTPHelper ftpHelper = new Ni3FTPHelper(path);
		boolean result = false;
		try{
			if (!ftpHelper.connect())
				throw new IOException("Cannot connect to ftp server");
			result = ftpHelper.fileExists(fileName, false);
			ftpHelper.disconnect();
		} catch (IOException e){
			log.error(e);
			throw e;
		}
		return result;
	}

}
