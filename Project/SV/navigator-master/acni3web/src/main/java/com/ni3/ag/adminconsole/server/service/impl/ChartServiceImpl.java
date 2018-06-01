/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ChartDAO;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.shared.service.def.ChartService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ChartServiceImpl implements ChartService{
	private static final Logger log = Logger.getLogger(ChartServiceImpl.class);
	private ObjectDefinitionDAO objectDefinitionDAO;
	private SchemaDAO schemaDAO;
	private ChartDAO chartDAO;
	private GroupDAO groupDAO;

	private ACValidationRule chartFavoriteReferenceRule;

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public ChartDAO getChartDAO(){
		return chartDAO;
	}

	public void setChartDAO(ChartDAO chartDAO){
		this.chartDAO = chartDAO;
	}

	public void setChartFavoriteReferenceRule(ACValidationRule chartFavoriteReferenceRule){
		this.chartFavoriteReferenceRule = chartFavoriteReferenceRule;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = schemaDAO.getSchemasWithAggregableAttributes();
		for (Schema schema : schemas){
			Hibernate.initialize(schema.getCharts());
			for (Chart ch : schema.getCharts()){
				Hibernate.initialize(ch);
				for (ObjectChart oc : ch.getObjectCharts()){
					Hibernate.initialize(oc.getObject());
					ObjectDefinition od = oc.getObject();
					Hibernate.initialize(od.getObjectAttributes());
					for (ChartAttribute ca : oc.getChartAttributes()){
						Hibernate.initialize(ca.getAttribute());
					}
				}
			}
		}
		return schemas;
	}

	@Override
	public List<ObjectDefinition> getObjectDefinitions(){
		List<ObjectDefinition> nodes = objectDefinitionDAO.getNodeLikeObjectDefinitions();
		for (ObjectDefinition od : nodes){
			Hibernate.initialize(od);
			Hibernate.initialize(od.getSchema());
			Hibernate.initialize(od.getObjectAttributes());
		}
		return nodes;
	}

	@Override
	public Chart saveChart(Chart ch){
		if (ch.getId() == null){ // new chart
			ch.setChartGroups(new ArrayList<ChartGroup>());
			final List<Group> groups = groupDAO.getGroups();
			for (Group group : groups){
				ChartGroup cg = new ChartGroup(group, ch);
				ch.getChartGroups().add(cg);
				group.getChartGroups().add(cg);
			}
		}
		chartDAO.saveOrUpdate(ch);
		return ch;
	}

	@Override
	public Chart updateChart(Chart chart){
		chartDAO.saveOrUpdate(chart);
		return chart;
	}

	@Override
	public void deleteChart(Chart chart, boolean force) throws ACException{
		if (!force){
			ChartModel model = new ChartModel();
			model.setCurrentObject(chart);

			if (!chartFavoriteReferenceRule.performCheck(model)){
				log.error("Cannot delete object, it is referenced from favorites");
				throw new ACException(chartFavoriteReferenceRule.getErrorEntries());
			}
		}
		chartDAO.delete(chart);
	}

	@Override
	public Chart getChart(int chartID){
		Chart chart = chartDAO.getChart(chartID);
		Hibernate.initialize(chart.getObjectCharts());
		for (ObjectChart oc : chart.getObjectCharts()){
			Hibernate.initialize(oc.getObject());
			ObjectDefinition od = oc.getObject();
			Hibernate.initialize(od.getObjectAttributes());
			for (ChartAttribute ca : oc.getChartAttributes()){
				Hibernate.initialize(ca.getAttribute());
			}

		}
		return chart;
	}

	@Override
	public List<Chart> getAllCharts(){
		List<Chart> charts = chartDAO.getAllCharts();
		return charts;
	}

}
