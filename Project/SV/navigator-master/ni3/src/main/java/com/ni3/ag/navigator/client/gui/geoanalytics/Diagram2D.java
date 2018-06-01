package com.ni3.ag.navigator.client.gui.geoanalytics;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import javax.swing.*;

public class Diagram2D extends JPanel{
	private static final long serialVersionUID = 8933986810311047792L;
	private final Color lineColor = Color.BLUE;
	private final Color pointColor = Color.RED;
	private final int PAD = 3;

	private List<Double> data;

	public Diagram2D(){
		data = new ArrayList<Double>();
	}

	public void setData(List<Double> data){
		this.data = data;
	}

	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		// Draw ordinate.
		g2.draw(new Line2D.Double(PAD, PAD, PAD, h - PAD));
		// Draw abscissa.
		g2.draw(new Line2D.Double(PAD, h - PAD, w - PAD, h - PAD));

		// Draw diagram line
		double xInc = (double) (w - 2 * PAD) / (data.size() - 1);
		double scale = (double) (h - 2 * PAD) / getMax();
		g2.setPaint(lineColor);
		for (int i = 0; i < data.size() - 1; i++){
			double x1 = PAD + i * xInc;
			double y1 = h - PAD - scale * data.get(i);
			double x2 = PAD + (i + 1) * xInc;
			double y2 = h - PAD - scale * data.get(i + 1);
			g2.draw(new Line2D.Double(x1, y1, x2, y2));
		}
		// Mark data points.
		g2.setPaint(pointColor);
		for (int i = 0; i < data.size(); i++){
			double x = PAD + i * xInc;
			double y = h - PAD - scale * data.get(i);
			g2.fill(new Ellipse2D.Double(x - 2, y - 2, 4, 4));
		}
	}

	private double getMax(){
		double max = -Integer.MAX_VALUE;
		for (int i = 0; i < data.size(); i++){
			if (data.get(i) > max)
				max = data.get(i);
		}
		return max;
	}
}