/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl.salesforce;

import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.server.services.SalesforceConnectionProvider;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.apache.log4j.Logger;

public class SalesforceConnectionProviderImpl implements SalesforceConnectionProvider{
	private static final Logger log = Logger.getLogger(SalesforceConnectionProviderImpl.class);
	private String userName;
	private String password;
	private String endpointUrl;

	private ThreadLocalStorage threadLocalStorage;
	private UserSettingsDAO userSettingsDAO;

	private PartnerConnection connection;

	public void setThreadLocalStorage(ThreadLocalStorage threadLocalStorage){
		this.threadLocalStorage = threadLocalStorage;
	}

	public void setUserSettingsDAO(UserSettingsDAO userSettingsDAO){
		this.userSettingsDAO = userSettingsDAO;
	}

	@Override
	public PartnerConnection getConnection(){
		if (connection == null){
			int userId = threadLocalStorage.getCurrentUser().getId();
			UserSetting url = userSettingsDAO.getSettingForUser(userId, "Applet", "Salesforce_API_URL");
			UserSetting username = userSettingsDAO.getSettingForUser(userId, "Applet", "Salesforce_API_Username");
			UserSetting password = userSettingsDAO.getSettingForUser(userId, "Applet", "Salesforce_API_Password");
			if (url != null && username != null && password != null){
				this.endpointUrl = url.getValue();
				this.userName = username.getValue();
				this.password = password.getValue();
			}
			connection = createConnection();
		}
		return connection;
	}

	@Override
	public void recreateConnection(){
		log.debug("Recreating sales force connection");
		connection = createConnection();
		log.debug("Recreation completed");
	}

	private PartnerConnection createConnection(){
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(userName);
		config.setPassword(password);
		config.setAuthEndpoint(endpointUrl);
		config.setServiceEndpoint(endpointUrl);
		PartnerConnection connection = null;
		try{
			connection = new PartnerConnection(config);
			GetUserInfoResult userInfo = connection.getUserInfo();
			log.info("\nLogging in ...\n");
			log.info("UserID: " + userInfo.getUserId());
			log.info("User Full Name: " + userInfo.getUserFullName());
			log.info("User Email: " + userInfo.getUserEmail());
			log.info("SessionID: " + config.getSessionId());
			log.info("Auth End Point: " + config.getAuthEndpoint());
			log.info("Service End Point: " + config.getServiceEndpoint());
		} catch (ConnectionException ce){
			log.error(ce);
		}
		return connection;
	}
}
