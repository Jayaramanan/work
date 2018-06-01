/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.remoting.httpinvoker.SimpleHttpInvokerRequestExecutor;

public class ACHttpInvokerRequestExecutor extends SimpleHttpInvokerRequestExecutor{

	@Override
	protected InputStream decorateInputStream(InputStream is) throws IOException{
		return new GZIPInputStream(is);
	}

	@Override
	protected OutputStream decorateOutputStream(OutputStream os) throws IOException{
		return new GZIPOutputStream(os);
	}
}
