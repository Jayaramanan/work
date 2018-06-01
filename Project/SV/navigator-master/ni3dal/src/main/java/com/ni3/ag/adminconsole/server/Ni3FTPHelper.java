/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Ni3FTPHelper{

	public static final String FTP_BASE_LOCAL_DIRECTORY = "./temp/";

	private static final Logger log = Logger.getLogger(Ni3FTPHelper.class);

	private FTPClient ftp;
	private String user, password, host;
	private int port;
	private String workingFolder;

	public Ni3FTPHelper(String mapPath){
		ftp = new FTPClient();

		File tempDir = new File(FTP_BASE_LOCAL_DIRECTORY);
		if (!tempDir.exists())
			tempDir.mkdir();

		try{
			URL url = new URL(mapPath);
			user = url.getUserInfo();
			if (user == null)
				user = "";
			int index = user.indexOf(":");
			if (index != -1){
				password = user.substring(index + 1);
				user = user.substring(0, index);
			}
			if (user == null)
				user = "anonymous";
			if (user.isEmpty())
				user = "anonymous";
			host = url.getHost();
			port = url.getPort();
			if (port == 0 || port == -1)
				port = 21;
			workingFolder = url.getPath();
			log.debug("user: " + user);
			log.debug("host: " + host);
			log.debug("port: " + port);
			log.debug("working directory: " + workingFolder);
		} catch (MalformedURLException ex){
			log.error("error parsing url", ex);
		}
	}

	public boolean connect() throws IOException{
		ftp.connect(host, port);
		if (!ftp.login(user, password))
			return false;
		return true;
	}

	public void disconnect(){
		try{
			ftp.disconnect();
		} catch (IOException e){
			log.error(e.getMessage(), e);
		}
	}

	public boolean fileExists(String fileName){
		return fileExists(fileName, true);
	}

	public boolean fileExists(String fileName, boolean logError){
		FTPFile[] ftpFiles = null;
		try{
			ftpFiles = ftp.listFiles(fileName);
		} catch (IOException e1){
			log.error(e1.getMessage(), e1);
		}
		if (ftpFiles == null || ftpFiles.length == 0 || ftpFiles[0] == null){
			log.log(logError ? Level.ERROR : Level.DEBUG, "File not found: " + fileName);
			return false;
		}
		return true;
	}

	public boolean getAndStoreFile(String localName, String remoteName){
		boolean retrieveOk = false;
		File file = new File(localName);
		FileOutputStream fos = null;
		try{
			File parent = file.getParentFile();
			if (!parent.exists() && !parent.mkdirs()){
				log.error("Cannot retrive file " + remoteName + " -> " + localName + "\nError create destination: "
				        + parent.getCanonicalPath());
				return false;
			}
			log.debug("Parent direcotry exists: " + parent.getCanonicalPath());
			file.createNewFile();
			log.debug("Retrive destination: " + file.getCanonicalPath());
			fos = new FileOutputStream(file);
			retrieveOk = ftp.retrieveFile(remoteName, fos);
			fos.close();
		} catch (IOException e){
			log.debug("could not get and store file");
			log.debug("   remote file name: " + remoteName);
			log.debug("   local file name : " + localName);
			log.error(e.getMessage(), e);
		} finally{
			if (fos != null)
				try{
					fos.close();
				} catch (IOException e){
					log.debug("Error closing FileOutputStream", e);
				}
		}
		if (!retrieveOk)
			file.delete();
		return retrieveOk;
	}

	private void cleanDirectory() throws IOException{
		String[] filesInWorkDir = ftp.listNames();
		for (String file : filesInWorkDir)
			ftp.deleteFile(file);
	}

	public boolean uploadUserDirectory(String dirName){
		log.debug("switching to directory `" + dirName + "`");
		boolean uploadOk = true;
		try{
			String oldWorkingDir = ftp.printWorkingDirectory();

			boolean dirNotExist = ftp.makeDirectory(dirName);
			uploadOk = ftp.changeWorkingDirectory(dirName);
			if (!dirNotExist)
				cleanDirectory();

			if (uploadOk)
				uploadOk = uploadFiles(dirName);

			ftp.changeWorkingDirectory(oldWorkingDir);
		} catch (IOException e){
			log.error(e.getMessage(), e);
			uploadOk = false;
		}
		return uploadOk;
	}

	public boolean uploadFile(String fileName, byte[] data){
		log.debug("uploading file `" + fileName + "` to ftp");
		boolean uploadOk;
		try{
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			int bytesWritten = 0;
			OutputStream os = ftp.storeFileStream(fileName);
			while (bytesWritten < data.length){
				int len = Math.min(1024, data.length - bytesWritten);
				os.write(data, bytesWritten, len);
				bytesWritten += len;
			}
			os.flush();
			os.close();
			uploadOk = ftp.completePendingCommand();
			ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
		} catch (IOException e){
			log.error(e.getMessage(), e);
			uploadOk = false;
		}
		return uploadOk;
	}

	private boolean uploadFiles(String dirName){
		log.debug("uploading files from user directory `" + dirName + "` to ftp");
		boolean uploadOk = true;
		try{
			File dir = new File(FTP_BASE_LOCAL_DIRECTORY + dirName);
			File[] files = dir.listFiles();
			for (File f : files){
				if (f.isDirectory())
					continue;
				log.debug("uploading file `" + f.getName() + "`...");
				FileInputStream is = null;
				try{
					is = new FileInputStream(f);
					ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
					boolean result = ftp.storeFile(f.getName(), is);
					ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
					if (!result){
						uploadOk = false;
						break;
					}
				} finally{
					if (is != null)
						is.close();
				}
			}
		} catch (IOException e){
			log.error(e.getMessage(), e);
			uploadOk = false;
		}

		return uploadOk;
	}

	public void uploadFile(String fileName, InputStream is) throws IOException{
		log.debug("uploading file `" + fileName + "` to ftp");
		try{
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			boolean result = ftp.storeFile(workingFolder + fileName, is);
			ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
			if (!result)
				throw new IOException("Failed to upload file: " + ftp.getReplyCode());
		} catch (IOException e){
			log.error(e.getMessage(), e);
			throw e;
		}
		log.debug("upload done");
	}

	public void downloadFile(OutputStream os, String fileName) throws IOException{
		log.debug("downloading file `" + fileName + "` from ftp");
		try{
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			boolean result = ftp.retrieveFile(workingFolder + fileName, os);
			ftp.setFileType(FTPClient.ASCII_FILE_TYPE);
			if (!result)
				throw new IOException("Failed to download tile: " + ftp.getReplyCode());
		} catch (IOException e){
			log.error(e.getMessage(), e);
			throw e;
		}
		log.debug("download done");
	}

	public List<String> list() throws IOException{
		FTPFile[] files = ftp.listFiles(workingFolder);
		List<String> result = new ArrayList<String>();
		for (FTPFile f : files){
			if (f.isDirectory())
				continue;
			result.add(f.getName());
		}
		return result;
	}

	public long getFileLength(String fileName) throws IOException{
		String path = workingFolder;
		if (!path.endsWith("/"))
			path += "/";
		FTPFile[] ftpFiles = ftp.listFiles(path + fileName);
		if (ftpFiles.length == 0)
			return -1;
		return ftpFiles[0].getSize();
	}
}
