/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao.impl.salesforce;

import com.ni3.ag.navigator.server.services.SalesforceConnectionProvider;
import com.ni3.ag.navigator.server.util.SalesforceConnectionProviderImpl;
import com.sforce.soap.partner.PartnerConnection;

public class SalesforceBaseDAO{

	private SalesforceConnectionProvider provider;

	public void setProvider(SalesforceConnectionProvider provider){
		this.provider = provider;
	}

	public PartnerConnection getConnection(){
		return SalesforceConnectionProviderImpl.getInstance().getConnection();
	}
}
