/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Font;

public class PieChart extends AbstractChart{

	@Override
	protected void paint(Graphics2D g2, Point p, double scale, Font font, Color fontColor){
		int x = p.x;
		int y = p.y;
		int Angle;
		int Start = 0;
		double Cumulative = 0.0;

		int ovalX = (int) (x - magicChartR / 2.0 * scale);
		int ovalY = (int) (y - magicChartR / 2.0 * scale);
		int ovalWidth = (int) (magicChartR * scale);
		int ovalHeight = (int) (magicChartR * scale);

		if (ChartTotal == 0){
			g2.setColor(Color.black);
			BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			g2.drawOval(ovalX, ovalY, ovalWidth, ovalHeight);
		} else{
			for (int n = 0; n < cChart; n++){
				if (ChartVal[n] != 0.0){
					Cumulative += ChartVal[n];

					if (Cumulative >= ChartTotal){
						Angle = 360 - Start;
					} else{
						Angle = (int) ((360.0 / ChartTotal) * ChartVal[n]);
					}

					g2.setColor(ChartColor[n]);
					g2.fillArc(ovalX, ovalY, ovalWidth, ovalHeight, Start, Angle);
					Start += Angle;
				}
			}
		}

		super.drawValue(g2, x, y, CHART_VALUE_STRING, font, fontColor);

	}

	@Override
	protected Point getPointForMin(){
		return new Point(40, 40);
	}

	@Override
	protected Point getPointForMax(){
		return new Point(200, 100);
	}

}
