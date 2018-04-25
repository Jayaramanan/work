/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.charts;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.*;

import com.ni3.ag.navigator.client.controller.DynamicChart;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gateway.ChartsGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpChartsGatewayImpl;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.*;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;

public class ChartController{
	private Ni3Document doc;
	private SNA sna;

	private static ChartController instance;

	public static void initialize(Ni3Document doc){
		instance = new ChartController(doc);
	}

	public static ChartController getInstance(){
		return instance;
	}

	private ChartController(Ni3Document doc){
		this.doc = doc;
	}

	public boolean setChart(int chartId){
		boolean result = true;
		if (chartId == 0){
			doc.resetChart();
		} else if (chartId == SNA.SNA_CHART_ID){
			doSNABasic();
		} else if (chartId == DynamicChart.DYNAMIC_CHART_ID){
			Map<Integer, ChartParams> paramsMap = null;
			if (doc.getCurrentChartId() == chartId){
				paramsMap = doc.getChartParams();
			}
			final DynamicChart dynamicChart = new DynamicChart(paramsMap, doc.DB.schema.definitions);
			if (dynamicChart.showDynamicChartDialog()){
				doc.resetChart();
				paramsMap = fillDynamicChartParams(doc.DB.schema, dynamicChart.getSelectedAttributeMap());
				initChartFilters(doc.filter, paramsMap);
				doc.setChartParams(paramsMap);
				doc.setChart(chartId);
			} else{
				result = false;
			}
		} else{
			doc.resetChart();
			final Map<Integer, ChartParams> paramsMap = fillChartParams(doc.DB.schema, chartId);
			initChartFilters(doc.filter, paramsMap);
			doc.setChartParams(paramsMap);
			doc.setChart(chartId);
		}
		return result;
	}

	public Map<Integer, ChartParams> fillChartParams(Schema schema, int chartId){
		final Map<Integer, ChartParams> paramsMap = new HashMap<Integer, ChartParams>();
		final ChartsGateway gateway = new HttpChartsGatewayImpl();
		final Chart chart = gateway.getChartWithParameters(chartId);

		for (ObjectChart objectChart : chart.getObjectCharts()){
			final Entity entity = schema.getEntity(objectChart.getObjectId());
			if (entity == null || !entity.CanRead || objectChart.getChartAttributes().isEmpty()){
				continue;
			}

			final ChartParams chartParams = new ChartParams(chartId, true, objectChart.getMinValue(), objectChart
			        .getMaxValue(), objectChart.getMinScale(), objectChart.getMaxScale());
			chartParams.setTitle(chart.getName() + " - " + entity.Name);
			chartParams.setChartType(objectChart.getChartType());
			chartParams.setDisplayOperation(objectChart.getDisplayOperation());
			chartParams.setChartSumColor(Utility.createColor(objectChart.getFontColor()));
			if (chartParams.getChartSumColor() == null){
				chartParams.setChartSumColor(Color.BLACK);
			}
			chartParams.setShowSummary(objectChart.isValueDisplayed());
			chartParams.setShowLabelOnLegend(objectChart.isLabelInUse());
			if (objectChart.getLabelFont() != null && !objectChart.getLabelFont().isEmpty()){
				chartParams.setSummaryFont(Utility.createFont(objectChart.getLabelFont()));
			} else{
				chartParams.setSummaryFont(Utility.createFont("Dialog,1,13"));
			}
			NumberFormat format;
			if (objectChart.getNumberFormat() != null && !objectChart.getNumberFormat().isEmpty()){
				format = new DecimalFormat(objectChart.getNumberFormat());
			} else{
				format = new DecimalFormat("#");
			}
			chartParams.setSummaryFormat(format);
			for (ChartAttribute chartAttribute : objectChart.getChartAttributes()){
				final Attribute attribute = entity.getAttribute(chartAttribute.getAttributeId());
				if (attribute != null){
					final Color color = Utility.createColor(chartAttribute.getRgb());
					ChartAttributeDescriptor ca = new ChartAttributeDescriptor(attribute, color, chartParams
					        .getChartAttributes().size());
					chartParams.getChartAttributes().add(ca);
				}
			}
			setChartMinMaxValues(chartParams);

			paramsMap.put(entity.ID, chartParams);
		}
		return paramsMap;
	}

	public void setChartMinMaxValues(final ChartParams chartParams){
		if (chartParams != null){
			final ChartsGateway gateway = new HttpChartsGatewayImpl();
			double[] minMaxValues = gateway.getCurrentChartMinMaxValues(chartParams.getChartAttributes());
			chartParams.setChartMinVal(minMaxValues[0]);
			chartParams.setChartMaxVal(minMaxValues[1]);
			chartParams.setChartSliceMaxVal(minMaxValues[2]);
		}
	}

