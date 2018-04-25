/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.filters.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

public class GZipResponseWrapper extends HttpServletResponseWrapper implements Serializable{

	private static final Logger log = Logger.getLogger(GZipResponseWrapper.class);

	private static final long serialVersionUID = 772285797317321297L;
	private ServletOutputStream outstr;
	private PrintWriter writer;

	public GZipResponseWrapper(final HttpServletResponse response, final OutputStream outstr){
		super(response);
		this.outstr = new GZipOutputStream(outstr);
	}

	/**
	 * Gets the outputstream.
	 */
	public ServletOutputStream getOutputStream(){
		return outstr;
	}

	/**
	 * Gets the print writer.
	 */
	public PrintWriter getWriter(){
		if (writer == null){
			Writer w = null;
			try{
				w = new BufferedWriter(new OutputStreamWriter(outstr, "UTF-8"));
			} catch (UnsupportedEncodingException e){
				log.error(e);
			}
			writer = new PrintWriter(w, true);
		}
		return writer;
	}

	/**
	 * Flushes buffer and commits response to client.
	 */
	public void flushBuffer() throws IOException{
		flush();
		super.flushBuffer();
	}

	/**
	 * Flushes all the streams for this response.
	 */
	public void flush() throws IOException{
		if (writer != null){
			writer.flush();
		}
		outstr.flush();
	}

}
