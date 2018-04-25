/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.LicenseData.LicenseStatus;
import com.ni3.ag.adminconsole.server.dao.mock.LicenseDAOMock;
import com.ni3.ag.adminconsole.shared.language.TextID;

import junit.framework.TestCase;

public class LicenseServiceImplTest extends TestCase{
	private LicenseServiceImpl service;

	public void setUp(){
		service = new LicenseServiceImpl();
	}

	public void testGetExpiringLicenses(){
		service.setLicenseDAO(new LicenseDAOMock());
		List<LicenseData> licenses = service.getExpiredLicenseData();
		assertTrue(licenses.isEmpty());
		licenses = service.getExpiringLicenseData();
		assertTrue(licenses.isEmpty());
	}

	public void testGetLicenses(){
		service.setLicenseDAO(new LicenseDAOMock());
		List<LicenseData> licenseList = service.getLicenseDataByProductName(LicenseData.ACNi3WEB_PRODUCT);
		assertFalse(licenseList.isEmpty());
		LicenseData ldata = licenseList.get(0);
		assertEquals(ldata.getStatus(), LicenseStatus.Active);
		assertEquals(ldata.get(LicenseData.ACLANGUAGE_MODULE), 1);
	}

	public void testUpdateInvalidLicense(){
		service.setLicenseDAO(new LicenseDAOMock());
		License lic = new License();
		lic.setId(0);
		lic.setProduct(LicenseData.ACNi3WEB_PRODUCT);
		lic.setLicense("invalid license text");
		List<ErrorEntry> errors = service.updateLicense(lic);
		assertFalse(errors.isEmpty());
		ErrorEntry err = errors.get(0);
		assertEquals(TextID.MsgInvalidLicense, err.getId());
	}
}
