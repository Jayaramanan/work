/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.OutputStream;

import com.ni3.ag.adminconsole.validation.ACException;

public interface ModuleDownloader{
	void downloadModule(OutputStream os, String fileName) throws ACException;

	long getFileLength(String fileName) throws ACException;
}
