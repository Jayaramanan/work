/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.util;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;

import com.ni3.ag.navigator.client.gateway.SessionStore;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;
import org.apache.log4j.Logger;

public class URLEx{
	private static final Logger log = Logger.getLogger(URLEx.class);
	private static final boolean COMPRESSION_ENABLED = true;
	private static final String COOKIE = "Cookie";
	private URLConnection connection;
	private OutputStreamWriter writer = null;
	private BufferedReader reader = null;
	private String url;
	private StringBuilder paramStr;

	public URLEx(ServletName servletName){
		this(SystemGlobals.ServerURL, servletName);
	}

	public URLEx(String serverUrl, ServletName servletName){
		this(serverUrl + servletName.getUrl());
	}

	public URLEx(String url){
		this.url = url;
		try{
			connection = new URL(url).openConnection();
			if (COMPRESSION_ENABLED)
				connection.setRequestProperty("Accept-Encoding", "gzip");
			final String cookie = SessionStore.getInstance().getSessionString();
			if (cookie != null){
				connection.setRequestProperty(COOKIE, cookie);
			}

			Utility.debugToConsole("URL=" + url);
		} catch (MalformedURLException e){
			log.error("Error connecting url: " + url, e);
		} catch (IOException e){
			log.error("Error connecting url: " + url, e);
		}
	}

	public boolean isConnected(){
		try{
			HTTPPut();
		} catch (IOException ex){
			log.error("", ex);
			return false;
		}
		return writer != null;
	}

	public void close(){
		closeInput();
	}

	public void closeOutput(String Log){
		if (log.isDebugEnabled())
			log.debug(Str.unescape(paramStr.toString()));

		print(getParamsString());

		try{
			if (writer != null)
				writer.close();
		} catch (IOException e){
			log.error(e);
		}
		writer = null;
	}

	public void closeOutput(){
		print(getParamsString());

		try{
			if (writer != null)
				writer.close();
		} catch (IOException e){
			log.error(e);
		}

		writer = null;
	}

	public void closeInput(){
		try{
			if (reader != null)
				reader.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		reader = null;
	}

	public String readLine(){
		String s = null;
		try{
			if (reader == null)
				HTTPRead();
			if (reader != null)
				s = reader.readLine();
		} catch (IOException e){
			e.printStackTrace();
		}

		if ("MsgPleaseRelogin".equals(s)){
			boolean ok = Ni3.relogin(SystemGlobals.getUser().getUserName());
			if (ok)
				s = reReadLine();
			else
				s = null;
		}
		if ("MsgReloadSchema".equals(s)){
			Ni3.reloadSchema();
			s = reReadLine();
		}
		if (s != null && s.startsWith("\b")){// unexpected protobuffer message in simple urlex?
			throw new RuntimeException("Server communication error: " + s);
		}
		return s;
	}

	private String reReadLine(){
		try{
			reader = null;
			connection = new URL(url).openConnection();
			if (COMPRESSION_ENABLED)
				connection.setRequestProperty("Accept-Encoding", "gzip");
			final String cookie = SessionStore.getInstance().getSessionString();
			if (cookie != null){
				connection.setRequestProperty("Cookie", cookie);
			}
			closeOutput();
			return readLine();
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	private void print(String paramsString){
		try{
			if (writer == null)
				HTTPPut();
			writer.write(paramsString);
		} catch (IOException e){
			log.error("", e);
			log.error("No connection to server: " + SystemGlobals.ServerURL);
			SystemGlobals.MainFrame.showErrorMessage("Cannot connect to the server");
		}
	}

	public boolean HTTPRead(){
		try{
			InputStream is = getInputStream();
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		} catch (Exception e){
			return false;
		}
		return true;
	}

	void HTTPPut() throws IOException{
		try{
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		} catch (MalformedURLException e){
			log.error("Error in URL", e);
			throw e;
		} catch (IOException e){
			log.error("Unable to connect to server: url = " + url + ", error = " + e.getMessage(), e);
			throw e;
		}
	}

	public InputStream getInputStream() throws IOException{
		InputStream is = connection.getInputStream();
		if (COMPRESSION_ENABLED){
			String contentEncoding = connection.getHeaderField("Content-Encoding");
			boolean isGZipped = contentEncoding != null && "gzip".equalsIgnoreCase(contentEncoding);
			if (isGZipped){
				is = new GZIPInputStream(is);
				log.debug("Zipped content received from " + url);
			}
		}
		return is;
	}

	public URLEx addParam(RequestParam param, Object value){
		if (paramStr == null){
			paramStr = new StringBuilder();
		} else{
			paramStr.append("&");
		}
		String paramName = param.name();

		// TODO: Character escaping was removed from here to fix NAV-1080. Needs to be checked.
		String escapedValue = (value == null) ? "null" : value.toString();
		paramStr.append(paramName).append("=").append(escapedValue);

		return this;
	}

	public URLEx addStr(String str){
		if (paramStr == null){
			paramStr = new StringBuilder();
		} else{
			paramStr.append("&");
		}
		paramStr.append(str);

		return this;
	}

	private String getParamsString(){
		return paramStr != null ? paramStr.toString() : "";
	}
}
