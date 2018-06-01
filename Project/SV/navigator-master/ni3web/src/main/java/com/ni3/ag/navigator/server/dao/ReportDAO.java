/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.ReportTemplate;

public interface ReportDAO{

	ReportTemplate getReportTemplate(int reportId);

	List<ReportTemplate> getReportTemplates();

}
