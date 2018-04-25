/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.swing.SwingWorker;

import com.ni3.ag.adminconsole.client.session.SessionData;

public class UploadTask extends SwingWorker<Void, Void>{

	private String servletUrl;
	private File file;
	private OutputStream os = null;
	private FileInputStream fis = null;

	public UploadTask(String servletUrl, File file){
		this.servletUrl = servletUrl;
		this.file = file;
	}

	public boolean ping(){
		try{
			String params = "?action=Ping&DBID=" + SessionData.getInstance().getCurrentDatabaseInstanceId();
			URL url = new URL(servletUrl + params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.getInputStream();
		} catch (IOException e){
			return false;
		}
		return true;
	}

	public boolean fileExists() throws IOException{
		boolean exists = false;
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader in = null;
		try{
			String params = "?action=CheckFileExistance&DBID=" + SessionData.getInstance().getCurrentDatabaseInstanceId()
			        + "&module=" + URLEncoder.encode(file.getName(), "UTF-8");
			URL url = new URL(servletUrl + params);
			connection = (HttpURLConnection) url.openConnection();
			is = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(is));
			String inputLine;
			if ((inputLine = in.readLine()) != null){
				exists = "true".equals(inputLine);
			}
		} finally{
			if (is != null){
				is.close();
			}
			if (in != null)
				in.close();
		}
		return exists;
	}

	@Override
	protected Void doInBackground() throws Exception{
		setProgress(0);

		try{
			URL url = new URL(servletUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setChunkedStreamingMode(102400);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("module", file.getName());
			connection.setRequestProperty("DBID", SessionData.getInstance().getCurrentDatabaseInstanceId());
			os = connection.getOutputStream();
			fis = new FileInputStream(file);

			long contLen = file.length();
			long totalBytesRead = 0;
			int bytesRead = 0;
			while (bytesRead >= 0 && !isCancelled()){

				byte[] chunkBuf = new byte[100 * 1024];
				bytesRead = fis.read(chunkBuf);
				if (bytesRead != -1){
					os.write(chunkBuf, 0, bytesRead);
					os.flush();
				}
				totalBytesRead += bytesRead;
				int percent = (int) (totalBytesRead * 100.0 / contLen);
				setProgress(percent);
			}
			setProgress(100);
			connection.getInputStream();
		} catch (IOException e){
			firePropertyChange("error", null, e.getMessage());
		} finally{
			if (os != null){
				os.flush();
				os.close();
			}
			if (fis != null)
				fis.close();
		}
		return null;
	}

	public boolean cancelTask(boolean force) throws IOException{
		boolean ok = super.cancel(force);
		if (ok){
			if (os != null){
				os.flush();
				os.close();
			}
			if (fis != null)
				fis.close();
		}
		return ok;
	}
}
