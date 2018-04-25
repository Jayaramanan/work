package com.ni3.ag.navigator.client.geocoding;

import junit.framework.TestCase;

/**
 * Created by ilya on 20.9.15.
 */
public class GoogleClientSideGeocoderTest  extends TestCase {
    public void testGeoCode(){
        String address = "1600 Amphitheatre Parkway, Mountain View, CA";
        GoogleClientSideGeocoder geocoder = new GoogleClientSideGeocoder("https://maps.googleapis.com/maps/api/geocode/json?address=", "ROOFTOP");
        GeocodingResult result = geocoder.geocode(address);

        assert(true);
    }
}
