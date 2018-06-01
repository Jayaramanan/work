package com.ni3.ag.navigator.client.geocoding;

/**
 * Created by ilya on 22.9.15.
 */
public class GoogleGeocodingResult implements GeocodingResult{
    private double lon;
    private double lat;

    public GoogleGeocodingResult(double lon, double lat){
        setLon(lon);
        setLat(lat);
    }

    public GoogleGeocodingResult(){
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLon() {
        return lon;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }
}
