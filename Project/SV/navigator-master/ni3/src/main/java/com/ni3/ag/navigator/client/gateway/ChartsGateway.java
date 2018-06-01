package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.ChartAttributeDescriptor;
import com.ni3.ag.navigator.shared.domain.Chart;

public interface ChartsGateway{
	List<Chart> getChartsForUser(int schemaId);

	Chart getChartWithParameters(int chartId);

	double[] getCurrentChartMinMaxValues(List<ChartAttributeDescriptor> chartAttributes);
}
