package com.ni3.ag.navigator.server.geocode.coding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import com.ni3.ag.navigator.server.geocode.Config;
import com.ni3.ag.navigator.server.geocode.data.Location;

public class GoogleGeoCoder extends GoogleGeoCoderImpl{
	private static final Logger log = Logger.getLogger(GoogleGeoCoder.class);
	private int tooManyQueriesCount;

	public Location getLocation(String address, Config config) throws IOException{
		Location location = super.getLocation(address, config);
		if (location.status == GeoCodeStatus.G_GEO_TOO_MANY_QUERIES){
			tooManyQueriesCount++;
			log.debug("tooManyQueriesCount: " + tooManyQueriesCount);
			if(tooManyQueriesCount >= config.getTooManyQueriesCountToAbort()){
				int count = tooManyQueriesCount;
				tooManyQueriesCount = 0;
				throw new GoogleTooManyQueriesAbortException(count);
			}
			mySleep(60000);
		}
		else{
			tooManyQueriesCount = 0;
			mySleep(1000);
		}

		return location;
	}

	private void mySleep(long i){
		try{
			Thread.sleep(i);
		} catch (InterruptedException ignore){
		}
	}
	
	public static class GoogleTooManyQueriesAbortException extends IOException{
		public GoogleTooManyQueriesAbortException(int count){
			super("Too many queries count " + count);
		}
	}
}

class GoogleGeoCoderImpl implements GeoCoder{
	private static final Logger logger = Logger.getLogger(GoogleGeoCoder.class);
	private static final String MAPS_URL_BASE = "http://maps.google.com/maps/";
	private static final String KEY = "ABQIAAAAHsvMy4_8ZUStvjVB_ayH2BRPspZCc14mgczgDyH15Z3uqopbAhSGv6-9-aGpCBcgd7ZsOq3LO6tgVw";


	public Location getLocation(String address, Config config) throws IOException{
		if (address == null || address.trim().isEmpty()){
			logger.warn("Invalid address " + address);
			return new Location(GeoCodeStatus.CODER_INVALID_ADDRESS);
		}
		String url = new StringBuilder(MAPS_URL_BASE).append("geo?q=").append(URLEncoder.encode(address, "UTF-8")).append("&output=csv&key=").append(KEY).toString();
		logger.trace(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		String line;
		List<Location> locations = new ArrayList<Location>();
		while ((line = in.readLine()) != null){
			logger.debug("IN: " + line);
			locations.add(parseLocation(line));
		}
		dumpResults(locations, false);
		if (!haveValidResult(locations, config)){
			GeoCodeStatus status = getErrorStatusToReturn(locations);
			return new Location(status);
		}

		if (!haveOneValidResult(locations)){
			dumpResults(locations, true);
			return new Location(GeoCodeStatus.CODER_TOO_MANY_VALID_RESULTS);
		}
		return getBestResult(locations);
	}

	private GeoCodeStatus getErrorStatusToReturn(List<Location> locations){
		for (Location l : locations){
			if (l.status != GeoCodeStatus.G_GEO_SUCCESS)
				return l.status;
		}
		return GeoCodeStatus.CODER_NO_VALID_RESULT;
	}

	private static Location getBestResult(List<Location> locations){
		Location best = null;
		for (Location l : locations)
			if (best == null || best.precision < l.precision)
				best = l;
		if (best == null)
			return new Location(GeoCodeStatus.CODER_NO_VALID_RESULT);
		return best;
	}

	private static boolean haveOneValidResult(List<Location> locations){
		int count = 0;
		for (Location l : locations)
			if (l.precision >= 8)
				count++;
		if (count > 1){
			logger.warn("More then one result is accurate enough to use (" + count + ")");
			return false;
		}
		return true;
	}

	private static boolean haveValidResult(List<Location> locations, Config config){
		for (Location l : locations)
			if (l.precision >= config.getMinAccuracy())
				return true;
		dumpResults(locations, true);
		return false;
	}

	private static void dumpResults(List<Location> locations, boolean toError){
		for (Location l : locations){
			if (toError){
				logger.warn("\tParsed result: ");
				logger.warn(l.toString());
			} else{
				logger.debug("\tParsed result: ");
				logger.debug(l.toString());
			}
		}
	}

	private static Location parseLocation(String line){
		String[] fields = line.split(",");
		if (fields.length == 0){
			logger.warn("Cannot find status code in result line " + line);
			return new Location(GeoCodeStatus.CODER_ERROR_PARSE_RESULT_INVALID_FIELD_COUNT);
		}
		int iStatus;
		try{
			iStatus = Integer.parseInt(fields[0]);
		} catch (NumberFormatException ex){
			logger.warn("Error parse status code " + fields[0]);
			return new Location(GeoCodeStatus.CODER_ERROR_PARSE_RESULT_STATUS);
		}

		GeoCodeStatus status = GeoCodeStatus.forInt(iStatus);
		if (status == null){
			logger.warn("Cannot resolve service response status " + iStatus);
			return new Location(GeoCodeStatus.CODER_ERROR_PARSE_RESULT_UNKNOWN_STATUS);
		}

		if (status == GeoCodeStatus.G_GEO_SUCCESS){
			if (fields.length != 4){
				logger.warn("Status code is success but field count != 4");
				return new Location(GeoCodeStatus.CODER_ERROR_PARSE_RESULT_INVALID_FIELD_COUNT);
			}
			Location loc = Location.parseLocation(fields[2], fields[3], fields[1], status);
			if (loc == null){
				logger.warn("Error parse location fields lat=" + fields[2] + "| lon=" + fields[3] + "| presicion=" + fields[1]);
				return new Location(GeoCodeStatus.CODER_ERROR_PARSE_RESULT_INVALID_FORMAT);
			}
			return loc;
		} else
			return new Location(status);
	}

}
