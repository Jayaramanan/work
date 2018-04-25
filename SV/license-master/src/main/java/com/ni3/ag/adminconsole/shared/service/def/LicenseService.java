/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.validation.ACException;

public interface LicenseService{
	public List<License> getLicenses();

	public List<License> getLicenseByProductName(String name);

	public List<ErrorEntry> updateLicense(License license);

	public List<LicenseData> getLicenseDataByProductName(String name);

	public List<LicenseData> getLicenseData() throws ACException;

	public void deleteLicense(License currentLicense);

	public List<LicenseData> getExpiringLicenseData();

	public License addLicense(License license);

	public List<LicenseData> getExpiredLicenseData();

	public void deleteLicenses(List<LicenseData> expiredLicenses);

	boolean isLicenseExpiring(LicenseData lData);

	int getMaxNonExpiringUserCount(List<LicenseData> licenseDataList, String module);

	boolean canDeleteLicense(LicenseData lDataToRemove);

	int getMaxUserCount(List<LicenseData> licenseDataList, String module);

	int getUsedUserCount(List<User> users, String moduleValue);

}
