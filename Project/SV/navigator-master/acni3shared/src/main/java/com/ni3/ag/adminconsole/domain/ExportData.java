/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;

public class ExportData implements Serializable{
	private static final long serialVersionUID = -1763694606833408373L;
	private byte[] fileData;
	private String fileName;

	public ExportData(String excelName, byte[] decodeBuffer){
		fileName = excelName;
		fileData = decodeBuffer;
	}

	public byte[] getFileData(){
		return fileData;
	}

	public void setFileData(byte[] fileData){
		this.fileData = fileData;
	}

	public String getFileName(){
		return fileName;
	}

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
}
