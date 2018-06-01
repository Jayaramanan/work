/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.reports;

import java.util.List;

import com.ni3.ag.navigator.server.domain.ReportTemplate;
import com.ni3.ag.navigator.shared.proto.NRequest.Report;

public interface ReportManager{

	byte[] getReport(Report report);

	List<ReportTemplate> getReportTemplates();
}
