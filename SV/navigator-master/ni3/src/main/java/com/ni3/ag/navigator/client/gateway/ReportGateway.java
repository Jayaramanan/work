package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.ReportFormat;
import com.ni3.ag.navigator.shared.proto.NRequest.ReportData;
import com.ni3.ag.navigator.shared.proto.NResponse.Report;

public interface ReportGateway{

	byte[] getReport(Integer id, ReportFormat reportFormat, byte[] graphImage, byte[] mapImage, byte[] logoImg,
	        List<ReportData> data);

	List<Report> getReportTemplates();
}
