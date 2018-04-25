/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.geoanalytics;

import java.util.Comparator;

import com.ni3.ag.navigator.shared.domain.GeoTerritory;

public class GisAggregationComparator implements Comparator<GeoTerritory>{
	private boolean sumMode;

	public GisAggregationComparator(boolean sumMode){
		this.sumMode = sumMode;
	}

	@Override
	public int compare(GeoTerritory o1, GeoTerritory o2){
		final Double val1 = o1.getValue(sumMode);
		final Double val2 = o2.getValue(sumMode);
		return val1.compareTo(val2);
	}

}
