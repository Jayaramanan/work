package com.ni3.ag.navigator.client.geocoding;

/**
 * Created by ilya on 22.9.15.
 */
public class GeocodingMock implements Geocoder{
    @Override
    public GeocodingResult geocode(String address) {
        return new GoogleGeocodingResult(-122.0840084, 37.422245);
    }
}
