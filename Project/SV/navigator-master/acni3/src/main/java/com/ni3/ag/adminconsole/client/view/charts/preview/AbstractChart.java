/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts.preview;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;

public abstract class AbstractChart{

	protected final static String CHART_VALUE_STRING = "123";

	protected static Color chartBackgroungColor = Color.WHITE;
	private static Color defaultColor = Color.BLACK;
	protected static double magicChartR = 1;
	protected double ChartTotal;
	protected int cChart;
	protected double[] ChartVal;
	protected Color[] ChartColor;
	protected double maxScale;
	protected double minScale;

	protected void setValues(ObjectChart ch){
		if (ch.getChartAttributes() == null || ch.getChartAttributes().isEmpty()){
			ChartTotal = 0;
			cChart = 0;
			ChartVal = new double[] {};
			ChartColor = new Color[] {};
			maxScale = 0;
			minScale = 0;
		} else{
			List<ChartAttribute> chartAttrs = ch.getChartAttributes();
			ChartTotal = chartAttrs.size();
			cChart = chartAttrs.size();
			ChartVal = new double[chartAttrs.size()];
			for (int i = 0; i < ChartVal.length; i++){
				ChartVal[i] = 1;
			}
			ChartColor = new Color[cChart];
			for (int i = 0; i < ChartColor.length; i++)
				ChartColor[i] = rgbToColor(ch.getChartAttributes().get(i).getRgb());
			maxScale = (ch.getMaxScale() != null) ? ch.getMaxScale().doubleValue() : 0;
			minScale = (ch.getMinScale() != null) ? ch.getMinScale().doubleValue() : 0;
		}
	}

	public void draw(Graphics2D gr, ObjectChart ch){
		setValues(ch);
		Font labelFont = ch.getFont();
		String colorProp = ch.getFontColor();
		Boolean isValueDisplayed = ch.getIsValueDisplayed();
		Color c = null;
		if (colorProp != null && Boolean.TRUE.equals(isValueDisplayed)){
			colorProp = colorProp.replaceAll("#", "0x");
			if (colorProp == null || colorProp.isEmpty())
				c = Color.BLACK;
			else
				c = Color.decode(colorProp);
		}
		if (minScale > 0)
			paint(gr, getPointForMin(), minScale, labelFont, c);
		if (maxScale > 0)
			paint(gr, getPointForMax(), maxScale, labelFont, c);
	}

	protected abstract Point getPointForMin();

	protected abstract Point getPointForMax();

	protected abstract void paint(Graphics2D g2, Point p, double scale, Font font, Color fontColor);

	protected void drawValue(Graphics g, int x, int y, String value, Font font, Color color){
		if (color != null){
			FontMetrics fm = g.getFontMetrics(font);
			int strWidth = fm.charsWidth(value.toCharArray(), 0, value.length());
			int strHeight = fm.getAscent() - fm.getLeading() - fm.getDescent();
			g.setColor(color);
			g.setFont(font);
			g.drawString(value, x - (strWidth >> 1), y + (strHeight >> 1));
		}
	}

	protected Color rgbToColor(String rgb){
		if (rgb == null)
			return defaultColor;
		try{
			return java.awt.Color.decode(rgb);
		} catch (NumberFormatException e){
			return defaultColor;
		}
	}
}
