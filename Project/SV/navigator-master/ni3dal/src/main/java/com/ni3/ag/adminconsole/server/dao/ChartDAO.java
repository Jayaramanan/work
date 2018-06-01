/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.Schema;

public interface ChartDAO{
	public void saveOrUpdate(Chart ch);

	public void delete(Chart chart);

	public List<Chart> getChartsBySchema(Schema origSchema);

	public void saveOrUpdateAll(List<Chart> charts);

	public Chart getChart(int chartID);

	void saveOrUpdateChartAttribute(ChartAttribute attr);

	public List<Chart> getAllCharts();
}