	public Map<Integer, ChartParams> fillDynamicChartParams(Schema schema, Map<Integer, List<DynamicChartAttribute>> attrMap){
		final Map<Integer, ChartParams> paramsMap = new HashMap<Integer, ChartParams>();
		if (attrMap == null || attrMap.isEmpty()){
			return paramsMap;
		}
		final double minScale = 20, maxScale = 200;

		for (Integer entityId : attrMap.keySet()){
			final List<DynamicChartAttribute> attributes = attrMap.get(entityId);
			final Entity entity = schema.getEntity(entityId);
			if (entity == null || !entity.CanRead || attributes.isEmpty()){
				continue;
			}

			final ChartParams chartParams = new ChartParams(DynamicChart.DYNAMIC_CHART_ID, false, Double.NEGATIVE_INFINITY,
			        Double.POSITIVE_INFINITY, minScale, maxScale);
			chartParams.setTitle(entity.Name);
			chartParams.setChartType(ChartType.Pie);
			chartParams.setDisplayOperation(DisplayOperation.Sum);
			chartParams.setChartSumColor(Color.BLACK);
			chartParams.setShowSummary(false);
			chartParams.setShowLabelOnLegend(true);
			chartParams.setSummaryFont(Utility.createFont("Dialog,1,13"));
			chartParams.setSummaryFormat(new DecimalFormat("#"));
			for (DynamicChartAttribute chartAttribute : attributes){
				ChartAttributeDescriptor ca = new ChartAttributeDescriptor(chartAttribute, chartParams.getChartAttributes()
				        .size());
				chartParams.getChartAttributes().add(ca);
			}

			setChartMinMaxValues(chartParams);

			paramsMap.put(entity.ID, chartParams);
		}
		return paramsMap;
	}

	private void initChartFilters(DataFilter filter, final Map<Integer, ChartParams> paramsMap){
		for (Integer entityId : paramsMap.keySet()){
			filter.initChartFilter(entityId);
		}
	}

	private boolean doSNABasic(){
		doc.resetChart();

		doc.filter.initChartFilter(SNA.SNA_CHART_ID);

		if (sna == null){
			sna = new SNA(doc);
		}
		sna.doIt();

		doc.setChart(SNA.SNA_CHART_ID);

		return true;
	}

	public String toXML(Map<Integer, ChartParams> paramsMap){
		final StringBuilder xml = new StringBuilder();
		if (paramsMap != null && !paramsMap.isEmpty()){
			xml.append("<DynamicChart>");
			for (final Integer entityId : paramsMap.keySet()){
				final ChartParams chartParams = paramsMap.get(entityId);
				for (ChartAttributeDescriptor attr : chartParams.getChartAttributes()){
					xml.append("<Entity EntityID='").append(entityId).append("'");
					xml.append(" AttributeID='").append(attr.getAttribute().ID).append("'");

					String rgb = Integer.toHexString(attr.getColor().getRGB());
					rgb = "#" + rgb.substring(2, rgb.length());
					xml.append("Color='").append(rgb).append("' />");
				}
			}
			xml.append("</DynamicChart>");
		}
		return xml.toString();
	}

	public Map<Integer, List<DynamicChartAttribute>> fromXML(NanoXML xml, Schema schema){
		final Map<Integer, List<DynamicChartAttribute>> attributeMap = new HashMap<Integer, List<DynamicChartAttribute>>();
		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			NanoXMLAttribute attr;
			Entity entity = null;
			Attribute attribute = null;
			String colorStr = null;
			while ((attr = nextX.Tag.getNextAttribute()) != null){
				if ("EntityID".equals(attr.Name)){
					entity = schema.getEntity(attr.getIntegerValue());
				} else if ("AttributeID".equals(attr.Name) && entity != null){
					attribute = entity.getAttribute(attr.getIntegerValue());
				} else if ("Color".equals(attr.Name)){
					colorStr = attr.getValue();
				}
			}
			if (attribute != null){
				if (!attributeMap.containsKey(entity.ID)){
					attributeMap.put(entity.ID, new ArrayList<DynamicChartAttribute>());
				}
				final List<DynamicChartAttribute> list = attributeMap.get(entity.ID);
				final DynamicChartAttribute cAttr = new DynamicChartAttribute(attribute);
				cAttr.setColor(Utility.createColor(colorStr));
				list.add(cAttr);
			}
		}

		return attributeMap;
	}
}
