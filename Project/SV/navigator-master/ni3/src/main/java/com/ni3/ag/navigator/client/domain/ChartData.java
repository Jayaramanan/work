package com.ni3.ag.navigator.client.domain;

import java.util.List;

public class ChartData{
	private int nodeId;
	private double radius;
	private String bkColor;
	private boolean showSummary;
	private boolean showLabel;
	private String font;
	private String sumFormat;
	private List<ChartValue> chartValues;

	public void setNodeId(int nodeId){
		this.nodeId = nodeId;
	}

	public void setRadius(double radius){
		this.radius = radius;
	}

	public void setBkColor(String bkColor){
		this.bkColor = bkColor;
	}

	public void setShowSummary(boolean showSummary){
		this.showSummary = showSummary;
	}

	public void setShowLabel(boolean showLabel){
		this.showLabel = showLabel;
	}

	public void setFont(String font){
		this.font = font;
	}

	public void setSumFormat(String sumFormat){
		this.sumFormat = sumFormat;
	}

	public void setChartValues(List<ChartValue> chartValues){
		this.chartValues = chartValues;
	}

	public int getNodeId(){
		return nodeId;
	}

	public double getRadius(){
		return radius;
	}

	public String getBkColor(){
		return bkColor;
	}

	public boolean isShowSummary(){
		return showSummary;
	}

	public boolean isShowLabel(){
		return showLabel;
	}

	public String getFont(){
		return font;
	}

	public String getSumFormat(){
		return sumFormat;
	}

	public List<ChartValue> getChartValues(){
		return chartValues;
	}
}
