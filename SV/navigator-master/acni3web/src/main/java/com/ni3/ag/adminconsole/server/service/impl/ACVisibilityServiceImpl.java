/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.LicenseData.LicenseStatus;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.ACVisibilityService;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;

public class ACVisibilityServiceImpl implements ACVisibilityService{

	private final static Logger log = Logger.getLogger(ACVisibilityServiceImpl.class);

	private LicenseService licenseService;
	private UserDAO userDAO;

	public void setLicenseService(LicenseService licenseService){
		this.licenseService = licenseService;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	@Override
	public Map<TextID, Boolean> getLicenseAccesses(Integer userId){
		List<LicenseData> acLds = getValidLicenses(LicenseData.ACNi3WEB_PRODUCT);
		List<LicenseData> navigatorLds = getValidLicenses(LicenseData.NAVIGATOR_PRODUCT);
		User user = userDAO.getById(userId);
		boolean hasBaseModule = hasModuleAccess(user, AdminConsoleModule.BaseModule);
		if (!hasBaseModule){
			log.error("User doesn't have access to AC's base module (com.ni3.ag.license.userCount property)");
			return new HashMap<TextID, Boolean>();
		}
		Map<TextID, Boolean> map = getMap(acLds, navigatorLds, user);
		log.debug("Created visibility map");
		return map;
	}

	private List<LicenseData> getValidLicenses(String productName){
		List<LicenseData> lds = licenseService.getLicenseDataByProductName(productName);
		List<LicenseData> correctLds = new ArrayList<LicenseData>();
		for (LicenseData ld : lds){
			if (ld.getStatus().equals(LicenseStatus.Active)){
				correctLds.add(ld);
			}
		}
		return correctLds;
	}

	private Map<TextID, Boolean> getMap(List<LicenseData> acLds, List<LicenseData> navigatorLds, User user){
		Map<TextID, Boolean> map = new HashMap<TextID, Boolean>();
		map.put(TextID.SchemaAdministration, isModuleVisible(acLds, LicenseData.ACSCHEMA_MODULE));
		map.put(TextID.MetaphorAdministration, isModuleVisible(acLds, LicenseData.ACMETAPHOR_MODULE));
		map.put(TextID.UserAdministration, isModuleVisible(acLds, LicenseData.ACUSERS_MODULE));
		map.put(TextID.LanguageAdministration, isModuleVisible(acLds, LicenseData.ACLANGUAGE_MODULE));
		map.put(TextID.ChartAdministration,
		        isModuleVisible(acLds, LicenseData.ACCHART_MODULE)
		                && isModuleVisible(navigatorLds, LicenseData.CHARTS_MODULE));
		map.put(TextID.DataCaptureModule, isModuleVisible(navigatorLds, LicenseData.DATA_CAPTURE_MODULE));
		map.put(TextID.GeoAnalytics,
		        isModuleVisible(acLds, LicenseData.ACGEO_MODULE)
		                && isModuleVisible(navigatorLds, LicenseData.GEO_ANALYTICS_MODULE));
		map.put(TextID.Diagnostics, isModuleVisible(acLds, LicenseData.ACDIAGNOSTICS_MODULE));
		map.put(TextID.ThickClientAdministration,
		        isModuleVisible(acLds, LicenseData.ACOFFLINE_MODULE)
		                && isModuleVisible(navigatorLds, LicenseData.REMOTE_CLIENT_MODULE));
		map.put(TextID.MapAdministration,
		        isModuleVisible(acLds, LicenseData.ACGEO_MODULE) && isModuleVisible(navigatorLds, LicenseData.MAPS_MODULE));
		map.put(TextID.ReportAdministration,
		        isModuleVisible(acLds, LicenseData.ACREPORTS_MODULE)
		                && isModuleVisible(navigatorLds, LicenseData.REPORTS_MODULE));
		map.put(TextID.ETL,
		        isModuleVisible(acLds, LicenseData.ACETL_MODULE) && hasModuleAccess(user, AdminConsoleModule.ETLModule));
		return map;
	}

	private boolean hasModuleAccess(User user, AdminConsoleModule module){
		for (UserEdition ue : user.getUserEditions()){
			if (ue.getEdition().equals(module.getValue())){
				return true;
			}
		}
		return false;
	}

	private boolean isModuleVisible(List<LicenseData> licenseDataList, String key){
		for (LicenseData ldata : licenseDataList){
			Integer lcount = (Integer) ldata.get(key);
			if (lcount != null && lcount.intValue() > 0)
				return true;
		}
		return false;
	}

}
