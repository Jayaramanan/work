package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.ChartAttribute;

public interface ChartAttributeDAO{
	List<ChartAttribute> getChartAttributes(int chartId);
}
