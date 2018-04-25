package com.ni3.ag.navigator.server.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.NSpringFactory;

public class SpringInitializerServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SpringInitializerServlet.class);

	@Override
	public void init() throws ServletException{
		super.init();
		log.debug("NSpringFactory init");
		NSpringFactory.init();
	}

	@Override
	public void destroy(){
		super.destroy();
		log.debug("NSpringFactory destroy");
		NSpringFactory.destroy();
	}
}
