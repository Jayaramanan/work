/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;

public class ChartTableModelTest extends TestCase{

	List<ObjectChart> charts;
	private ChartType chartType;
	private ChartDisplayOperation displayOperation;
	private ObjectDefinition object;

	@Override
	protected void setUp() throws Exception{
		charts = new ArrayList<ObjectChart>();
		chartType = ChartType.PIE;

		displayOperation = ChartDisplayOperation.SUM;

		object = new ObjectDefinition();
		object.setId(100);

		ObjectChart ch = new ObjectChart();
		ch.setChartType(chartType);
		ch.setDisplayOperation(displayOperation);
		ch.setIsValueDisplayed(true);
		ch.setLabelFontSize("Dialog,1,10");
		ch.setLabelInUse(true);
		ch.setMaxScale(new BigDecimal(1.0));
		ch.setMaxValue(2);
		ch.setMinScale(new BigDecimal(3.0));
		ch.setMinValue(4);
		ch.setNumberFormat("nf");
		ch.setObject(object);
		ch.setFontColor("#000");
		charts.add(ch);
	}

	public void testGetRowCount(){
		ChartTableModel model = new ChartTableModel(charts);
		assertEquals(1, model.getRowCount());
	}

	public void testGetValueAt(){
		ChartTableModel model = new ChartTableModel(charts);
		assertEquals(object, model.getValueAt(0, 0));
		assertEquals(4, model.getValueAt(0, 1));
		assertEquals(2, model.getValueAt(0, 2));
		assertEquals(new BigDecimal(3.0), model.getValueAt(0, 3));
		assertEquals(new BigDecimal(1.0), model.getValueAt(0, 4));
		assertEquals(true, model.getValueAt(0, 5));
		assertEquals("Dialog", model.getValueAt(0, 6));
		assertEquals(false, model.getValueAt(0, 7));
		assertEquals(true, model.getValueAt(0, 8));
		assertEquals(displayOperation, model.getValueAt(0, 12));
		assertEquals(chartType, model.getValueAt(0, 13));
		assertEquals(true, model.getValueAt(0, 14));
	}
}
