/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ni3.ag.adminconsole.validation.ACException;

public interface TransferUtils{
	public void uploadFile(String modulePath, InputStream is, String module) throws ACException;

	public void uploadFile(String tmpModulePath, String backupFile) throws ACException;

	public void downloadFile(String modulePath, OutputStream servletOutputStream, String module) throws ACException;

	public long getDownloadableFileLength(String modulePath, String module) throws ACException;

	public boolean ping(String modulePath) throws IOException;

	boolean fileExists(String modulePath, String fileName) throws IOException;
}
