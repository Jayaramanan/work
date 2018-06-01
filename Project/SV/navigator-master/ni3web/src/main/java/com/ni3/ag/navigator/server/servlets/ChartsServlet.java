package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.services.ChartService;
import com.ni3.ag.navigator.shared.domain.Chart;
import com.ni3.ag.navigator.shared.domain.ChartAttribute;
import com.ni3.ag.navigator.shared.domain.ObjectChart;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.Charts;
import com.ni3.ag.navigator.shared.proto.NRequest.Charts.Action;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class ChartsServlet extends Ni3Servlet{
	private static final long serialVersionUID = 2552096274815632443L;
	private NRequest.Charts request;

	@Override
	protected void doInternalPost(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException,
			IOException{
		InputStream is = getInputStream(httpRequest);
		request = NRequest.Charts.parseFrom(is);
		NResponse.Envelope.Builder resultBuilder = NResponse.Envelope.newBuilder();
		switch (request.getAction()){
			case GET_CHARTS:
				handleGetCharts(request, resultBuilder);
				break;
			case GET_CHART_WITH_PARAMETERS:
				handleGetChartWithParameters(request, resultBuilder);
				break;
			case GET_CHART_LIMITS:
				handleGetCurrentChartLimits(request, resultBuilder);
				break;
		}
		resultBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		sendResponse(httpRequest, response, resultBuilder);
	}

	private void handleGetCurrentChartLimits(Charts protoRequest, Builder resultBuilder){
		List<Integer> attributeIds = protoRequest.getAttributeIdsList();
		ChartService chartService = NSpringFactory.getInstance().getChartService();
		final double[] minMaxValues = chartService.getCurrentChartMinMaxValues(attributeIds, protoRequest.getEntity());
		NResponse.Charts.Builder chartsBuilder = NResponse.Charts.newBuilder();
		if (minMaxValues.length > 0){
			chartsBuilder.setMinVal(minMaxValues[0]);
			chartsBuilder.setMaxVal(minMaxValues[1]);
			chartsBuilder.setMaxSliceVal(minMaxValues[2]);
		}

		resultBuilder.setPayload(chartsBuilder.build().toByteString());
	}

	private void handleGetChartWithParameters(Charts protoRequest, Builder resultBuilder){
		ChartService chartService = NSpringFactory.getInstance().getChartService();
		Chart chart = chartService.getChartWithParameters(protoRequest.getChartId());
		NResponse.Chart.Builder protoChart = NResponse.Chart.newBuilder();
		protoChart.setId(chart.getId());
		protoChart.setName(chart.getName());
		protoChart.setSchemaId(chart.getSchemaId());

		for (ObjectChart oc : chart.getObjectCharts()){
			NResponse.ObjectChart.Builder protoObjectChart = NResponse.ObjectChart.newBuilder();
			protoObjectChart.setId(oc.getId());
			protoObjectChart.setObjectId(oc.getObjectId());
			protoObjectChart.setChartId(oc.getChartId());
			protoObjectChart.setMinValue(oc.getMinValue());
			protoObjectChart.setMaxValue(oc.getMaxValue());
			protoObjectChart.setMinScale(oc.getMinScale());
			protoObjectChart.setMaxScale(oc.getMaxScale());
			protoObjectChart.setLabelInUse(oc.isLabelInUse());
			protoObjectChart.setChartType(oc.getChartType().toInt());
			protoObjectChart.setIsValueDisplayed(oc.isValueDisplayed());
			if (oc.getLabelFont() != null)
				protoObjectChart.setLabelFont(oc.getLabelFont());
			if (oc.getNumberFormat() != null)
				protoObjectChart.setNumberFormat(oc.getNumberFormat());
			if (oc.getDisplayOperation() != null)
				protoObjectChart.setDisplayOperation(oc.getDisplayOperation().toInt());
			if (oc.getFontColor() != null)
				protoObjectChart.setFontColor(oc.getFontColor());

			for (ChartAttribute ca : oc.getChartAttributes()){
				NResponse.ChartAttribute.Builder protoAttribute = NResponse.ChartAttribute.newBuilder();
				protoAttribute.setId(ca.getId());
				protoAttribute.setObjectChartId(ca.getObjectChartId());
				protoAttribute.setAttributeId(ca.getAttributeId());
				protoAttribute.setRgb(ca.getRgb());
				protoObjectChart.addChartAttributes(protoAttribute);
			}
			protoChart.addObjectCharts(protoObjectChart);
		}

		resultBuilder.setPayload(protoChart.build().toByteString());
	}

	private void handleGetCharts(NRequest.Charts protoRequest, NResponse.Envelope.Builder resultBuilder){
		ChartService chartService = NSpringFactory.getInstance().getChartService();
		List<Chart> charts = chartService.getChartForUser(protoRequest.getSchemaId());
		NResponse.Charts.Builder chartsBuilder = NResponse.Charts.newBuilder();
		for (Chart chart : charts){
			NResponse.Chart.Builder chartBuilder = NResponse.Chart.newBuilder().setId(chart.getId())
					.setName(chart.getName());
			if (chart.getComment() != null)
				chartBuilder.setComment(chart.getComment());
			chartBuilder.setSchemaId(chart.getSchemaId());
			chartsBuilder.addCharts(chartBuilder);
		}
		resultBuilder.setPayload(chartsBuilder.build().toByteString());
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		if (request.getAction() == Action.GET_CHART_WITH_PARAMETERS){
			activity = UserActivityType.InvokeChart;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		if (request.getAction() == Action.GET_CHART_WITH_PARAMETERS){
			params.add(new LogParam(ID_LOG_PARAM, request.getChartId()));
		}
		return params;
	}
}
