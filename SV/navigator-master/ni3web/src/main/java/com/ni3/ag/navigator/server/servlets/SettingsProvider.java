/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordEncoder;

public class SettingsProvider extends Ni3Servlet{

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(SettingsProvider.class);

	private static NSpringFactory daoFactory = NSpringFactory.getInstance();

	// this field is used to access the request data in getTransactionDeltaForRequest method
	private HttpServletRequest request;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		String action = getParameter(request, RequestParam.Action);
		if ("createOrUpdate".equals(action)){
			this.request = request;
			processUpdate(request, response, true);
		} else if ("update".equals(action)){
			this.request = request;
			processUpdate(request, response, false);
		} else{
			processGetProperty(request, response);
		}
	}

	@Override
	protected DeltaHeader getTransactionDeltaForRequest(){
		DeltaHeader result = DeltaHeader.DO_NOTHING;

		if (request != null){
			ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
			User creator = storage.getCurrentUser();
			Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

			final String propertyName = getParameter(request, RequestParam.P3);
			params.put(DeltaParamIdentifier.UpdateSettingsPropertyName, new DeltaParam(
			        DeltaParamIdentifier.UpdateSettingsPropertyName, propertyName));

			result = new DeltaHeader(DeltaType.SETTING_UPDATE, creator, params);
		}

		return result;
	}

	private void processUpdate(HttpServletRequest request, HttpServletResponse response, boolean create) throws IOException{
		final String value = getParameter(request, RequestParam.P1);
		final int userId = getIntParam(request, RequestParam.P2);
		final String property = getParameter(request, RequestParam.P3);
		final String section = getParameter(request, RequestParam.P4);

		if (log.isDebugEnabled()){
			log.debug("Updating settings for user " + userId + ", section = " + section + ", " + property + "=" + value);
		}

		final UserSettingsDAO dao = daoFactory.getUserSettingsDao();
		UserSetting currentSetting = dao.get(userId, property);
		if (currentSetting == null && create){
			currentSetting = new UserSetting();
			currentSetting.setId(userId);
			currentSetting.setProperty(property);
			currentSetting.setSection(section);
		}

		if (currentSetting != null){
			currentSetting.setValue(value);
			dao.save(currentSetting);
			response.getWriter().write("1");
		}
	}

	private void processGetProperty(HttpServletRequest request, HttpServletResponse response) throws IOException{
		this.request = null;
		String propertyName = getParameter(request, RequestParam.propertyName);
		if (propertyName == null || propertyName.isEmpty()){
			log.warn("Property name not passed to servlet");
			return;
		}
		if (log.isDebugEnabled()){
			log.debug("Requested property name: " + propertyName);
		}
		Properties properties = new Properties();
		String resourceName = "/Ni3Web.properties";
		properties.load(getClass().getResourceAsStream(resourceName));
		String value = properties.getProperty(propertyName, getDefaultValueForName(propertyName));
		response.getWriter().write(value);
	}

	private String getDefaultValueForName(String propertyName){
		if (PasswordEncoder.PASSWORD_ENCODER_PROPERTY.equals(propertyName)){
			return PasswordEncoder.DEFAULT_PASSWORD_ENCODER;
		}
		return "";
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
