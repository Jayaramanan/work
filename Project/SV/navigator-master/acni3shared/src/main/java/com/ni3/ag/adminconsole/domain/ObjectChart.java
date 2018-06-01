/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.awt.Font;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class ObjectChart implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;

	private final static Logger log = Logger.getLogger(ObjectChart.class);

	public static final String OBJECT = "object";

	public static final int DEFAULT_MIN_SCALE = 20;

	public static final int DEFAULT_MAX_SCALE = 100;

	private Integer id;
	private ObjectDefinition object;
	private Chart chart;
	private Integer minValue;
	private Integer maxValue;
	private BigDecimal minScale;
	private BigDecimal maxScale;
	private Integer labelInUse_;
	private String labelFontSize;
	private String numberFormat;
	private Integer isValueDisplayed_;
	private String fontColor;
	private Integer displayOperation_;
	private Integer chartType_;

	private List<ChartAttribute> chartAttributes;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getIsValueDisplayed_(){
		return isValueDisplayed_;
	}

	public void setIsValueDisplayed_(Integer isValueDisplayed_){
		this.isValueDisplayed_ = isValueDisplayed_;
	}

	public Integer getLabelInUse_(){
		return labelInUse_;
	}

	public void setLabelInUse_(Integer labelInUse_){
		this.labelInUse_ = labelInUse_;
	}

	public ObjectDefinition getObject(){
		return object;
	}

	public void setObject(ObjectDefinition object){
		this.object = object;
	}

	public Chart getChart(){
		return chart;
	}

	public void setChart(Chart chart){
		this.chart = chart;
	}

	public Integer getMinValue(){
		return minValue;
	}

	public void setMinValue(Integer minValue){
		this.minValue = minValue;
	}

	public Integer getMaxValue(){
		return maxValue;
	}

	public void setMaxValue(Integer maxValue){
		this.maxValue = maxValue;
	}

	public BigDecimal getMinScale(){
		return minScale;
	}

	public void setMinScale(BigDecimal minScale){
		this.minScale = minScale;
	}

	public BigDecimal getMaxScale(){
		return maxScale;
	}

	public void setMaxScale(BigDecimal maxScale){
		this.maxScale = maxScale;
	}

	public Boolean getLabelInUse(){
		if (labelInUse_ == null)
			return false;
		return labelInUse_.intValue() != 0;
	}

	public void setLabelInUse(Boolean labelInUse){
		labelInUse_ = labelInUse ? 1 : 0;
	}

	public String getLabelFontSize(){
		return labelFontSize;
	}

	public void setLabelFontSize(String labelFontSize){
		this.labelFontSize = labelFontSize;
	}

	public String getNumberFormat(){
		return numberFormat;
	}

	public void setNumberFormat(String numberFormat){
		this.numberFormat = numberFormat;
	}

	public void setDisplayOperation_(Integer displayOperation){
		displayOperation_ = displayOperation;
	}

	public void setChartType_(Integer chartType){
		chartType_ = chartType;
	}

	public Integer getDisplayOperation_(){
		return displayOperation_;
	}

	public Integer getChartType_(){
		return chartType_;
	}

	public ChartDisplayOperation getDisplayOperation(){
		return displayOperation_ != null ? ChartDisplayOperation.fromInt(displayOperation_) : null;
	}

	public void setDisplayOperation(ChartDisplayOperation displayOperation){
		this.displayOperation_ = displayOperation != null ? displayOperation.toInt() : null;
	}

	public ChartType getChartType(){
		return chartType_ != null ? ChartType.fromInt(chartType_) : null;
	}

	public void setChartType(ChartType chartType){
		this.chartType_ = chartType != null ? chartType.toInt() : null;
	}

	public Boolean getIsValueDisplayed(){
		if (isValueDisplayed_ == null)
			return false;
		return isValueDisplayed_.intValue() != 0;
	}

	public void setIsValueDisplayed(Boolean isValueDisplayed){
		isValueDisplayed_ = isValueDisplayed ? 1 : 0;
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

	@Override
	public boolean equals(java.lang.Object o){
		if (o == this)
			return true;
		if (o == null)
			return false;
		if (!(o instanceof ObjectChart))
			return false;

		ObjectChart oc = (ObjectChart) o;
		if (oc.getId() == null || getId() == null)
			return false;
		return getId().equals(oc.getId());
	}

	public Font getFont(){
		Font f = null;
		String prop = getLabelFontSize();
		try{
			StringTokenizer strt = new StringTokenizer(prop, ",");
			String fontName = strt.nextToken();
			String fontStyleProp = strt.nextToken();
			String fontSizeProp = strt.nextToken();
			int fontStyle = Integer.parseInt(fontStyleProp);
			int fontSize = Integer.parseInt(fontSizeProp);
			f = new Font(fontName, fontStyle, fontSize);
		} catch (NumberFormatException nfe){
			log.error("", nfe);
		} catch (NoSuchElementException nse){
			log.error("", nse);
		}
		return f;
	}

	public void setFont(Font f){
		String labelFontSize = f.getFamily() + "," + f.getStyle() + "," + f.getSize();
		this.setLabelFontSize(labelFontSize);
	}

	public ObjectChart clone() throws CloneNotSupportedException{
		return (ObjectChart) super.clone();
	}

	public ObjectChart clone(Integer id, ObjectDefinition object, Chart chart) throws CloneNotSupportedException{
		ObjectChart oc = clone();
		oc.setId(id);
		oc.setObject(object);
		oc.setChart(chart);
		return oc;
	}
}
