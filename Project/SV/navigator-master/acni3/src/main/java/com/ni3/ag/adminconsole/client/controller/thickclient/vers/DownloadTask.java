/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.swing.SwingWorker;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Module;

public class DownloadTask extends SwingWorker<Void, Void>{

	private Module module;
	private String servletUrl;
	private File localFile;
	private InputStream is = null;
	private FileOutputStream fos = null;

	public DownloadTask(Module m, String servletUrl, File localFile){
		this.module = m;
		this.servletUrl = servletUrl;
		this.localFile = localFile;
	}

	public boolean ping(){
		try{
			String params = "?action=Ping&DBID=" + SessionData.getInstance().getCurrentDatabaseInstanceId();
			URL url = new URL(servletUrl + params);
			URLConnection connection = url.openConnection();
			connection.getInputStream();
		} catch (IOException e){
			firePropertyChange("error", null, e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	protected Void doInBackground() throws IOException{
		setProgress(0);
		try{
			fos = new FileOutputStream(localFile);
		} catch (IOException e){
			firePropertyChange("error_save", null, e.getMessage());
		}
		try{
			String params = "?action=DownloadModule&DBID=" + SessionData.getInstance().getCurrentDatabaseInstanceId()
			        + "&module=" + URLEncoder.encode(module.getPath(), "UTF-8");

			URL url = new URL(servletUrl + params);
			URLConnection connection = url.openConnection();

			is = connection.getInputStream();

			boolean showProgress = true;
			int contLen = connection.getContentLength();
			if (contLen == -1)
				showProgress = false;

			long totalBytesRead = 0;
			int bytesRead = 0;
			while (bytesRead >= 0){
				byte[] chunkBuf = new byte[102400];
				bytesRead = is.read(chunkBuf);
				if (bytesRead != -1)
					fos.write(chunkBuf, 0, bytesRead);
				totalBytesRead += bytesRead;
				if (showProgress){
					int percent = (int) (totalBytesRead * 100.0 / contLen);
					setProgress(percent);
				}
			}
			setProgress(100);
		} catch (IOException e){
			firePropertyChange("error_download", null, e.getMessage());
		} finally{
			if (fos != null){
				fos.flush();
				fos.close();
			}
			if (is != null)
				is.close();
		}
		return null;
	}

	public boolean cancelTask(boolean force) throws IOException{
		boolean ok = super.cancel(force);
		if (ok){
			if (fos != null){
				fos.flush();
				fos.close();
			}
			if (is != null)
				is.close();
		}
		return ok;
	}

}
