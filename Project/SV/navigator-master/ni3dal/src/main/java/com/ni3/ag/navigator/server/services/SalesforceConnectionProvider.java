/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import com.sforce.soap.partner.PartnerConnection;

public interface SalesforceConnectionProvider{

    PartnerConnection getConnection();

	void recreateConnection();
}
