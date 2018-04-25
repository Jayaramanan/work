package com.ni3.ag.navigator.server.services;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.Chart;
import com.ni3.ag.navigator.shared.domain.ChartAttribute;
import com.ni3.ag.navigator.shared.domain.ObjectChart;

public interface ChartService{
	List<Chart> getChartForUser(int schemaId);

	List<ObjectChart> getObjectCharts(int chartId);

	List<ChartAttribute> getChartAttributes(int chartId);

	Chart getChartWithParameters(int chartId);

	double[] getCurrentChartMinMaxValues(List<Integer> attributeIds, int entityId);
}
