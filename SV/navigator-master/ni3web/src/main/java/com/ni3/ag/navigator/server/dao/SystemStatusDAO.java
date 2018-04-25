/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.dao;

import java.sql.Timestamp;

public interface SystemStatusDAO{

	Timestamp getServerTime();

}
