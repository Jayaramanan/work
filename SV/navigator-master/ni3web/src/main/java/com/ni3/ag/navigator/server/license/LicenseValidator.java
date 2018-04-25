/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.license;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.adminconsole.license.KeyStore;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.CustomChecksumEncoder;
import com.ni3.ag.adminconsole.util.TimeUtil;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.navigator.server.dao.LicenseDAO;
import com.ni3.ag.navigator.shared.domain.License;
import com.smardec.license4j.LicenseManager;
import com.smardec.license4j.LicenseNotFoundException;

public class LicenseValidator extends JdbcDaoSupport{
	private final static Logger log = Logger.getLogger(LicenseValidator.class);
	private LicenseDAO licenseDAO;
	private ChecksumEncoder encoder;

	public LicenseValidator(){
		encoder = new CustomChecksumEncoder();
	}

	public void setLicenseDAO(LicenseDAO licenseDAO){
		this.licenseDAO = licenseDAO;
	}

	private List<LicenseData> getLicenseData(){
		List<LicenseData> licenseData = null;
		List<String> licenses = licenseDAO.getNavigatorLicenses();
		if (licenses.isEmpty()){
			log.error("license not found " + LicenseData.NAVIGATOR_PRODUCT);
			return null;
		}
		try{
			licenseData = getNavigatorLicenseData(licenses);
		} catch (ACException e){
			log.error(e);
		}
		return licenseData;
	}

	private List<LicenseData> getNavigatorLicenseData(List<String> licenses) throws ACException{
		LicenseManager.setPublicKey(KeyStore.publicKey);
		List<LicenseData> lDataList = new ArrayList<LicenseData>();

		for (String license : licenses){
			LicenseData lData = new LicenseData();

			try{
				ByteArrayInputStream bis = new ByteArrayInputStream(license.getBytes());
				com.smardec.license4j.License smartLicense = LicenseManager.loadLicense(bis);

				lData.put(LicenseData.PRODUCT_NAME_PROPERTY, smartLicense.getFeature(LicenseData.PRODUCT_NAME_PROPERTY));

				StringBuffer startDate = LicenseData.getDateStr(smartLicense.getFeature(LicenseData.START_DATE_PROPERTY));
				StringBuffer expiryDate = LicenseData.getDateStr(smartLicense.getFeature(LicenseData.EXPIRY_DATE_PROPERTY));
				lData.put(LicenseData.START_DATE_PROPERTY, startDate);
				lData.put(LicenseData.EXPIRY_DATE_PROPERTY, expiryDate);

				if (log.isDebugEnabled()){
					log.debug("Navigator license. start date = " + startDate + ", expiry date = " + expiryDate);
				}

				lData.put(LicenseData.BASE_MODULE, smartLicense.getFeature(LicenseData.BASE_MODULE));
				lData.put(LicenseData.DATA_CAPTURE_MODULE, smartLicense.getFeature(LicenseData.DATA_CAPTURE_MODULE));
				lData.put(LicenseData.CHARTS_MODULE, smartLicense.getFeature(LicenseData.CHARTS_MODULE));
				lData.put(LicenseData.MAPS_MODULE, smartLicense.getFeature(LicenseData.MAPS_MODULE));
				lData.put(LicenseData.GEO_ANALYTICS_MODULE, smartLicense.getFeature(LicenseData.GEO_ANALYTICS_MODULE));
				lData.put(LicenseData.REMOTE_CLIENT_MODULE, smartLicense.getFeature(LicenseData.REMOTE_CLIENT_MODULE));
				lData.put(LicenseData.REPORTS_MODULE, smartLicense.getFeature(LicenseData.REPORTS_MODULE));
				if (!LicenseManager.isValid(smartLicense)){
					log.warn("License is invalid");
				} else{
					lDataList.add(lData);
				}
			} catch (LicenseNotFoundException ex){
				log.error(ex.getMessage());
				log.debug("", ex);
			} catch (GeneralSecurityException e){
				log.error(e.getMessage());
				log.debug("", e);
			}
		}

		return lDataList;
	}

	private List<LicenseData> filterCorrectLicenses(List<LicenseData> lDataList){
		List<LicenseData> correctLicenses = new ArrayList<LicenseData>();
		for (LicenseData licenseData : lDataList){
			Date now = TimeUtil.getToday();
			Date start = LicenseData.getDate(licenseData.get(LicenseData.START_DATE_PROPERTY));
			Date end = LicenseData.getDate(licenseData.get(LicenseData.EXPIRY_DATE_PROPERTY));
			if (start == null || end == null || now.before(start) || now.after(end)){
				log.warn("License is expired: start date = " + start + ", expiry date = " + end);
				continue;
			}
			if (!hasBaseModule(licenseData)){
				log.warn("License is invalid: Base module unavailable");
				continue;
			}
			if (hasGeoAnalyticsModule(licenseData) && !hasMapsModule(licenseData)){
				log.warn("License is invalid: Geo-analytics module is without Maps module");
				continue;
			}
			correctLicenses.add(licenseData);
		}
		return correctLicenses;
	}

	private void checkUsedLicenseCount(List<LicenseData> licenseData){
		checkModule(LicenseData.BASE_MODULE, licenseData);
		checkModule(LicenseData.DATA_CAPTURE_MODULE, licenseData);
		checkModule(LicenseData.CHARTS_MODULE, licenseData);
		checkModule(LicenseData.MAPS_MODULE, licenseData);
		checkModule(LicenseData.GEO_ANALYTICS_MODULE, licenseData);
		checkModule(LicenseData.REMOTE_CLIENT_MODULE, licenseData);
		checkModule(LicenseData.REPORTS_MODULE, licenseData);
	}

