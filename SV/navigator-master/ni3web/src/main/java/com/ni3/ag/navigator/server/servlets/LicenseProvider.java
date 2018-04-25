/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.license.LicenseValidator;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.License;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class LicenseProvider extends Ni3Servlet{
	private static final long serialVersionUID = -1740813387042124684L;
	private static final Logger log = Logger.getLogger(LicenseProvider.class);

	@Override
	protected void doInternalPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
	        IOException{
		InputStream io = httpRequest.getInputStream();
		NRequest.License request = NRequest.License.parseFrom(io);
		NResponse.Envelope.Builder builder = NResponse.Envelope.newBuilder();
		switch (request.getAction()){
			case GET_LICENSE:
				NResponse.License protoLicense = getLicense();
				builder.setPayload(protoLicense.toByteString());
				break;
		}

		NResponse.Envelope env = builder.setStatus(NResponse.Envelope.Status.SUCCESS).build();
		env.writeTo(httpResponse.getOutputStream());
	}

	private NResponse.License getLicense(){
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User user = localStorage.getCurrentUser();
		if (log.isDebugEnabled()){
			log.debug("Getting license for user " + user.getId());
		}
		LicenseValidator validator = NSpringFactory.getInstance().getLicenseValidator();
		License license = validator.getLicense(user.getId());
		if (log.isDebugEnabled()){
			log.debug("Got license, valid =  " + license.isValid() + ", base module = " + license.hasBaseModule());
		}

		NResponse.License.Builder protoLicense = NResponse.License.newBuilder();
		protoLicense.setValid(license.isValid());
		protoLicense.setBaseModule(license.hasBaseModule());
		protoLicense.setDataCaptureModule(license.hasDataCaptureModule());
		protoLicense.setChartsModule(license.hasChartsModule());
		protoLicense.setMapsModule(license.hasMapsModule());
		protoLicense.setGeoAnalyticsModule(license.hasGeoAnalyticsModule());
		protoLicense.setRemoteClientModule(license.hasRemoteClientModule());
		protoLicense.setReportsModule(license.hasReportsModule());
		return protoLicense.build();
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
