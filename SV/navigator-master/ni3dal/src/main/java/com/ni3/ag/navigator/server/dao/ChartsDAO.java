package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.Chart;

public interface ChartsDAO{
	List<Chart> getChartsForGroup(int groupId, int schemaId);

	Chart getChart(int chartId);
}
