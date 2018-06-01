/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.gateway.LicenseGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpLicenseGatewayImpl;
import com.ni3.ag.navigator.shared.domain.License;

public class LicenseValidator{
	private static final Logger log = Logger.getLogger(LicenseValidator.class);
	private License license;
	private static LicenseValidator instance;

	private LicenseValidator(){
		license = new License();
	}

	public static LicenseValidator getInstance(){
		if (instance == null)
			instance = new LicenseValidator();
		return instance;
	}

	public void reloadLicense(){
		if (log.isDebugEnabled())
			log.debug("Loading license");
		LicenseGateway licenseGateway = new HttpLicenseGatewayImpl();
		license = licenseGateway.getLicense();
	}

	public String isCorrectLicense(){
		return license.isValid() ? null : "License not found";
	}

	public boolean isNodeDataChangeEnabled(){
		return license.hasDataCaptureModule();
	}

	public boolean isEdgeDataChangeEnabled(){
		return license.hasDataCaptureModule();
	}

	public boolean isMapsEnabled(){
		return license.hasMapsModule();
	}

	public boolean isChartsEnabled(){
		return license.hasChartsModule();
	}

	public boolean isGeoAnalyticsEnabled(){
		return license.hasMapsModule() && license.hasGeoAnalyticsModule();
	}

	public boolean isOfflineClientEnabled(){
		return license.hasRemoteClientModule();
	}

	public boolean isReportsEnabled(){
		return license.hasReportsModule();
	}

	public boolean isApplicationEnabled(){
		return license.hasBaseModule();
	}

}
