/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Font;

public class BarChart extends AbstractChart{

	@Override
	protected void paint(Graphics2D g2, Point p, double scale, Font font, Color fontColor){
		int x = p.x, y = p.y;
		double max = Double.MIN_VALUE;

		int barX = (int) (x - magicChartR / 2.0 * scale);
		int barY = (int) (y - magicChartR / 2.0 * scale);
		int barWidth = (int) (magicChartR * scale);
		int barHeight = (int) (magicChartR * scale);

		if (ChartTotal == 0){
			g2.setColor(Color.black);
			BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			g2.drawRect(barX, barY, barWidth, barHeight);
		} else{
			for (int n = 0; n < cChart; n++)
				max = Math.max(max, ChartVal[n]);

			int ht, yy;
			int xb, dx;

			xb = 0;
			dx = (int) ((magicChartR / cChart) * scale);

			for (int n = 0; n < cChart; n++){
				if (ChartVal[n] != 0.0){
					g2.setColor(ChartColor[n]);

					ht = (int) (magicChartR * (ChartVal[n] / max) * scale);
					yy = y + barHeight - ht;
					g2.fillRect(barX + xb, yy, dx, ht);

				}

				xb += dx;
			}
		}
		super.drawValue(g2, x, y + (barHeight >> 1), CHART_VALUE_STRING, font, fontColor);
	}

	@Override
	protected Point getPointForMin(){
		return new Point(30, 30);
	}

	@Override
	protected Point getPointForMax(){
		return new Point(200, 40);
	}

}
