/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;

public class ReportsModel extends AbstractModel{

	private Map<DatabaseInstance, List<ReportTemplate>> reportMap = new HashMap<DatabaseInstance, List<ReportTemplate>>();
	private ReportTemplate currentReport;
	private String newReportTemplateName;

	public List<ReportTemplate> getReports(){
		return reportMap.get(currentDatabaseInstance);
	}

	public Map<DatabaseInstance, List<ReportTemplate>> getReportMap(){
		return reportMap;
	}

	public void setReports(List<ReportTemplate> schemas){
		reportMap.put(currentDatabaseInstance, schemas);
	}

	public ReportTemplate getCurrentReport(){
		return currentReport;
	}

	public void setCurrentReport(ReportTemplate currentReport){
		this.currentReport = currentReport;
	}

	public void setNewReportTemplateName(String message){
		this.newReportTemplateName = message;
	}

	public String getNewReportTemplateName(){
		return newReportTemplateName;
	}

	public boolean isInstanceLoaded(){
		return reportMap.containsKey(currentDatabaseInstance);
	}

}
