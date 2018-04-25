/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.license;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class NavigatorModuleTest extends TestCase{
	public void testValues(){
		Map<NavigatorModule, String> _map = new HashMap<NavigatorModule, String>();

		_map.put(NavigatorModule.BaseModule, LicenseData.BASE_MODULE);
		_map.put(NavigatorModule.DataCaptureModule, LicenseData.DATA_CAPTURE_MODULE);
		_map.put(NavigatorModule.ChartsModule, LicenseData.CHARTS_MODULE);
		_map.put(NavigatorModule.MapsModule, LicenseData.MAPS_MODULE);
		_map.put(NavigatorModule.GeoAnalyticsModule, LicenseData.GEO_ANALYTICS_MODULE);
		_map.put(NavigatorModule.RemoteClientModule, LicenseData.REMOTE_CLIENT_MODULE);
		_map.put(NavigatorModule.ReportsModule, LicenseData.REPORTS_MODULE);

		for (NavigatorModule nm : NavigatorModule.values()){
			assertTrue(_map.containsKey(nm));
			String mod = _map.get(nm);
			assertNotNull(mod);
			NavigatorModule foundNM = NavigatorModule.getNavigatorModule(mod);
			assertEquals(nm, foundNM);
		}

	}
}
