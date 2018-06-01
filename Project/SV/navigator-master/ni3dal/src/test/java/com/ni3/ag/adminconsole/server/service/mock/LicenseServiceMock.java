/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.validation.ACException;

public class LicenseServiceMock implements LicenseService{

	@Override
	public License addLicense(License license){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteLicense(License currentLicense){
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteLicenses(List<LicenseData> expiredLicenses){
		// TODO Auto-generated method stub

	}

	@Override
	public List<LicenseData> getExpiredLicenseData(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LicenseData> getExpiringLicenseData(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<License> getLicenseByProductName(String name){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LicenseData> getLicenseData() throws ACException{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LicenseData> getLicenseDataByProductName(String name){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<License> getLicenses(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ErrorEntry> updateLicense(License license){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLicenseExpiring(LicenseData lData){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canDeleteLicense(LicenseData lDataToRemove){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaxNonExpiringUserCount(List<LicenseData> licenseDataList, String module){
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxUserCount(List<LicenseData> licenseDataList, String module){
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUsedUserCount(List<User> users, String moduleValue){
		// TODO Auto-generated method stub
		return 0;
	}

}
