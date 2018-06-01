package com.ni3.ag.navigator.shared.domain;

import java.util.List;

public class ObjectChart{
	private int id;
	private int objectId;
	private int chartId;
	private int minValue;
	private int maxValue;
	private double minScale;
	private double maxScale;
	private boolean labelInUse;
	private String labelFont;
	private String numberFormat;
	private DisplayOperation displayOperation;
	private ChartType chartType;
	private boolean isValueDisplayed;
	private String fontColor;

	private List<ChartAttribute> chartAttributes;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getObjectId(){
		return objectId;
	}

	public void setObjectId(int objectId){
		this.objectId = objectId;
	}

	public int getChartId(){
		return chartId;
	}

	public void setChartId(int chartId){
		this.chartId = chartId;
	}

	public int getMinValue(){
		return minValue;
	}

	public void setMinValue(int minValue){
		this.minValue = minValue;
	}

	public int getMaxValue(){
		return maxValue;
	}

	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}

	public double getMinScale(){
		return minScale;
	}

	public void setMinScale(double minScale){
		this.minScale = minScale;
	}

	public double getMaxScale(){
		return maxScale;
	}

	public void setMaxScale(double maxScale){
		this.maxScale = maxScale;
	}

	public boolean isLabelInUse(){
		return labelInUse;
	}

	public void setLabelInUse(boolean labelInUse){
		this.labelInUse = labelInUse;
	}

	public String getLabelFont(){
		return labelFont;
	}

	public void setLabelFont(String labelFont){
		this.labelFont = labelFont;
	}

	public String getNumberFormat(){
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat){
		this.numberFormat = numberFormat;
	}

	public DisplayOperation getDisplayOperation(){
		return displayOperation;
	}

	public void setDisplayOperation(DisplayOperation displayOperation){
		this.displayOperation = displayOperation;
	}

	public ChartType getChartType(){
		return chartType;
	}

	public void setChartType(ChartType chartType){
		this.chartType = chartType;
	}

	public boolean isValueDisplayed(){
		return isValueDisplayed;
	}

	public void setValueDisplayed(boolean valueDisplayed){
		isValueDisplayed = valueDisplayed;
	}

	public String getFontColor(){
		return fontColor;
	}

	public void setFontColor(String fontColor){
		this.fontColor = fontColor;
	}

	public List<ChartAttribute> getChartAttributes(){
		return chartAttributes;
	}

	public void setChartAttributes(List<ChartAttribute> chartAttributes){
		this.chartAttributes = chartAttributes;
	}

}
