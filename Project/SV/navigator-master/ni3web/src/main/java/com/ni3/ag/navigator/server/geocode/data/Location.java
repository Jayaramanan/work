package com.ni3.ag.navigator.server.geocode.data;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.geocode.coding.GeoCoder;

public class Location {
    private static final Logger logger = Logger.getLogger(Location.class);

    public double lon;
    public double lat;
    public int precision;
    public GeoCoder.GeoCodeStatus status;

    public Location(GeoCoder.GeoCodeStatus status) {
        this.status = status;
    }

    public String toString() {
        return new StringBuilder("\t\tLat: ").append(lat)
                .append("\n\t\tLon: ").append(lon)
                .append("\n\t\tAccuracy: ").append(precision).append(" ->").append(getAccuracyDescription(precision))
                .append("\n\t\tStatus: ").append(status).append(" -> ").append(status.getDescription())
                .toString();
    }

    private String getAccuracyDescription(int precision) {
        switch (precision) {
            case 1:
                return "Country level accuracy.";
            case 2:
                return "Region (state, province, prefecture, etc.) level accuracy.";
            case 3:
                return "Sub-region (county, municipality, etc.) level accuracy.";
            case 4:
                return "Town (city, village) level accuracy.";
            case 5:
                return "Post code (zip code) level accuracy.";
            case 6:
                return "Street level accuracy.";
            case 7:
                return "Intersection level accuracy.";
            case 8:
                return "Address level accuracy.";
            case 0:
            default:
                return "Unknown accuracy";
        }
    }

    public static Location parseLocation(String slat, String slon, String spre, GeoCoder.GeoCodeStatus status) {
        Location loc = new Location(status);
        try {
            double lat = Double.parseDouble(slat);
            double lon = Double.parseDouble(slon);
            int pre = Integer.parseInt(spre);
            loc.lat = lat;
            loc.lon = lon;
            loc.precision = pre;
            return loc;
        } catch (NumberFormatException ex) {
            logger.warn("Error parse fields " + ex);
            return null;
        }
    }
}
