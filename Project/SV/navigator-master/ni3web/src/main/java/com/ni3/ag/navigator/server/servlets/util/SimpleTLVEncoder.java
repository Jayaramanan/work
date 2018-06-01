/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.log4j.Logger;

public class SimpleTLVEncoder{
	private static final byte INT_TYPE = 1;
	private static final byte STRING_TYPE = 2;
	private static final byte FILE_TYPE = 127;

	private static final long INT4_LENGTH = 4;
	private static final Logger log = Logger.getLogger(SimpleTLVEncoder.class);
	private OutputStream destination;

	public SimpleTLVEncoder(OutputStream out){
		destination = out;
	}

	public void writeInt(int i) throws IOException{
		if (destination == null)
			throw new IOException("output stream is null");
		destination.write(INT_TYPE);
		destination.write(longToByteArray(INT4_LENGTH));
		destination.write(intToByteArray(i));
	}

	public void writeString(String name) throws IOException{
		if (destination == null)
			throw new IOException("output stream is null");
		if (name == null)
			name = "";
		destination.write(STRING_TYPE);
		byte[] buf = name.getBytes("UTF-8");
		destination.write(longToByteArray(buf.length));
		if (buf.length > 0)
			destination.write(buf);
	}

	public void writeFile(String name) throws IOException{
		FileInputStream fis = null;
		try{
			if (destination == null)
				throw new IOException("output stream is null");
			BufferedOutputStream bos = new BufferedOutputStream(destination, 100 * 1024);
			if (log.isDebugEnabled()){
				log.debug("open file " + name);
			}
			File f = new File(name);
			if (!f.canRead()){
				log.error("Cannot read module file");
				throw new IOException("CanRead for " + name + " returrned false");
			}
			log.debug("Opened");
			bos.write(FILE_TYPE);
			bos.write(longToByteArray(f.length()));
			log.debug("size and type written");
			fis = new FileInputStream(f);
			log.debug("stream opened: " + fis);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buf = new byte[100 * 1024];
			log.debug("sending");
			while (bis.available() > 0){
				int count = bis.read(buf);
				bos.write(buf, 0, count);
			}
			log.debug("sent");
			bis.close();
			log.debug("all closed");
			bos.flush();
		} finally{
			if (fis != null)
				fis.close();
		}
	}

	private static byte[] longToByteArray(long value){
		return new byte[] { (byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24),
		        (byte) (value >>> 32), (byte) (value >>> 40), (byte) (value >>> 48), (byte) (value >>> 56) };
	}

	private static byte[] intToByteArray(int value){
		return new byte[] { (byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24) };
	}
}
