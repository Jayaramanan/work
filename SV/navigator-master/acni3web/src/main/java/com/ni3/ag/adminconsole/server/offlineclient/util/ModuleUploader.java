/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.IOException;
import java.io.InputStream;

import com.ni3.ag.adminconsole.validation.ACException;

public interface ModuleUploader{
	void uploadModule(InputStream is, String fileName) throws ACException;

	void uploadModule(String backupFile) throws ACException;

	boolean ping() throws IOException;

	boolean fileExists(String fileName) throws IOException;
}
