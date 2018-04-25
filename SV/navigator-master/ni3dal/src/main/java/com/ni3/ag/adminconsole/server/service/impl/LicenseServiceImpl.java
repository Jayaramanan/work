/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.License;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;
import com.ni3.ag.adminconsole.license.KeyStore;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.license.LicenseData.LicenseStatus;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.LicenseDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.util.TimeUtil;
import com.ni3.ag.adminconsole.validation.ACException;
import com.smardec.license4j.LicenseManager;
import com.smardec.license4j.LicenseNotFoundException;

public class LicenseServiceImpl implements LicenseService{

	private final static Logger log = Logger.getLogger(LicenseServiceImpl.class);

	private final static String EXPIRY_PERIOD_PROPERTY = "com.ni3.ag.adminconsole.licence.expiryPeriod";
	/** expiry period in days */
	private final static String DEFAULT_EXPIRY_PERIOD = "30";

	private LicenseDAO licenseDAO;
	private Properties databaseProperties;
	private GroupDAO groupDAO;
	private UserDAO userDAO;

	public void setDatabaseProperties(Properties databaseProperties){
		this.databaseProperties = databaseProperties;
	}

	public void setLicenseDAO(LicenseDAO licenseDAO){
		this.licenseDAO = licenseDAO;
	}

