/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ni3.ag.adminconsole.server.offlineclient.util.FTPModuleDownloader;
import com.ni3.ag.adminconsole.server.offlineclient.util.FTPModuleUploader;
import com.ni3.ag.adminconsole.server.offlineclient.util.LocalModuleDownloader;
import com.ni3.ag.adminconsole.server.offlineclient.util.LocalModuleUploader;
import com.ni3.ag.adminconsole.server.offlineclient.util.ModuleDownloader;
import com.ni3.ag.adminconsole.server.offlineclient.util.ModuleUploader;
import com.ni3.ag.adminconsole.validation.ACException;

public class TransferUtilsImpl implements TransferUtils{

	@Override
	public void uploadFile(String modulePath, InputStream is, String name) throws ACException{
		ModuleUploader uploader = null;
		if (modulePath.startsWith("ftp://"))
			uploader = new FTPModuleUploader(modulePath);
		else
			uploader = new LocalModuleUploader(modulePath);
		uploader.uploadModule(is, name);
	}

	@Override
	public void downloadFile(String modulePath, OutputStream os, String module) throws ACException{
		ModuleDownloader downloader = null;
		if (modulePath.startsWith("ftp://"))
			downloader = new FTPModuleDownloader(modulePath);
		else
			downloader = new LocalModuleDownloader(modulePath);
		downloader.downloadModule(os, module);
	}

	@Override
	public long getDownloadableFileLength(String modulePath, String module) throws ACException{
		ModuleDownloader downloader = null;
		if (modulePath.startsWith("ftp://"))
			downloader = new FTPModuleDownloader(modulePath);
		else
			downloader = new LocalModuleDownloader(modulePath);
		return downloader.getFileLength(module);
	}

	@Override
	public void uploadFile(String modulePath, String backupFile) throws ACException{
		ModuleUploader uploader = null;
		if (modulePath.startsWith("ftp://"))
			uploader = new FTPModuleUploader(modulePath);
		else
			uploader = new LocalModuleUploader(modulePath);
		uploader.uploadModule(backupFile);
	}

	@Override
	public boolean ping(String modulePath) throws IOException{
		ModuleUploader uploader = null;
		if (modulePath.startsWith("ftp://"))
			uploader = new FTPModuleUploader(modulePath);
		else
			uploader = new LocalModuleUploader(modulePath);
		return uploader.ping();
	}

	@Override
	public boolean fileExists(String modulePath, String fileName) throws IOException{
		ModuleUploader uploader = null;
		if (modulePath.startsWith("ftp://"))
			uploader = new FTPModuleUploader(modulePath);
		else
			uploader = new LocalModuleUploader(modulePath);
		return uploader.fileExists(fileName);
	}
}
