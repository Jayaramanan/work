/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class LocalModuleDownloader implements ModuleDownloader{
	private static final Logger log = Logger.getLogger(LocalModuleDownloader.class);
	private String modulePath;

	public LocalModuleDownloader(String path){
		modulePath = path;
	}

	@Override
	public void downloadModule(OutputStream os, String fileName) throws ACException{
		if (!modulePath.endsWith(File.separator))
			modulePath += File.separator;

		FileInputStream fis = null;
		try{
			File f = new File(modulePath + fileName);
			if (!f.exists())
				throw new ACException(TextID.MsgEmpty, new String[] { "file `" + f.getCanonicalPath() + "` does not exist" });
			long fileLength = f.length();
			fis = new FileInputStream(f);
			int bytesWritten = 0;
			int chunkSize = 102400;
			while (bytesWritten < fileLength){
				int len = (int) Math.min((long) chunkSize, fileLength - bytesWritten);
				byte[] readBuf = new byte[len];
				int bytesRead = fis.read(readBuf);
				os.write(readBuf, 0, bytesRead);
				bytesWritten += len;
			}
		} catch (IOException e){
			log.error("Download local file failed", e);
			throw new ACException(TextID.MsgEmpty, new String[] { "" + e + ":" + e.getMessage() });
		} finally{
			if (fis != null)
				try{
					fis.close();
				} catch (IOException ex){
					log.error("Error closing FileInputStream", ex);
				}
		}
	}

	@Override
	public long getFileLength(String fileName) throws ACException{
		if (!modulePath.endsWith(File.separator))
			modulePath += File.separator;
		long fileLength = -1;
		try{
			File f = new File(modulePath + fileName);
			if (!f.exists())
				throw new ACException(TextID.MsgEmpty, new String[] { "file `" + f.getCanonicalPath() + "` does not exist" });
			fileLength = f.length();
		} catch (IOException e){
			log.error("Error retrieving local file: " + fileName, e);
			throw new ACException(TextID.MsgEmpty, new String[] { "" + e + ":" + e.getMessage() });
		}
		return fileLength;
	}
}
