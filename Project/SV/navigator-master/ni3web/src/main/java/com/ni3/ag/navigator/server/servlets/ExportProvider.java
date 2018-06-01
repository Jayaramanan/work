/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.navigator.server.export.UserDataExporter;
import com.ni3.ag.navigator.shared.constants.RequestParam;

public class ExportProvider extends Ni3Servlet{
	private static final long serialVersionUID = -1740813387042124684L;

	@Override
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException{
		doPost(request, response);
	}

	@Override
	public void doInternalPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException{
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");

		if (getParameter(request, RequestParam.XLSExport) != null){
			final String nodes = listParam(request, RequestParam.Nodes);
			final String edges = listParam(request, RequestParam.Edges);
			final String dateFormat = getParameter(request, RequestParam.DateFormat);

			final UserDataExporter exporter = new UserDataExporter();
			try{
				exporter.performAction(nodes, edges, dateFormat, response.getOutputStream());
			} catch (final ACException e){
				response.reset();
			}
			response.flushBuffer();
		}

	}

	@Override
	protected UserActivityType getActivityType(){
		return UserActivityType.ExportData;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}

}
