/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.swing.SwingWorker;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.export.ProgressDialog;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;

public class XLSDataExporter{
	private static final Logger log = Logger.getLogger(XLSDataExporter.class);

	private ProgressDialog progressDialog = null;

	public XLSDataExporter(){
		progressDialog = new ProgressDialog(UserSettings.getWord("Export"), 3);
	}

	public String exportData(final File file, final int schemaParam, final String nodeParam, final String edgeParam){
		String result = null;
		SwingWorker<String, Void> worker = new SwingWorker<String, Void>(){

			@Override
			protected String doInBackground() throws Exception{
				return export(file, schemaParam, nodeParam, edgeParam);
			}

			@Override
			protected void done(){
				String result = null;
				try{
					result = get();
				} catch (Exception e){
					log.error(e);
				}

				if (result == null){
					progressDialog.setFinished();
				} else
					progressDialog.dispose();

			}

		};
		worker.execute();
		progressDialog.setVisible(true);

		try{
			result = worker.get();
		} catch (Exception e){
			log.error(e);
		}

		return result;
	}

	private String export(File file, int schemaId, String nodeParam, String edgeParam){
		String error = null;
		URLEx url = new URLEx(ServletName.ExportProvider);
		url.addParam(RequestParam.XLSExport, "A");
		url.addParam(RequestParam.SCHID, schemaId);
		url.addParam(RequestParam.Nodes, nodeParam);
		url.addParam(RequestParam.Edges, edgeParam);
		url.addParam(RequestParam.DateFormat, SystemGlobals.DateFormat);
		url.closeOutput(null);
		try{
			InputStream is = url.getInputStream();
			byte[] fileData = getBytes(is);
			if (fileData == null || fileData.length == 0){
				error = "MsgNoDataFoundForExport";
			} else{
				if (!file.getName().endsWith(".xls"))
					file = new File(file.getAbsolutePath() + ".xls");
				file.createNewFile();
				FileOutputStream fs = new FileOutputStream(file);
				fs.write(fileData);
				fs.close();
			}
		} catch (MalformedURLException e){
			log.error("Fatal Error, can not export data to xls");
			error = "MsgFailedToMakeUserDataExport";
		} catch (IOException e){
			log.error("Fatal Error, can not export data to xls");
			error = "MsgFailedToMakeUserDataExport";
		}
		url.close();

		return error != null ? UserSettings.getWord(error) : null;
	}

	private static byte[] getBytes(InputStream is) throws IOException{
		int len;
		int size = 1024;
		byte[] buf;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		buf = new byte[size];
		while ((len = is.read(buf, 0, size)) != -1)
			bos.write(buf, 0, len);
		buf = bos.toByteArray();
		return buf;
	}

}
