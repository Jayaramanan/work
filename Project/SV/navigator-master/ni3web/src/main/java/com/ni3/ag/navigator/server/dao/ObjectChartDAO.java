package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.ObjectChart;

public interface ObjectChartDAO{
	List<ObjectChart> getObjectCharts(int chartId);
}
