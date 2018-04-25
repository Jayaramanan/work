/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.ByteString;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.domain.ReportTemplate;
import com.ni3.ag.navigator.server.domain.ReportType;
import com.ni3.ag.navigator.server.reports.ReportManager;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.Report;
import com.ni3.ag.navigator.shared.proto.NResponse.Report.Builder;

public class ReportProvider extends Ni3Servlet{

	private static final long serialVersionUID = -1740813387042124684L;

	private static NSpringFactory springFactory = NSpringFactory.getInstance();

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");

		ReportManager manager = springFactory.getReportManager();
		final NRequest.Reports reports = NRequest.Reports.parseFrom(request.getInputStream());
		NResponse.Reports.Builder result = NResponse.Reports.newBuilder();
		switch (reports.getAction()){
			case GET_ALL:
				List<ReportTemplate> templates = manager.getReportTemplates();
				for (ReportTemplate template : templates){
					Builder report = Report.newBuilder();
					report.setId(template.getId());
					report.setName(template.getName());
					report.setType(template.getType() == ReportType.DYNAMIC_REPORT ? Report.ReportType.DYNAMIC
					        : Report.ReportType.STATIC);
					if (template.getPreviewIcon() != null && template.getPreviewIcon().length > 0){
						report.setPreview(ByteString.copyFrom(template.getPreviewIcon()));
					}
					result.addReports(report);
				}
				break;
			case GET_PRINT:
				byte[] bytes = manager.getReport(reports.getReport());
				if (bytes != null){
					result.setReportPrint(ByteString.copyFrom(bytes));
				}
				break;
		}

		final ByteString payload = result.build().toByteString();
		final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
		envelope.setStatus(NResponse.Envelope.Status.SUCCESS);
		envelope.setPayload(payload);
		envelope.build().writeTo(response.getOutputStream());
	}

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		// TODO Auto-generated method stub
	}

	@Override
	protected UserActivityType getActivityType(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// TODO Auto-generated method stub
		return null;
	}

}
