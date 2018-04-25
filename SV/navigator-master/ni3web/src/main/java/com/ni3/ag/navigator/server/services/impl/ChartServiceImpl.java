package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.ChartAttributeDAO;
import com.ni3.ag.navigator.server.dao.ChartsDAO;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.dao.ObjectChartDAO;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.ChartService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.Chart;
import com.ni3.ag.navigator.shared.domain.ChartAttribute;
import com.ni3.ag.navigator.shared.domain.ObjectChart;
import org.apache.log4j.Logger;

public class ChartServiceImpl implements ChartService{
	private static final Logger log = Logger.getLogger(ChartServiceImpl.class);
	private ChartsDAO chartsDAO;
	private ObjectChartDAO objectChartDAO;
	private ChartAttributeDAO chartAttributeDAO;
	private SchemaLoaderService schemaLoaderService;

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	public void setObjectChartDAO(ObjectChartDAO objectChartDAO){
		this.objectChartDAO = objectChartDAO;
	}

	public void setChartAttributeDAO(ChartAttributeDAO chartAttributeDAO){
		this.chartAttributeDAO = chartAttributeDAO;
	}

	public void setChartsDAO(ChartsDAO chartsDAO){
		this.chartsDAO = chartsDAO;
	}

	@Override
	public List<Chart> getChartForUser(int schemaId){
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		Group g = groupDAO.getByUser(storage.getCurrentUser().getId());

		ChartsDAO chartsDAO = NSpringFactory.getInstance().getChartsDAO();
		return chartsDAO.getChartsForGroup(g.getId(), schemaId);
	}

	@Override
	public List<ObjectChart> getObjectCharts(int chartId){
		ObjectChartDAO objectChartDAO = NSpringFactory.getInstance().getObjectChartDAO();
		return objectChartDAO.getObjectCharts(chartId);
	}

	@Override
	public List<ChartAttribute> getChartAttributes(int chartId){
		ChartAttributeDAO chartAttributeDAO = NSpringFactory.getInstance().getChartAttributeDAO();
		return chartAttributeDAO.getChartAttributes(chartId);
	}

	@Override
	public Chart getChartWithParameters(int chartId){
		Chart chart = chartsDAO.getChart(chartId);
		final List<ObjectChart> objectCharts = objectChartDAO.getObjectCharts(chartId);
		chart.setObjectCharts(objectCharts);
		for (ObjectChart oc : objectCharts){
			final List<ChartAttribute> chartAttributes = chartAttributeDAO.getChartAttributes(oc.getId());
			oc.setChartAttributes(chartAttributes);
		}
		return chart;
	}

	@Override
	public double[] getCurrentChartMinMaxValues(List<Integer> attributeIds, int entityId){
		if(attributeIds.isEmpty())
			return new double[]{};
		ObjectDefinition entity = getEntity(entityId);
		Map<String, List<Attribute>> dataSourceAttributeMap = new HashMap<String, List<Attribute>>();
		for(Attribute attribute : entity.getAttributes()){
			if(attributeIds.contains(attribute.getId())){
				if(!dataSourceAttributeMap.containsKey(attribute.getDataSource()))
					dataSourceAttributeMap.put(attribute.getDataSource(), new ArrayList<Attribute>());
				dataSourceAttributeMap.get(attribute.getDataSource()).add(attribute);
			}
		}
		List<Double[]> results = new ArrayList<Double[]>();
		for(String dataSource : dataSourceAttributeMap.keySet()){
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(dataSource);
			List<Attribute> attributes = dataSourceAttributeMap.get(dataSource);
			log.debug("Getting data from dataSource: " + attributeDataSource + " attributes count: " + attributes.size());
			results.add(attributeDataSource.getRowMaxRowSumMaxRowSumMin(attributes));
			log.debug("Data load completed");
		}
		return getGlobalRowMaxRowSumMaxRowSumMin(results);
	}

	private double[] getGlobalRowMaxRowSumMaxRowSumMin(List<Double[]> results){
		double max = 0, rowSumMax = 0, rowSumMin = 0;
		for(Double[] result : results){
			if(max < result[0])
				max = result[0];
			rowSumMax += result[1];
			rowSumMin += result[2];
		}
		return new double[]{rowSumMin, rowSumMax, max};
	}

	private ObjectDefinition getEntity(int entityId){
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		for(Schema schema : schemas)
			for(ObjectDefinition entity : schema.getDefinitions())
				if(entity.getId() == entityId)
					return entity;
		return null;
	}
}
