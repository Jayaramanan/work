/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.validation.ACException;

/**
 * 
 * @author demon
 */
public interface ChartService{
	public List<Schema> getSchemas();

	public List<ObjectDefinition> getObjectDefinitions();

	public void deleteChart(Chart chart, boolean force) throws ACException;

	public Chart saveChart(Chart chart);

	public Chart updateChart(Chart chart);

	public Chart getChart(int chartID);

	public List<Chart> getAllCharts();

}
