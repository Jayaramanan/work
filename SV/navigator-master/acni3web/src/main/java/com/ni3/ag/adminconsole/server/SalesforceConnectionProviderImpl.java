/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server;

import org.apache.log4j.Logger;

import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceConnectionProviderImpl implements SalesforceConnectionProvider{
	private static final Logger log = Logger.getLogger(SalesforceConnectionProviderImpl.class);

	@Override
	public PartnerConnection getConnection(String url, String username, String password) throws ConnectionException{
		PartnerConnection connection = createConnection(url, username, password);
		return connection;
	}

	private PartnerConnection createConnection(String url, String username, String password) throws ConnectionException{
		ConnectorConfig config = new ConnectorConfig();
		config.setUsername(username);
		config.setPassword(password);
		config.setAuthEndpoint(url);
		config.setServiceEndpoint(url);
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
			throw ce;
		}
		return connection;
	}
}
