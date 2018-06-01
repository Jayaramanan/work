package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.SystemStatusDAO;

public class SystemStatusServlet extends HttpServlet{

	private static final Logger log = Logger.getLogger(SystemStatusServlet.class);

	private static final long serialVersionUID = 574288558587415231L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		boolean systemOk = false;

		final SystemStatusDAO systemStatusDAO = NSpringFactory.getInstance().getSystemStatusDAO();
		final long dbTimeStamp = systemStatusDAO.getServerTime().getTime();
		long appTimeStamp = new Date().getTime();
		if (Math.abs(dbTimeStamp - appTimeStamp) < 60000){
			systemOk = true;
		} else{
			log.warn("Time difference between DB and App is more than 60 seconds");
		}

		final PrintWriter writer = response.getWriter();
		if (systemOk){
			writer.print("OK");
		} else{
			writer.print("FAILED");
		}
	}

}
