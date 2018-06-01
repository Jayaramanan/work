package com.ni3.ag.navigator.client.geocoding;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by ilya on 20.9.15.
 */
public class GoogleClientSideGeocoder implements Geocoder {

    private String baseUrl;
    private String accuracy;
    private JSONParser jsonParser;

    private static final String STATUS_FIELD = "status";
    private static final String RESULTS_FIELD = "results";
    private static final String GEOMETRY_FIELD = "geometry";
    private static final String LOCATION_FIELD = "location";
    private static final String LNG_FIELD = "lng";
    private static final String LAT_FIELD = "lat";

    private static final String OK_STATUS = "OK";

    public GoogleClientSideGeocoder(String baseUrl, String accuracy){
        setBaseUrl(baseUrl);
        setAccuracy(accuracy);
        jsonParser = new JSONParser();
    }

    @Override
    public GeocodingResult geocode(String address) {
        BufferedReader in;
        JSONObject o = null;
        GoogleGeocodingResult out = new GoogleGeocodingResult();

        try {

            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            String url = getBaseUrl() + encodedAddress;
            in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            o = (JSONObject) jsonParser.parse(in);

            String status = (String) o.get(STATUS_FIELD);
            System.out.println(status);
            if (OK_STATUS.equals(status)) {
                JSONArray results = (JSONArray) o.get(RESULTS_FIELD);
                if (results.size() > 1) {
                    System.out.println("Warning, more than 1 geo result found");
                }
                if (results.size() > 0) {
                    JSONObject result = (JSONObject) results.get(0);

                    boolean isAccurateEnough = checkAccuracy(result, accuracy);
                    if (isAccurateEnough) {
                        JSONObject geometry = (JSONObject) result.get(GEOMETRY_FIELD);
                        JSONObject location = (JSONObject) geometry.get(LOCATION_FIELD);
                        Double lon = (Double) location.get(LNG_FIELD);
                        Double lat = (Double) location.get(LAT_FIELD);
                        out.setLon(lon);
                        out.setLat(lat);

                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
        return out;
    }

    private boolean checkAccuracy(JSONObject object, String minimalAccuracy) {
        //TODO: implement
        return true;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }
}
