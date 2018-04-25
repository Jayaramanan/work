package com.ni3.ag.navigator.client.domain.cache;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.GISPolygon;

public class GeoAnalyticsCache extends AbstractObjectCache<List<GISPolygon>>{

	private static final GeoAnalyticsCache instance = new GeoAnalyticsCache();

	public static GeoAnalyticsCache getInstance(){
		return instance;
	}

	private String getFileName(final int territoryId, final Integer geometryId, final int version){
		return String.valueOf(version + "-" + territoryId + "-" + geometryId);
	}

	@Override
	protected String getName(){
		return "geoanalytics";
	}

	public List<GISPolygon> getPolygons(final int territoryId, final Integer geometryId, final int version){
		return load(getFileName(territoryId, geometryId, version));
	}

	public boolean savePolygons(final int territoryId, final int geometryId, final int version,
	        final List<GISPolygon> geometry){
		return save(getFileName(territoryId, geometryId, version), geometry);
	}

}
