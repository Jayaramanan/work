/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.Hashtable;

import com.ni3.ag.adminconsole.server.jobs.data.UserDataExtractor;
import com.ni3.ag.adminconsole.shared.service.def.UserDataExportService;

public class UserDataExportServiceImpl implements UserDataExportService{

	private UserDataExtractor userDataExtractor;

	public void setUserDataExtractor(UserDataExtractor userDataExtractor){
		this.userDataExtractor = userDataExtractor;
	}

	@Override
	public Hashtable<String, Integer> getExportPreview(String userIds, boolean withFirstDegree){
		return userDataExtractor.getClientTableExportPreview(userIds, withFirstDegree);
	}

}
