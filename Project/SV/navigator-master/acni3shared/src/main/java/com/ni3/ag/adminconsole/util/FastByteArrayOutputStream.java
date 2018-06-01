/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.util;

import java.io.InputStream;
import java.io.OutputStream;

public class FastByteArrayOutputStream extends OutputStream{
	private static int INITIAL = 20 * 1024;
	private static int GROW_BY = 5 * 1024;
	protected byte[] buf = null;
	protected int size = 0;

	/**
	 * Constructs a stream with buffer capacity size 5K
	 */
	public FastByteArrayOutputStream(){
		this(INITIAL);
	}

	/**
	 * Constructs a stream with the given initial size
	 */
	public FastByteArrayOutputStream(int initSize){
		this.size = 0;
		this.buf = new byte[initSize];
	}

	/**
	 * Ensures that we have a large enough buffer for the given size.
	 */
	private void verifyBufferSize(int sz){
		if (sz > buf.length){
			byte[] old = buf;
			buf = new byte[Math.max(sz, buf.length + GROW_BY)];
			System.arraycopy(old, 0, buf, 0, old.length);
			old = null;
		}
	}

	public int getSize(){
		return size;
	}

	/**
	 * Returns the byte array containing the written data. Note that this array will almost always be larger than the
	 * amount of data actually written.
	 */
	public byte[] getByteArray(){
		return buf;
	}

	public final void write(byte b[]){
		verifyBufferSize(size + b.length);
		System.arraycopy(b, 0, buf, size, b.length);
		size += b.length;
	}

	public final void write(byte b[], int off, int len){
		verifyBufferSize(size + len);
		System.arraycopy(b, off, buf, size, len);
		size += len;
	}

	public final void write(int b){
		verifyBufferSize(size + 1);
		buf[size++] = (byte) b;
	}

	public void reset(){
		size = 0;
	}

	/**
	 * Returns a ByteArrayInputStream for reading back the written data
	 */
	public InputStream getInputStream(){
		return new FastByteArrayInputStream(buf, size);
	}
}
