/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.Hashtable;

public interface UserDataExportService{

	public Hashtable<String, Integer> getExportPreview(String userIds, boolean withFirstDegree);
}
