/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

public interface SyncQuartzJobStarterService{

	public static final int THICK_CLIENT_EXTRACT_JOB_ID = 0;
	public static final int MAP_EXTRACTION_JOB_ID = 2;
	public static final int DELETE_USER_EDITIONS_JOB_ID = 4;

	public void startJob(int jobId);

}
