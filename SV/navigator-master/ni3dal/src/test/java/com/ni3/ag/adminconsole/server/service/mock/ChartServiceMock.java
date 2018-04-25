/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.service.def.ChartService;
import com.ni3.ag.adminconsole.validation.ACException;

public class ChartServiceMock implements ChartService{

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = new ArrayList<Schema>();
		Schema schema = new Schema();
		schema.setId(1);
		schema.setName("schema1");
		schemas.add(schema);
		schema.setObjectDefinitions(generateObjectDefinitions(schema));
		schema.setCharts(generateCharts(schema));

		schema = new Schema();
		schemas.add(schema);
		schema.setId(2);
		schema.setName("schema2");
		schema.setObjectDefinitions(generateObjectDefinitions(schema));
		schema.setCharts(generateCharts(schema));
		return schemas;
	}

	private List<ObjectDefinition> generateObjectDefinitions(Schema parent){
		List<ObjectDefinition> obsj = new ArrayList<ObjectDefinition>();
		for (int i = 1; i < 3; i++){
			ObjectDefinition od = new ObjectDefinition();
			od.setId(i * parent.getId());
			od.setName("object def" + od.getId());
			od.setSchema(parent);
			obsj.add(od);
		}
		return obsj;
	}

	private List<Chart> generateCharts(Schema parent){
		List<Chart> charts = new ArrayList<Chart>();
		for (int i = 1; i < 5; i++){
			charts.add(generateChart(i * parent.getId(), parent));
		}
		return charts;
	}

	private Chart generateChart(int id, Schema parent){
		Chart ch = new Chart();
		ch.setComment("comment" + id);
		ch.setId(id);
		ch.setName("chart_name" + id);
		ch.setSchema(parent);
		ch.setObjectCharts(generateObjectCharts(ch, parent));
		return ch;
	}

	private List<ChartAttribute> generateChartAttributes(ObjectChart ch){
		List<ChartAttribute> attrs = new ArrayList<ChartAttribute>();
		for (int i = 1; i <= 5; i++){
			ChartAttribute cca = new ChartAttribute();
			cca.setObjectChart(ch);
			cca.setId(ch.getId() * i);
			cca.setRgb("000000");
			attrs.add(cca);
		}
		return attrs;
	}

	private List<ObjectChart> generateObjectCharts(Chart ch, Schema parent){
		List<ObjectChart> ocharts = new ArrayList<ObjectChart>();
		for (int i = 1; i <= 5; i++){
			ObjectChart oc = new ObjectChart();
			oc.setChart(ch);
			oc.setChartType(ChartType.PIE);
			oc.setDisplayOperation(ChartDisplayOperation.SUM);
			oc.setIsValueDisplayed(Boolean.TRUE);
			oc.setLabelFontSize("10");
			oc.setLabelInUse(Boolean.TRUE);
			oc.setMaxScale(BigDecimal.ZERO);
			oc.setMaxValue(i);
			oc.setMinScale(BigDecimal.ZERO);
			oc.setMinValue(0);
			oc.setNumberFormat("%d");
			oc.setObject(parent.getObjectDefinitions().get(0));
			ocharts.add(oc);
		}
		return ocharts;
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitions(){
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Chart saveChart(Chart ch){
		return ch;
	}

	@Override
	public Chart getChart(int chartID){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Chart> getAllCharts(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteChart(Chart chart, boolean force) throws ACException{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ni3.ag.adminconsole.shared.service.def.ChartService#updateChart(com.ni3.ag.adminconsole.domain.Chart)
	 */
	@Override
	public Chart updateChart(Chart chart){
		// TODO Auto-generated method stub
		return null;
	}

}
