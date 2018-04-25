/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts.preview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Font;

public class StackChart extends AbstractChart{

	@Override
	protected void paint(Graphics2D g2, Point p, double Scale, Font font, Color color){
		int x = p.x;
		int y = p.y;

		int stackValueX;
		int stackY = (int) (y - magicChartR / 2.0 * Scale);
		int stackWidth = (int) (10 * Scale);
		int stackHeight = (int) (magicChartR * Scale);

		if (ChartTotal == 0){
			stackValueX = (int) ((x - 5) * Scale);
			g2.setColor(Color.black);
			BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			g2.drawRect(stackValueX, stackY, stackWidth, stackHeight);
		} else{
			stackValueX = (int) (x - 5 * Scale);
			int stackX = stackValueX < 0 ? 0 : stackValueX;
			double Total;
			double dy;
			int ys;

			Total = 0.0;

			for (int n = 0; n < cChart; n++)
				Total += ChartVal[n];

			ys = stackY;
			dy = (magicChartR / Total) * Scale;

			for (int n = 0; n < cChart; n++){
				if (ChartVal[n] != 0.0){
					g2.setColor(ChartColor[n]);

					int height = (int) (dy * ChartVal[n]);
					g2.fillRect(stackX, ys, stackWidth, height);

				}

				ys += (int) (dy * ChartVal[n]);
			}
		}
		super.drawValue(g2, stackValueX + (stackWidth >> 1), y, CHART_VALUE_STRING, font, color);
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
