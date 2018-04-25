/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.ni3.ag.navigator.shared.proto.NResponse;

public class ProtoSessionFilter extends AbstractSessionFilter{

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
	        throws IOException, ServletException{
		super.doFilter(request, response, chain);
	}

	@Override
	protected void sendReloginResponse(ServletResponse response) throws IOException{
		final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
		envelope.setStatus(NResponse.Envelope.Status.SESSION_EXPIRED);
		envelope.build().writeTo(response.getOutputStream());
	}

    @Override
    protected void sendInvalidateSchemaResponse(ServletResponse response) throws IOException {
        final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
        envelope.setStatus(NResponse.Envelope.Status.INVALID_SCHEMA);
        envelope.build().writeTo(response.getOutputStream());
    }

    @Override
	protected boolean isFreeMethod(ServletRequest request){
		return false;
	}
}
