/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.jws;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;

public class ClientDownloadServlet extends HttpServlet{
	private static final long serialVersionUID = -2729517376798646613L;

	@Override
	protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response)
	        throws IOException{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>");
		sb.append("<p>");
		sb.append("<a href=\"webstart/launch.jnlp\">Ni3 Admin Console</a>");
		sb.append("</p>");
		sb.append("</HTML>");
		out.print(sb.toString());
	}
}
