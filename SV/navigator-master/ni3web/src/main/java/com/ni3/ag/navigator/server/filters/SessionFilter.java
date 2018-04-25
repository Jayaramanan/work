/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordEncoder;

public class SessionFilter extends AbstractSessionFilter{

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
	        throws IOException, ServletException{
		super.doFilter(request, response, chain);
	}

	@Override
	protected void sendReloginResponse(ServletResponse response) throws IOException{
		response.getWriter().print(TextID.MsgPleaseRelogin);
		response.getWriter().close();
	}

    @Override
    protected void sendInvalidateSchemaResponse(ServletResponse response) throws IOException {
		response.getWriter().print("MsgReloadSchema");
		response.getWriter().close();
    }

    protected boolean isFreeMethod(final ServletRequest request){
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final StringBuffer requestedURL = httpRequest.getRequestURL();

		if (requestedURL == null){
			return false;
		}
		if (requestedURL.toString().endsWith("SettingsProvider")){
			final String parameter = httpRequest.getParameter("propertyName");
			if (parameter != null && parameter.endsWith(PasswordEncoder.PASSWORD_ENCODER_PROPERTY)){
				return true;
			}
		}
		return false;
	}
}
