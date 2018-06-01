/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

public class SyncModule{
	private String moduleName;
	private int totalRecords = 0;
	private int okCount = 0;
	private int warnCount = 0;
	private int errorCount = 0;
	private int status;

	public SyncModule(String moduleName, int totalRecords, int status){
		super();
		this.moduleName = moduleName;
		this.totalRecords = totalRecords;
		this.status = status;
	}

	public void setProcessedRecords(int okCount, int warnCount, int errorCount){
		this.okCount = okCount;
		this.warnCount = warnCount;
		this.errorCount = errorCount;
	}

	public int getStatus(){
		return status;
	}

	public void setStatus(int status){
		this.status = status;
	}

	@Override
	public String toString(){
		if (totalRecords >= 0){
			String ok = "<font color=\"#00ff00\">" + okCount + "</font>";
			String warn = "<font color=\"#aaaa00\">" + warnCount + "</font>";
			String error = "<font color=\"#ff0000\">" + errorCount + "</font>";
			return "<html><body><font size=\"3\">" + moduleName + "   (" + ok + "&#47;" + warn + "&#47;" + error + "&#47;"
			        + totalRecords + ")" + "<font></body></html>";
		}
		return "<html><body><font size=\"5\" face=\"arial\">" + moduleName + "</font></body></html>";
	}
}
