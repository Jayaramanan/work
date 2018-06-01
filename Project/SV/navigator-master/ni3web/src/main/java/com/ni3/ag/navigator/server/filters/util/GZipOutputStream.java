/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.filters.util;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GZipOutputStream extends ServletOutputStream{

	private OutputStream stream;

	public GZipOutputStream(final OutputStream stream){
		this.stream = stream;
	}

	/**
	 * Writes to the stream.
	 */
	public void write(final int b) throws IOException{
		stream.write(b);
	}

	/**
	 * Writes to the stream.
	 */
	public void write(final byte[] b) throws IOException{
		stream.write(b);
	}

	/**
	 * Writes to the stream.
	 */
	public void write(final byte[] b, final int off, final int len) throws IOException{
		stream.write(b, off, len);
	}
}
