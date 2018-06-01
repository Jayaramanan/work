/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;

public class ChartAttributeTableModelTest extends TestCase{
	List<ChartAttribute> attributes;
	private ObjectChart objectChart;
	private ObjectAttribute attribute;

	@Override
	protected void setUp() throws Exception{
		objectChart = new ObjectChart();
		attribute = new ObjectAttribute();
		attributes = new ArrayList<ChartAttribute>();
		ChartAttribute cca = new ChartAttribute();
		cca.setObjectChart(objectChart);
		cca.setAttribute(attribute);
		cca.setRgb("00");
		attributes.add(cca);
	}

	public void testGetRowCount(){
		ChartAttributeTableModel model = new ChartAttributeTableModel(attributes);
		assertEquals(1, model.getRowCount());
	}

	public void testGetValueAt(){
		ChartAttributeTableModel model = new ChartAttributeTableModel(attributes);
		assertEquals(attribute, model.getValueAt(0, 0));
		assertEquals("00", model.getValueAt(0, 1));
	}

	public void testSetValueAt(){
		ChartAttributeTableModel model = new ChartAttributeTableModel(attributes);

		model.setValueAt(attribute, 0, 0);
		model.setValueAt("11", 0, 1);

		assertEquals(attribute, attributes.get(0).getAttribute());
		assertEquals("11", attributes.get(0).getRgb());
	}

}