	private void checkModule(String module, List<LicenseData> licenseData){
		int lCount = 0;
		for (LicenseData lData : licenseData){
			Integer cnt = (Integer) lData.get(module);
			if (cnt != null){
				lCount += cnt;
			}
		}
		List<Integer> usedList = getUsedLicenses(module);

		if (usedList.size() > lCount){
			if (log.isDebugEnabled()){
				log.debug("Removing excess accesses to modules: available = " + lCount + ", used = " + usedList.size());
			}
			removeLicenses(usedList, lCount);
		}
	}

	public License getLicense(Integer userId){
		List<LicenseData> licenseData = getValidLicenses();

		License license = new License();
		license.setValid(licenseData != null && !licenseData.isEmpty());
		if (license.isValid()){
			List<String> userModules = getUserModules(userId);
			license.setBaseModule(hasBaseModule(licenseData) && userModules.contains(LicenseData.BASE_MODULE));
			if (license.hasBaseModule()){
				license.setChartsModule(hasChartsModule(licenseData) && userModules.contains(LicenseData.CHARTS_MODULE));
				license.setDataCaptureModule(hasDataCaptureModule(licenseData)
				        && userModules.contains(LicenseData.DATA_CAPTURE_MODULE));
				license.setRemoteClientModule(hasThickClientModule(licenseData)
				        && userModules.contains(LicenseData.REMOTE_CLIENT_MODULE));
				license.setMapsModule(hasMapsModule(licenseData) && userModules.contains(LicenseData.MAPS_MODULE));
				if (license.hasMapsModule()){
					license.setGeoAnalyticsModule(hasGeoAnalyticsModule(licenseData)
					        && userModules.contains(LicenseData.GEO_ANALYTICS_MODULE));
				}
				license.setReportsModule(hasReportsModule(licenseData) && userModules.contains(LicenseData.REPORTS_MODULE));
			}
		}

		return license;
	}

	public List<LicenseData> getValidLicenses(){
		List<LicenseData> lDataList = getLicenseData();
		if (lDataList == null || lDataList.isEmpty()){
			log.error("License not found for navigator");
			return null;
		}
		List<LicenseData> correctLicenses = filterCorrectLicenses(lDataList);

		checkUsedLicenseCount(correctLicenses);

		if (correctLicenses == null || correctLicenses.isEmpty()){
			log.error("No correct license found for navigator");
			return null;
		}
		return correctLicenses;
	}

	public boolean hasThickClientModule(Integer userId, List<LicenseData> licenseData){
		boolean result = false;
		if (licenseData != null && !licenseData.isEmpty()){
			List<String> userModules = getUserModules(userId);
			result = hasBaseModule(licenseData) && userModules.contains(LicenseData.BASE_MODULE)
			        && hasThickClientModule(licenseData) && userModules.contains(LicenseData.REMOTE_CLIENT_MODULE);
		}
		return result;
	}

	private boolean hasBaseModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasBaseModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasBaseModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.BASE_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasDataCaptureModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasDataCaptureModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasDataCaptureModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.DATA_CAPTURE_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasChartsModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasChartsModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasChartsModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.CHARTS_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasMapsModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasMapsModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasMapsModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.MAPS_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasGeoAnalyticsModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasGeoAnalyticsModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasGeoAnalyticsModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.GEO_ANALYTICS_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasThickClientModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasThickClientModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasThickClientModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.REMOTE_CLIENT_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	private boolean hasReportsModule(List<LicenseData> lDataList){
		for (LicenseData licenseData : lDataList){
			if (hasReportsModule(licenseData))
				return true;
		}
		return false;
	}

	private boolean hasReportsModule(LicenseData licenseData){
		Integer lcount = (Integer) licenseData.get(LicenseData.REPORTS_MODULE);
		return (lcount != null && lcount.intValue() > 0);
	}

	@SuppressWarnings("unchecked")
	private List<Integer> getUsedLicenses(String module){
		String sql = "SELECT id from sys_user_edition where editionId = ? order by expiring desc";
		final List<Integer> result = getJdbcTemplate().queryForList(sql, new Object[] { module }, Integer.class);
		return result;
	}

	private List<String> getUserModules(final Integer uId){
		final List<String> result = new ArrayList<String>();
		final List<Integer> incorrectRecords = new ArrayList<Integer>();

		String sql = "SELECT editionid, checksum, id from sys_user_edition where userId = ?";
		getJdbcTemplate().query(sql, new Object[] { uId }, new RowMapper(){

			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
				String editionId = rs.getString(1);
				String checksum = rs.getString(2);
				String checksumCorrect = encoder.encode(uId, editionId);
				if (checksum != null && checksum.equals(checksumCorrect)){
					result.add(editionId);
				} else{
					log.warn("Checksum incorrect for module " + editionId + ", userId " + uId);
					incorrectRecords.add(rs.getInt(3));
				}
				return null;
			}
		});

		if (!incorrectRecords.isEmpty()){
			removeLicenses(incorrectRecords, 0);
		}
		return result;
	}

	private void removeLicenses(List<Integer> allIds, Integer licenseCount){
		String sql = "delete from sys_user_edition where id in (";
		for (int i = 0; i < allIds.size() - licenseCount; i++){
			if (i > 0){
				sql += ",";
			}
			sql += allIds.get(i);
		}
		sql += ")";
		getJdbcTemplate().update(sql);
	}
}
