/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.ACVisibilityService;
import com.ni3.ag.adminconsole.shared.service.def.AdminConsoleLicenseService;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;

public class ObjectVisibilityStore{

	private final static Logger log = Logger.getLogger(ObjectVisibilityStore.class);

	private Map<DatabaseInstance, Map<TextID, Boolean>> visibilityMap;
	private Map<DatabaseInstance, List<LicenseData>> expiringLicenses;
	private ACVisibilityService acService;
	private LicenseService licenceService;
	private AdminConsoleLicenseService acLicenseService;

	private static ObjectVisibilityStore instance;

	private ObjectVisibilityStore(){
		visibilityMap = new HashMap<DatabaseInstance, Map<TextID, Boolean>>();
		expiringLicenses = new HashMap<DatabaseInstance, List<LicenseData>>();
		acService = ACSpringFactory.getInstance().getACVisibilityService();
		licenceService = ACSpringFactory.getInstance().getLicenseService();
		acLicenseService = ACSpringFactory.getInstance().getACLicenseService();
	}

	private Map<TextID, Boolean> getMap(){
		Integer userId = SessionData.getInstance().getUserId();
		if (userId != null)
			return acService.getLicenseAccesses(userId);
		return new HashMap<TextID, Boolean>();
	}

	public void refreshLicenses(DatabaseInstance dbi){
		visibilityMap.remove(dbi);
		expiringLicenses.remove(dbi);
		acLicenseService.checkLicenseModules();
		getInstanceVisibilityMap(dbi);
		setExpiringLicenses(dbi);
	}

	private Map<TextID, Boolean> getInstanceVisibilityMap(DatabaseInstance dbi){
		Map<TextID, Boolean> map = visibilityMap.get(dbi);
		if (map == null){
			map = getMap();
			visibilityMap.put(dbi, map);
			log.debug("Reloaded visibility map");
		}
		return map;
	}

	public void setExpiringLicenses(DatabaseInstance dbi){
		List<LicenseData> lDatas = licenceService.getExpiringLicenseData();
		expiringLicenses.put(dbi, lDatas);
	}

	public List<LicenseData> getExpiringLicenses(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return expiringLicenses.get(dbi);
	}

	public static ObjectVisibilityStore getInstance(){
		if (instance == null)
			instance = new ObjectVisibilityStore();
		return instance;
	}

	public boolean isCorrectLicense(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		return map != null && !map.isEmpty();
	}

	public boolean isChartsScreenVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isChartsScreenVisible(dbi);
	}

	public boolean isThickClientScreenVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isThickClientScreenVisible(dbi);
	}

	public boolean isDataCaptureEnabled(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isDataCaptureEnabled(dbi);
	}

	public boolean isSchemaVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isSchemaVisible(dbi);
	}

	public boolean isMetaphorVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isMetaphorVisible(dbi);
	}

	public boolean isUserVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isUserVisible(dbi);
	}

	public boolean isLanguageVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isLanguageVisible(dbi);
	}

	public boolean isGeoAnalyticsVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isGeoAnalyticsVisible(dbi);
	}

	public boolean isDiagnosticsVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isDiagnosticsVisible(dbi);
	}

	public boolean isMapsVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isMapsVisible(dbi);
	}

	public boolean isReportsVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isReportsVisible(dbi);
	}

	private boolean isMapsVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.MapAdministration);
		return visible != null && visible;
	}

	private boolean isDiagnosticsVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.Diagnostics);
		return visible != null && visible;
	}

	private boolean isGeoAnalyticsVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.GeoAnalytics);
		return visible != null && visible;
	}

	private boolean isLanguageVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.LanguageAdministration);
		return visible != null && visible;
	}

	private boolean isUserVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.UserAdministration);
		return visible != null && visible;
	}

	private boolean isMetaphorVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.MetaphorAdministration);
		return visible != null && visible;
	}

	private boolean isSchemaVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.SchemaAdministration);
		return visible != null && visible;
	}

	public boolean isDataCaptureEnabled(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.DataCaptureModule);
		return visible != null && visible;
	}

	public boolean isChartsScreenVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.ChartAdministration);
		return visible != null && visible;
	}

	public boolean isThickClientScreenVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.ThickClientAdministration);
		return visible != null && visible;
	}

	public boolean isReportsVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.ReportAdministration);
		return visible != null && visible;
	}

	public boolean isETLVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		return isETLVisible(dbi);
	}

	public boolean isETLVisible(DatabaseInstance dbi){
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		Boolean visible = map.get(TextID.ETL);
		return visible != null && visible;
	}

	public boolean isMonitoringScreenVisible(){
		DatabaseInstance dbi = SessionData.getInstance().getCurrentDatabaseInstance();
		Map<TextID, Boolean> map = getInstanceVisibilityMap(dbi);
		return map != null && !map.isEmpty();
	}
}
