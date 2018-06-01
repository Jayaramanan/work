/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.domain;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.domain.DisplayOperation;

public class ChartParams{
	public static final int CHART_DEFAULT_SCALE = 70;
	private int chartID;

	private double chartMinScale, chartMaxScale;
	private double chartDBMinScale, chartDBMaxScale;
	private double chartMinVal, chartMaxVal, chartSliceMaxVal;
	private double chartDBMinVal, chartDBMaxVal;
	private boolean absolute;
	private ChartType chartType;
	private DisplayOperation displayOperation;
	private Color fontColor;
	private Color chartSumColor;
	private boolean showLabelOnLegend; // show label in legend then cursor over node
	private boolean showSummary; // Show summary label on chart
	private Font summaryFont;
	private NumberFormat summaryFormat;
	private List<ChartAttributeDescriptor> chartAttributes;
	private boolean legendVisible;
	private String title;

	private double currentGraphMinTotal, currentGraphMaxTotal;
	private double currentGraphMaxValue;

	public ChartParams(int chartID, boolean absolute, double minVal, double maxVal, double minScale, double maxScale){
		this();
		this.chartID = chartID;
		this.absolute = absolute;
		chartDBMinVal = minVal;
		chartDBMaxVal = maxVal;
		chartMinVal = minVal;
		chartMaxVal = maxVal;
		chartDBMinScale = minScale;
		chartDBMaxScale = maxScale;
		chartMinScale = minScale;
		chartMaxScale = maxScale;
	}

	public ChartParams(){
		this.chartType = ChartType.Pie;
		chartSumColor = Color.BLACK;
		showSummary = false;
		showLabelOnLegend = false;
		chartAttributes = new ArrayList<ChartAttributeDescriptor>();
	}

	public void reset(){
		absolute = true;
		chartMinScale = chartDBMinScale;
		chartMaxScale = chartDBMaxScale;
	}

	public double getCurrentGraphMinTotal(){
		return currentGraphMinTotal;
	}

	public void setCurrentGraphMinTotal(double currentGraphMinTotal){
		this.currentGraphMinTotal = currentGraphMinTotal;
	}

	public double getCurrentGraphMaxTotal(){
		return currentGraphMaxTotal;
	}

	public void setCurrentGraphMaxTotal(double currentGraphMaxTotal){
		this.currentGraphMaxTotal = currentGraphMaxTotal;
	}

	public boolean isAbsolute(){
		return absolute;
	}

	public void setAbsolute(boolean absolute){
		this.absolute = absolute;
	}

	public int getChartID(){
		return chartID;
	}

	public double getChartMinScale(){
		return chartMinScale;
	}

	public double getChartMaxScale(){
		return chartMaxScale;
	}

	public double getChartMinVal(){
		return chartMinVal;
	}

	public double getChartMaxVal(){
		return chartMaxVal;
	}

	public void setChartID(int chartID){
		this.chartID = chartID;
	}

	public void setChartMinScale(double chartMinScale){
		this.chartMinScale = chartMinScale;
	}

	public void setChartMaxScale(double chartMaxScale){
		this.chartMaxScale = chartMaxScale;
	}

	public void setChartMinVal(double chartMinVal){
		this.chartMinVal = Math.min(chartDBMaxVal, Math.max(chartDBMinVal, chartMinVal));
	}

	public void setChartMaxVal(double chartMaxVal){
		this.chartMaxVal = Math.min(chartDBMaxVal, Math.max(chartDBMinVal, chartMaxVal));
	}

	public double getChartSliceMaxVal(){
		return chartSliceMaxVal;
	}

	public void setChartSliceMaxVal(double chartSliceMaxVal){
		this.chartSliceMaxVal = chartSliceMaxVal;
	}

	public double getChartDefaultScale(){
		double scale = CHART_DEFAULT_SCALE;
		if (scale < chartMinScale){
			scale = chartMinScale;
		}
		if (scale > chartMaxScale){
			scale = chartMaxScale;
		}
		return scale;
	}

	public double getCurrentGraphMaxValueDiff(){
		return currentGraphMaxValue;
	}

	public void setCurrentGraphMaxValueDiff(double currentGraphMaxValue){
		this.currentGraphMaxValue = currentGraphMaxValue;
	}

	public ChartType getChartType(){
		return chartType;
	}

	public void setChartType(ChartType chartType){
		this.chartType = chartType;
	}

	public Color getFontColor(){
		return fontColor;
	}

	public void setFontColor(Color fontColor){
		this.fontColor = fontColor;
	}

	public Color getChartSumColor(){
		return chartSumColor;
	}

	public void setChartSumColor(Color chartSumColor){
		this.chartSumColor = chartSumColor;
	}

	public DisplayOperation getDisplayOperation(){
		return displayOperation;
	}

	public void setDisplayOperation(DisplayOperation displayOperation){
		this.displayOperation = displayOperation;
	}

	public boolean isShowSummary(){
		return showSummary;
	}

	public void setShowSummary(boolean showSummary){
		this.showSummary = showSummary;
	}

	public boolean isShowLabelOnLegend(){
		return showLabelOnLegend;
	}

	public void setShowLabelOnLegend(boolean showLabelOnLegend){
		this.showLabelOnLegend = showLabelOnLegend;
	}

	public Font getSummaryFont(){
		return summaryFont;
	}

	public void setSummaryFont(Font summaryFont){
		this.summaryFont = summaryFont;
	}

	public NumberFormat getSummaryFormat(){
		return summaryFormat;
	}

	public void setSummaryFormat(NumberFormat summaryFormat){
		this.summaryFormat = summaryFormat;
	}

	public List<ChartAttributeDescriptor> getChartAttributes(){
		return chartAttributes;
	}

	public void setChartAttributes(List<ChartAttributeDescriptor> chartAttributes){
		this.chartAttributes = chartAttributes;
	}

	public boolean isLegendVisible(){
		return legendVisible;
	}

	public void setLegendVisible(boolean legendVisible){
		this.legendVisible = legendVisible;
	}

	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public boolean hasDynamicAttributes(){
		boolean result = false;
		if (chartAttributes != null){
			for (ChartAttributeDescriptor ca : chartAttributes){
				if (ca.getAttribute() != null && ca.getAttribute().isDynamic()){
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