	@Override
	public List<License> getLicenses(){
		return licenseDAO.getLicenses();
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public List<LicenseData> getLicenseData() throws ACException{
		List<License> licenses = getLicenses();
		List<LicenseData> licenseData = new ArrayList<LicenseData>();
		for (License l : licenses){
			LicenseData ld = loadLicense(l);
			if (ld != null)
				licenseData.add(ld);
		}
		return licenseData;
	}

	@Override
	public List<LicenseData> getLicenseDataByProductName(String name){
		List<License> licenseList = getLicenseByProductName(name);
		List<LicenseData> lDataList = new ArrayList<LicenseData>();
		if (licenseList == null)
			return lDataList;
		for (License l : licenseList)
			lDataList.add(loadLicense(l));
		return lDataList;
	}

	public LicenseData loadLicense(License l){
		LicenseData lData = new LicenseData();
		String licenseText = l.getLicense();
		if (licenseText == null){
			lData.setLicense(l);
			return lData;
		}

		LicenseManager.setPublicKey(KeyStore.publicKey);

		try{
			ByteArrayInputStream bis = new ByteArrayInputStream(licenseText.getBytes());
			com.smardec.license4j.License smartLicense = LicenseManager.loadLicense(bis);

			StringBuffer startDate = LicenseData.getDateStr(smartLicense.getFeature(LicenseData.START_DATE_PROPERTY));
			StringBuffer expiryDate = LicenseData.getDateStr(smartLicense.getFeature(LicenseData.EXPIRY_DATE_PROPERTY));

			lData.put(LicenseData.START_DATE_PROPERTY, startDate);
			lData.put(LicenseData.EXPIRY_DATE_PROPERTY, expiryDate);

			lData.put(LicenseData.PRODUCT_NAME_PROPERTY, smartLicense.getFeature(LicenseData.PRODUCT_NAME_PROPERTY));
			lData.put(LicenseData.USER_COUNT_PROPERTY, smartLicense.getFeature(LicenseData.USER_COUNT_PROPERTY));
			lData.put(LicenseData.BASE_MODULE, smartLicense.getFeature(LicenseData.BASE_MODULE));
			lData.put(LicenseData.DATA_CAPTURE_MODULE, smartLicense.getFeature(LicenseData.DATA_CAPTURE_MODULE));
			lData.put(LicenseData.CHARTS_MODULE, smartLicense.getFeature(LicenseData.CHARTS_MODULE));
			lData.put(LicenseData.MAPS_MODULE, smartLicense.getFeature(LicenseData.MAPS_MODULE));
			lData.put(LicenseData.GEO_ANALYTICS_MODULE, smartLicense.getFeature(LicenseData.GEO_ANALYTICS_MODULE));
			lData.put(LicenseData.REMOTE_CLIENT_MODULE, smartLicense.getFeature(LicenseData.REMOTE_CLIENT_MODULE));
			lData.put(LicenseData.REPORTS_MODULE, smartLicense.getFeature(LicenseData.REPORTS_MODULE));

			lData.put(LicenseData.ACUSERS_MODULE, smartLicense.getFeature(LicenseData.ACUSERS_MODULE));
			lData.put(LicenseData.ACSCHEMA_MODULE, smartLicense.getFeature(LicenseData.ACSCHEMA_MODULE));
			lData.put(LicenseData.ACMETAPHOR_MODULE, smartLicense.getFeature(LicenseData.ACMETAPHOR_MODULE));
			lData.put(LicenseData.ACLANGUAGE_MODULE, smartLicense.getFeature(LicenseData.ACLANGUAGE_MODULE));
			lData.put(LicenseData.ACCHART_MODULE, smartLicense.getFeature(LicenseData.ACCHART_MODULE));
			lData.put(LicenseData.ACGEO_MODULE, smartLicense.getFeature(LicenseData.ACGEO_MODULE));
			lData.put(LicenseData.ACDIAGNOSTICS_MODULE, smartLicense.getFeature(LicenseData.ACDIAGNOSTICS_MODULE));
			lData.put(LicenseData.ACOFFLINE_MODULE, smartLicense.getFeature(LicenseData.ACOFFLINE_MODULE));
			lData.put(LicenseData.ACREPORTS_MODULE, smartLicense.getFeature(LicenseData.ACREPORTS_MODULE));
			lData.put(LicenseData.ACETL_MODULE, smartLicense.getFeature(LicenseData.ACETL_MODULE));
			lData.setLicense(l);

			lData.setValid(LicenseManager.isValid(smartLicense));
			lData.setStatus(getLicenseStatus(LicenseData.getDate(startDate), LicenseData.getDate(expiryDate)));

			return lData;

		} catch (LicenseNotFoundException ex){
			log.error(ex);
		} catch (GeneralSecurityException e){
			log.error(e.getMessage(), e);
		}
		return null;
	}

	private LicenseStatus getLicenseStatus(Date startDate, Date expiryDate){
		Date today = TimeUtil.getToday();
		LicenseStatus status = LicenseStatus.Active;
		if (startDate == null || expiryDate == null){
			status = LicenseStatus.Invalid;
		} else if (expiryDate.before(today)){
			status = LicenseStatus.Expired;
		} else if (startDate.after(today)){
			status = LicenseStatus.NotStarted;
		}
		return status;
	}

	@Override
	public List<License> getLicenseByProductName(String name){
		return licenseDAO.getLicenseByProduct(name);
	}

	/**
	 * Check for validity is here (and not in a validation rule), because it's better not to share smartdec license
	 * implementation with a client
	 */
	@Override
	public List<ErrorEntry> updateLicense(License license){
		List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
		try{
			License original = licenseDAO.getLicense(license);
			if (original == null){
				errors.add(new ErrorEntry(TextID.MsgInvalidLicense));
				return errors;
			}

			license.setId(original.getId());
			LicenseManager.setPublicKey(KeyStore.publicKey);

			ByteArrayInputStream bis = new ByteArrayInputStream(license.getLicense().getBytes());

			com.smardec.license4j.License smartLicense = LicenseManager.loadLicense(bis);

			if (!LicenseManager.isValid(smartLicense))
				errors.add(new ErrorEntry(TextID.MsgInvalidLicense));
			else{
				Date expiryDate = LicenseData.getDate(smartLicense.getFeature(LicenseData.EXPIRY_DATE_PROPERTY));
				Date now = TimeUtil.getToday();
				if (expiryDate == null || expiryDate.before(now)){
					errors.add(new ErrorEntry(TextID.MsgLicenseExpired));
				}
			}
		} catch (LicenseNotFoundException ex){
			errors.add(new ErrorEntry(TextID.MsgInvalidLicense));
		} catch (GeneralSecurityException ex){
			log.error(ex);
		}
		if (errors.isEmpty())
			licenseDAO.merge(license);
		return errors;
	}

	@Override
	public License addLicense(License license){
		return licenseDAO.saveOrUpdate(license);
	}

	@Override
	public void deleteLicense(License license){
		licenseDAO.delete(license);
	}

	@Override
	public List<LicenseData> getExpiringLicenseData(){
		List<License> licenses = getLicenses();
		List<LicenseData> licenseData = new ArrayList<LicenseData>();
		if (licenses == null)
			return licenseData;
		for (License l : licenses){
			LicenseData ld = loadLicense(l);
			if (ld == null || ld.isEmpty())
				continue;

			if (!isLicenseExpiring(ld))
				continue;

			licenseData.add(ld);
		}
		return licenseData;
	}

	@Override
	public boolean isLicenseExpiring(LicenseData lData){
		String expPeriodProp = databaseProperties.getProperty(EXPIRY_PERIOD_PROPERTY);
		if (expPeriodProp == null)
			expPeriodProp = DEFAULT_EXPIRY_PERIOD;
		Integer days = Integer.parseInt(expPeriodProp);
		Date expiryDate = LicenseData.getDate(lData.get(LicenseData.EXPIRY_DATE_PROPERTY));
		Date startDate = LicenseData.getDate(lData.get(LicenseData.START_DATE_PROPERTY));
		Date now = TimeUtil.getToday();
		Date expiryLimit = TimeUtil.addDays(now, days);
		if (expiryDate.after(expiryLimit) || startDate.after(now) || expiryDate.before(now)){
			return false;
			// Not expiring if expiry date > limit || not started || expired
		}
		return true;
	}

	@Override
	public List<LicenseData> getExpiredLicenseData(){
		List<License> licenses = getLicenses();
		List<LicenseData> licenseData = new ArrayList<LicenseData>();
		if (licenses == null)
			return licenseData;
		for (License l : licenses){
			LicenseData ld = loadLicense(l);
			if (ld == null || ld.isEmpty())
				continue;
			Date expiryDate = LicenseData.getDate(ld.get(LicenseData.EXPIRY_DATE_PROPERTY));
			Date now = TimeUtil.getToday();
			if (expiryDate.before(now))
				licenseData.add(ld);
		}
		return licenseData;
	}

	@Override
	public void deleteLicenses(List<LicenseData> ldata){
		List<License> licenses = new ArrayList<License>();
		if (ldata == null)
			return;
		for (LicenseData ld : ldata)
			licenses.add(ld.getLicense());
		licenseDAO.deleteAll(licenses);
	}

	@Override
	public boolean canDeleteLicense(LicenseData lDataToRemove){
		String productName = (String) lDataToRemove.get(LicenseData.PRODUCT_NAME_PROPERTY);
		List<LicenseData> licenseDataList = getLicenseDataByProductName(productName);
		boolean isACLicense = LicenseData.ACNi3WEB_PRODUCT.equals(productName);
		List<User> users = isACLicense ? getAdministrators() : getAllUsers();

		if (isACLicense){
			log.debug("Checking AC's license whether it can be removed");
			for (AdminConsoleModule acModule : AdminConsoleModule.values()){
				String module = acModule.getValue();
				if (!checkModule(licenseDataList, lDataToRemove, users, module))
					return false;
			}
		} else{
			log.debug("Checking Navigator's license whether it can be removed");
			for (NavigatorModule nModule : NavigatorModule.values()){
				String module = nModule.getValue();
				if (!checkModule(licenseDataList, lDataToRemove, users, module))
					return false;
			}
		}

		return true;
	}

	private boolean checkModule(List<LicenseData> licenseDataList, LicenseData lDataToRemove, List<User> users, String module){
		Integer countToRemove = (Integer) lDataToRemove.get(module);
		if (countToRemove == null || countToRemove == 0)
			return true;
		int maxUserCount = getMaxUserCount(licenseDataList, module);
		int usedUserCount = getUsedUserCount(users, module);
		log.debug("Module = " + module + ", maxUserCount=" + maxUserCount + ", usedUserCount=" + usedUserCount
		        + ", countToRemove=" + countToRemove);
		if (maxUserCount - countToRemove < usedUserCount){
			log.warn("Module cannot be removed");
			return false;
		}
		return true;
	}

	@Override
	public int getMaxUserCount(List<LicenseData> licenseDataList, String module){
		int availableUserCount = 0;
		for (LicenseData lData : licenseDataList){
			if (!lData.getStatus().equals(LicenseStatus.Active))
				continue;

			Integer userCount = (Integer) lData.get(module);
			if (userCount != null && userCount > 0){
				availableUserCount += userCount;
			}
		}
		return availableUserCount;
	}

	@Override
	public int getMaxNonExpiringUserCount(List<LicenseData> licenseDataList, String module){
		int maxNonExpiringUserCount = 0;
		for (LicenseData lData : licenseDataList){
			if (!lData.getStatus().equals(LicenseStatus.Active))
				continue;
			boolean expiring = isLicenseExpiring(lData);

			Integer userCount = (Integer) lData.get(module);
			if (userCount != null && userCount > 0){
				if (!expiring)
					maxNonExpiringUserCount += userCount;
			}
		}
		return maxNonExpiringUserCount;
	}

	@Override
	public int getUsedUserCount(List<User> users, String moduleValue){
		int usedUserCount = 0;
		for (User user : users){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(moduleValue)){
					usedUserCount++;
				}
			}
		}
		return usedUserCount;
	}

	private List<User> getAdministrators(){
		Group adminGroup = groupDAO.getGroupByName(Group.ADMINISTRATORS_GROUP_NAME);
		List<User> users = adminGroup.getUsers();
		log.debug("Found administrator count: " + users.size());
		return users;
	}

	private List<User> getAllUsers(){
		List<User> users = userDAO.getUsers();
		log.debug("Found administrator count: " + users.size());
		return users;
	}
}
