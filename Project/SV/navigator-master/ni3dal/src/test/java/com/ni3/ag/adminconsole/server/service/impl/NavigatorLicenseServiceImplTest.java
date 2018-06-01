/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.server.dao.mock.UserDAOMock;
import com.ni3.ag.adminconsole.server.service.mock.LicenseServiceMock;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.CustomChecksumEncoder;
//import com.ni3.ag.licensecreator.actions.CreateActionListener;
//import com.ni3.ag.licensecreator.model.LicenseCreatorModel;
//import com.ni3.ag.licensecreator.model.PropertyTableModel;

public class NavigatorLicenseServiceImplTest extends TestCase{
/*
	private NavigatorLicenseServiceImpl impl;
	private static ChecksumEncoder encoder = new CustomChecksumEncoder();
	private LicenseServiceImpl licenseServiceImpl;
//	private LicenseCreatorModel model;
//	private CreateActionListener licenseCreationListener;

	@Override
	protected void setUp() throws Exception{
		impl = new NavigatorLicenseServiceImpl();
		impl.setLicenseService(new LicenseServiceMock());
		impl.setUserDAO(new UserDAOMock());
//		model = new LicenseCreatorModel();
//		licenseCreationListener = new CreateActionListener();
		licenseServiceImpl = new LicenseServiceImpl();
	}

	public void testEnoughUsersValidLicenseValidMarkers() throws IllegalArgumentException, GeneralSecurityException,
	        IOException{
		Date now = new Date();
//		model.setStartDate(new Date(now.getTime() - 1));
//		model.setExpiryDate(new Date(now.getTime() + Integer.MAX_VALUE));
//		model.setAdminCount(1);

		LicenseData validLData = getLicenseData();

		List<LicenseData> expiredLicenses = new ArrayList<LicenseData>();
		List<LicenseData> expiringLicenses = new ArrayList<LicenseData>();
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();
		nonExpiringLicenses.add(validLData);

		List<User> users = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		List<UserEdition> editions = new ArrayList<UserEdition>();
		String checksum = encoder.encode(user.getId(), LicenseData.BASE_MODULE);
		UserEdition ue = new UserEdition(user, LicenseData.BASE_MODULE, checksum);
		ue.setUser(user);
		ue.setIsExpiring(false);
		ue.setToDelete(false);
		editions.add(ue);
		user.setUserEditions(editions);

		Map<NavigatorModule, Integer> highlightMap = impl.checkExpiringLicenseModules(users, expiredLicenses,
		        expiringLicenses, nonExpiringLicenses);

		assertEquals(1, user.getUserEditions().size());
		assertEquals(ue.isExpiring(), false);
	}

	public void testEnoughUsersExpiringLicenseInvalidMarkers() throws IllegalArgumentException, GeneralSecurityException,
	        IOException{
		Date now = new Date();
		model.setStartDate(new Date(now.getTime() - 1));
		model.setExpiryDate(new Date(now.getTime() + 100000));
		model.setAdminCount(1);

		LicenseData validLData = getLicenseData();

		List<LicenseData> expiredLicenses = new ArrayList<LicenseData>();
		List<LicenseData> expiringLicenses = new ArrayList<LicenseData>();
		expiringLicenses.add(validLData);
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();

		List<User> users = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		List<UserEdition> editions = new ArrayList<UserEdition>();
		String checksum = encoder.encode(user.getId(), LicenseData.BASE_MODULE);
		UserEdition ue = new UserEdition(user, LicenseData.BASE_MODULE, checksum);
		ue.setUser(user);
		ue.setIsExpiring(false);
		ue.setToDelete(false);
		editions.add(ue);
		user.setUserEditions(editions);
		users.add(user);

		Map<NavigatorModule, Integer> highlightMap = impl.checkExpiringLicenseModules(users, expiredLicenses,
		        expiringLicenses, nonExpiringLicenses);

		assertTrue(new Integer(1).equals(highlightMap.get(NavigatorModule.BaseModule)));
		assertEquals(1, user.getUserEditions().size());
		assertEquals(ue.isExpiring(), true);
	}

	public void testEnoughUsersExpiredLicenseInvalidMarkers() throws IllegalArgumentException, GeneralSecurityException,
	        IOException{
		Date now = new Date();
		model.setStartDate(new Date(now.getTime() - 100000));
		model.setExpiryDate(new Date(now.getTime() - 1));
		model.setAdminCount(1);

		LicenseData validLData = getLicenseData();

		List<LicenseData> expiredLicenses = new ArrayList<LicenseData>();
		expiredLicenses.add(validLData);
		List<LicenseData> expiringLicenses = new ArrayList<LicenseData>();
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();

		List<User> users = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		List<UserEdition> editions = new ArrayList<UserEdition>();
		String checksum = encoder.encode(user.getId(), LicenseData.BASE_MODULE);
		UserEdition ue = new UserEdition(user, LicenseData.BASE_MODULE, checksum);
		ue.setUser(user);
		ue.setIsExpiring(false);
		ue.setToDelete(false);
		editions.add(ue);
		user.setUserEditions(editions);
		users.add(user);

		Map<NavigatorModule, Integer> highlightMap = impl.checkExpiringLicenseModules(users, expiredLicenses,
		        expiringLicenses, nonExpiringLicenses);

		assertTrue(new Integer(0).equals(highlightMap.get(NavigatorModule.BaseModule)));
		assertEquals(0, user.getUserEditions().size());
	}

	public void testNotEnoughUsersExpiredAndExpiringLicense() throws IllegalArgumentException, GeneralSecurityException,
	        IOException{
		Date now = new Date();
		model.setStartDate(new Date(now.getTime() - 1));
		model.setExpiryDate(new Date(now.getTime() + 100000));
		model.setAdminCount(1);
		LicenseData validLData1 = getLicenseData();
		model.setStartDate(new Date(now.getTime() - 100000));
		model.setExpiryDate(new Date(now.getTime() - 1));
		model.setAdminCount(1);
		LicenseData validLData2 = getLicenseData();

		List<LicenseData> expiredLicenses = new ArrayList<LicenseData>();
		List<LicenseData> expiringLicenses = new ArrayList<LicenseData>();
		expiringLicenses.add(validLData1);
		expiringLicenses.add(validLData2);
		List<LicenseData> nonExpiringLicenses = new ArrayList<LicenseData>();

		List<User> users = new ArrayList<User>();
		User user = new User();
		user.setId(1);
		List<UserEdition> editions = new ArrayList<UserEdition>();
		String checksum = encoder.encode(user.getId(), LicenseData.BASE_MODULE);
		UserEdition ue = new UserEdition(user, LicenseData.BASE_MODULE, checksum);
		ue.setUser(user);
		ue.setIsExpiring(false);
		ue.setToDelete(false);
		editions.add(ue);
		user.setUserEditions(editions);
		users.add(user);

		Map<NavigatorModule, Integer> highlightMap = impl.checkExpiringLicenseModules(users, expiredLicenses,
		        expiringLicenses, nonExpiringLicenses);

		assertTrue(new Integer(1).equals(highlightMap.get(NavigatorModule.BaseModule)));
		assertEquals(1, user.getUserEditions().size());
		assertEquals(ue.isExpiring(), true);
	}

	private LicenseData getLicenseData() throws IllegalArgumentException, GeneralSecurityException, IOException{
		PropertyTableModel tModel = new PropertyTableModel(LicenseData.NAVIGATOR_PRODUCT);
		model.setPropertyTableModel(tModel);
		licenseCreationListener.setModel(model);

		String licenseText = licenseCreationListener.makeLicensebySelectedProduct(LicenseData.NAVIGATOR_PRODUCT);
		License acLicense = new License();
		acLicense.setLicense(licenseText);
		acLicense.setProduct(LicenseData.NAVIGATOR_PRODUCT);

		return licenseServiceImpl.loadLicense(acLicense);
	}
*/
	public void testDummy(){
		assert(true);
	}
}
