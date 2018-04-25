/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public interface DatabaseSettingsService{

	public final static String EXPORT_SIZE = "com.ni3.ag.adminconsole.export.size";
	public final static String EXPIRY_PERIOD = "com.ni3.ag.adminconsole.licence.expiryPeriod";
	public final static String HIBERNATE_DIALECT = "hibernate.dialect";
	public final static String OFFLINE_TMP_DATASOURCE = "com.ni3.ag.adminconsole.offline.tmpDataSource";
	public final static String OFFLINE_TMP_DBNAME = "com.ni3.ag.adminconsole.offline.tmpDataBase.name";
	public final static String OFFLINE_TMP_DBHOST = "com.ni3.ag.adminconsole.offline.tmpDataBase.host";
	public final static String OFFLINE_TMP_DBPORT = "com.ni3.ag.adminconsole.offline.tmpDataBase.port";
	public final static String OFFLINE_TMP_DBUSER = "com.ni3.ag.adminconsole.offline.tmpDataBase.user";
	public final static String OFFLINE_TMP_DBPASS = "com.ni3.ag.adminconsole.offline.tmpDataBase.pwd";
	public final static String OFFLINE_PGDUMP = "com.ni3.ag.adminconsole.offline.postgres.pgdump";

	public String getDatabaseName();

	public List<DatabaseInstance> getDatabaseInstanceNames();

	public Map<String, String> getCommonProperties();
}
