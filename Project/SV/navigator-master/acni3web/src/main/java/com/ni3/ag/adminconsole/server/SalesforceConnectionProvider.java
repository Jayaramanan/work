/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.adminconsole.server;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

public interface SalesforceConnectionProvider{

    PartnerConnection getConnection(String url, String username, String password) throws ConnectionException;

}
