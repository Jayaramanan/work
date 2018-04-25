/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.servlets.util.UserDataIntegrityValidator;

public class DataIntegrityValidationServlet extends HttpServlet{

	private static final long serialVersionUID = -5557228348388682839L;

	@Override
	public void init() throws ServletException{
		UserDataIntegrityValidator validator = NSpringFactory.getInstance().getUserDataIntegrityValidator();
		validator.checkUserData();
	}
}
