/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class URLEx{
	private static final Logger log = Logger.getLogger(URLEx.class);
	URLConnection connection;
	OutputStreamWriter writer = null;
	BufferedReader reader = null;

	public URLEx(String url){
		try{
			connection = new URL(url).openConnection();
			log.debug("URL=" + url);
		} catch (MalformedURLException e){
			log.error(e);
		} catch (IOException e){
			log.error(e);
		}
	}

	public URLEx(boolean ThrowException, String url) throws MalformedURLException, IOException{
		connection = new URL(url).openConnection();
	}

	public void close(){
		closeInput();
	}

	public void closeInput(){
		try{
			if (reader != null)
				reader.close();
		} catch (IOException e){
			log.error("cant close connection input stream", e);
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
			log.error(e);
		}

		return s;
	}

	public void println(String s){
		if (writer == null)
			HTTPPut();
		try{
			writer.write(s + "\n");
		} catch (IOException e){
			log.error(e);
		}

	}

	public void print(String s){
		if (writer == null)
			HTTPPut();
		try{
			writer.write(s);
		} catch (IOException e){
			log.error(e);
		}

	}

	public boolean HTTPRead(){
		try{
			// reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		} catch (IOException e){
			log.error(e);
			return false;
		}
		return true;
	}

	void HTTPPut(){
		try{
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setDefaultUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
		} catch (MalformedURLException e){
			log.error(e);
		} catch (IOException e){
			log.error(e);
		}
	}

	public Object getConnection(){
		return connection;
	}
}
