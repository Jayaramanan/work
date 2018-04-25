package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.ChartAttributeDescriptor;
import com.ni3.ag.navigator.client.gateway.ChartsGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.*;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpChartsGatewayImpl extends AbstractGatewayImpl implements ChartsGateway{
	@Override
	public List<Chart> getChartsForUser(int schemaId){
		NRequest.Charts request = NRequest.Charts.newBuilder().setAction(NRequest.Charts.Action.GET_CHARTS).setSchemaId(
		        schemaId).build();
		try{
			ByteString payload = sendRequest(ServletName.ChartsServlet, request);
			NResponse.Charts protoCharts = NResponse.Charts.parseFrom(payload);
			List<NResponse.Chart> protoChartList = protoCharts.getChartsList();
			List<Chart> charts = new ArrayList<Chart>();
			for (NResponse.Chart protoChart : protoChartList){
				Chart chart = new Chart();
				chart.setId(protoChart.getId());
				chart.setName(protoChart.getName());
				chart.setComment(protoChart.getComment());
				chart.setSchemaId(protoChart.getSchemaId());
				charts.add(chart);
			}
			return charts;
		} catch (IOException e){
			showErrorAndThrow("Error get charts", e);
			return null;
		}
	}

	@Override
	public Chart getChartWithParameters(int chartId){
		NRequest.Charts request = NRequest.Charts.newBuilder().setAction(NRequest.Charts.Action.GET_CHART_WITH_PARAMETERS)
		        .setChartId(chartId).build();
		try{
			ByteString payload = sendRequest(ServletName.ChartsServlet, request);
			NResponse.Chart protoChart = NResponse.Chart.parseFrom(payload);
			Chart chart = new Chart();
			chart.setId(chartId);
			chart.setName(protoChart.getName());
			chart.setObjectCharts(new ArrayList<ObjectChart>());
			for (NResponse.ObjectChart protoOC : protoChart.getObjectChartsList()){
				ObjectChart oc = new ObjectChart();
				oc.setId(protoOC.getId());
				oc.setObjectId(protoOC.getObjectId());
				oc.setChartId(protoOC.getChartId());
				oc.setMinValue(protoOC.getMinValue());
				oc.setMaxValue(protoOC.getMaxValue());
				oc.setMinScale(protoOC.getMinScale());
				oc.setMaxScale(protoOC.getMaxScale());
				oc.setLabelInUse(protoOC.getLabelInUse());
				oc.setLabelFont(protoOC.getLabelFont());
				oc.setNumberFormat(protoOC.getNumberFormat());
				oc.setDisplayOperation(DisplayOperation.fromInt(protoOC.getDisplayOperation()));
				oc.setChartType(ChartType.fromInt(protoOC.getChartType()));
				oc.setValueDisplayed(protoOC.getIsValueDisplayed());
				oc.setFontColor(protoOC.getFontColor());
				oc.setChartAttributes(new ArrayList<ChartAttribute>());
				chart.getObjectCharts().add(oc);
				for (NResponse.ChartAttribute protoCA : protoOC.getChartAttributesList()){
					ChartAttribute ca = new ChartAttribute();
					ca.setId(protoCA.getId());
					ca.setObjectChartId(protoCA.getObjectChartId());
					ca.setAttributeId(protoCA.getAttributeId());
					ca.setRgb(protoCA.getRgb());
					oc.getChartAttributes().add(ca);
				}
			}
			return chart;
		} catch (IOException ex){
			showErrorAndThrow("Error get object chart " + chartId, ex);
			return null;
		}
	}

	@Override
	public double[] getCurrentChartMinMaxValues(List<ChartAttributeDescriptor> chartAttributes){
		NRequest.Charts.Builder request = NRequest.Charts.newBuilder();
		request.setAction(NRequest.Charts.Action.GET_CHART_LIMITS);
		int entityId = 0;
		for (ChartAttributeDescriptor ca : chartAttributes){
			final Attribute attribute = ca.getAttribute();
			if (attribute != null && attribute.ID > 0){
				request.addAttributeIds(attribute.ID);
				entityId = attribute.ent.ID;
			}
		}
		request.setEntity(entityId);
		try{
			ByteString payload = sendRequest(ServletName.ChartsServlet, request.build());
			NResponse.Charts protoCharts = NResponse.Charts.parseFrom(payload);
			double min = protoCharts.getMinVal();
			double max = protoCharts.getMaxVal();
			double sliceMax = protoCharts.getMaxSliceVal();
			return new double[] { min, max, sliceMax };
		} catch (IOException e){
			showErrorAndThrow("Error get current chart limits", e);
			return null;
		}
	}
}
