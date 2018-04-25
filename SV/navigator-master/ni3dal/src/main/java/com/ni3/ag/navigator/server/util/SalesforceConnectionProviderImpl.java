/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.util;

import com.ni3.ag.navigator.server.services.SalesforceConnectionProvider;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.apache.log4j.Logger;

@Deprecated
public class SalesforceConnectionProviderImpl implements SalesforceConnectionProvider{
	private static final Logger log = Logger.getLogger(SalesforceConnectionProviderImpl.class);
	private String userName;
	private String password;
	private String endpointUrl;

	private PartnerConnection connection;

	// TODO spring managed connection provider
	private static SalesforceConnectionProvider instance;

	public static final SalesforceConnectionProvider getInstance(){
		instance = new SalesforceConnectionProviderImpl();
		return instance;
	}

	public void setUserName(String userName){
		this.userName = userName;
	}

	public void setPassword(String password){
		this.password = password;
	}

	public void setEndpointUrl(String endpointUrl){
		this.endpointUrl = endpointUrl;
	}

	public SalesforceConnectionProviderImpl(){
		userName = "jury@ni3.net";
		password = "5i2MD8wp" + "K8RpoPo8u2ZKmtrUrPX5bqiY";
		endpointUrl = "https://login.salesforce.com/services/Soap/u/22.0/";
	}

	@Override
	public PartnerConnection getConnection(){
		if (connection == null){
			connection = createConnection();
		}
		return connection;
	}

	@Override
	public void recreateConnection(){
		connection = createConnection();
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
