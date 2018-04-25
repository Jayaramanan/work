/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

public enum NavigatorModule{
	BaseModule(LicenseData.BASE_MODULE), DataCaptureModule(LicenseData.DATA_CAPTURE_MODULE), ChartsModule(
	        LicenseData.CHARTS_MODULE), MapsModule(LicenseData.MAPS_MODULE), GeoAnalyticsModule(
	        LicenseData.GEO_ANALYTICS_MODULE), RemoteClientModule(LicenseData.REMOTE_CLIENT_MODULE), ReportsModule(
	        LicenseData.REPORTS_MODULE);

	private String value;

	NavigatorModule(String val){
		value = val;
	}

	public String getValue(){
		return value;
	}

	public static NavigatorModule getNavigatorModule(String val){
		for (NavigatorModule type : values()){
			if (type.getValue().equals(val)){
				return type;
			}
		}
		return null;
	}
}
