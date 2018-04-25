/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.maps;

public class WGS84Conversion{
	static public int convertLongitudeToM(double longitude, double latitude){
		return (int) (longitude * 110574); // Mercator - Google like
	}

	static public int convertLatitudeToM(double latitude){
		if (latitude > 80)
			latitude = 80;
		if (latitude < -80)
			latitude = -80;

		double sinlat = Math.sin(latitude * Math.PI / 180.0);
		double res = 6378137.0 / 2.0 * Math.log((1 + sinlat) / (1 - sinlat));

		return (int) (10000000 - res); // Mercator Google like
	}

	static public double convertMToLongitude(double m, double latitude){
		return (m / 110574);
	}

	static public double convertMToLatitude(double m){
		double lat, pow;
		pow = Math.pow(Math.E, 2.0 * (10000000 - m) / 6378137.0);
		lat = Math.asin((pow - 1) / (pow + 1)) * 180.0 / Math.PI;

		return lat;
	}

}
