/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class LocalModuleUploader implements ModuleUploader{
	private static final Logger log = Logger.getLogger(LocalModuleUploader.class);
	private String modulePath;

	public LocalModuleUploader(String modulePath){
		this.modulePath = modulePath;
	}

	@Override
	public void uploadModule(InputStream is, String fileName) throws ACException{
		String path = modulePath;
		if (!path.endsWith(File.separator))
			path += File.separator;

		File f = new File(path + fileName);

		FileOutputStream fos = null;
		try{
			f.createNewFile();
			fos = new FileOutputStream(f);
			int bytesRead = 0;
			while (bytesRead >= 0){
				byte[] chunkBuf = new byte[102400];
				bytesRead = is.read(chunkBuf);
				if (bytesRead != -1)
					fos.write(chunkBuf, 0, bytesRead);
			}
			fos.flush();
		} catch (Exception ex){
			log.error("Error uploading file", ex);
			throw new ACException(TextID.MsgEmpty, new String[] { ex + " | " + ex.getMessage() });
		} finally{
			if (fos != null)
				try{
					fos.close();
				} catch (IOException ex){
					throw new ACException(TextID.MsgEmpty, new String[] { ex + " | " + ex.getMessage() });
				}
		}
	}

	@Override
	public void uploadModule(String backupFile){
		File src = new File(backupFile);
		String path = modulePath;
		if (!path.endsWith(File.separator))
			path += File.separator;
		File dest = new File(path + backupFile);
		moveFile(src, dest);
	}

	@Override
	public boolean ping() throws IOException{
		String path = modulePath;
		if (!path.endsWith(File.separator))
			path += File.separator;
		File dest = new File(path);
		return dest.exists();
	}

	@Override
	public boolean fileExists(String fileName) throws IOException{
		if (!ping()){
			throw new IOException("Directory doesn't exist");
		}
		String path = modulePath;
		if (!path.endsWith(File.separator))
			path += File.separator;
		File file = new File(path + fileName);
		return file.exists();
	}

	private void moveFile(File srcFile, File dstFile){
		if (log.isDebugEnabled()){
			log.debug("Moving file from " + srcFile + " to " + dstFile);
		}
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new FileInputStream(srcFile);

			out = new FileOutputStream(dstFile);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			out.flush();

			srcFile.delete();

			if (log.isDebugEnabled()){
				log.debug("File moved successfully");
			}
		} catch (FileNotFoundException ex){
			log.error("Error copying file " + srcFile + " to " + dstFile, ex);
		} catch (IOException e){
			log.error("Error copying file", e);
		} finally{
			try{
				if (in != null){
					in.close();
				}
				if (out != null){
					out.close();
				}
			} catch (IOException e){
				log.error("Error closing stream", e);
			}

		}
	}
}
