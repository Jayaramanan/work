package com.ni3.ag.navigator.server.dao;

import java.util.Map;

import com.ni3.ag.navigator.server.geocode.data.GeoCodeError;
import com.ni3.ag.navigator.server.geocode.data.Location;

public interface GeoCodeErrDAO {
	void updateErrorResult(int id, String sAddr, Location adr);

	void insertErrorResult(int id, String sAddr, Location adr);

	Map<Integer, GeoCodeError> getPreviousErrorsMap();

	void removeError(int id);
}
