/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;

@SuppressWarnings("serial")
public class DBVersionValidationServlet extends Ni3Servlet{
	private static Logger log = Logger.getLogger(DBVersionValidationServlet.class);

	@Override
	public void init() throws ServletException{
		log.debug("Validation of database versions");
		// TODO: Enable validation
		/*
		 * DatabaseVersionValidator validator = new DatabaseVersionValidator(); if
		 * (!validator.validateDatabaseVersions()) {
		 * log.error("Application is not deployed, because of different database versions in war and sys_iam table");
		 * throw new Error("Stop deployment"); }
		 */
	}

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		// not used
	}

	@Override
	protected UserActivityType getActivityType(){
		// not used
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}
}
