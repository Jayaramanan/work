/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.ni3.ag.adminconsole.client.view.charts.preview.AbstractChart;
import com.ni3.ag.adminconsole.client.view.charts.preview.BarChart;
import com.ni3.ag.adminconsole.client.view.charts.preview.PieChart;
import com.ni3.ag.adminconsole.client.view.charts.preview.StackChart;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ObjectChart;

public class ChartPreviewPanel extends JPanel{
	private static final long serialVersionUID = 8378970941997246272L;

	private ObjectChart chart;
	private AbstractChart chartPreview;

	public ChartPreviewPanel(){
		setBorder(BorderFactory.createEtchedBorder());
	}

	public void updateView(ObjectChart oc){
		if (oc == null){
			chart = null;
			chartPreview = null;
		} else{
			chart = oc;
			chartPreview = getChartPreview(oc.getChartType());
		}
		repaint();
	}

	private AbstractChart getChartPreview(ChartType type){
		if (type == null)
			return new PieChart();
		switch (type){
			case PIE:
				return new PieChart();
			case STACKED:
				return new StackChart();
			case BAR:
				return new BarChart();
			default:
				return new PieChart();
		}
	}

	@Override
	public void paint(Graphics g){
		super.paint(g);
		if (chartPreview != null)
			chartPreview.draw((Graphics2D) g, chart);
	}

}
