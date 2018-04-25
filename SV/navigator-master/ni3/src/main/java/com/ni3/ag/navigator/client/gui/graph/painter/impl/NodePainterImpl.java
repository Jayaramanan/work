/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import com.ni3.ag.navigator.client.domain.ChartFilter;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.ConvexHull;
import com.ni3.ag.navigator.client.gui.graph.painter.NodePainter;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.domain.ChartType;

public class NodePainterImpl extends ObjectPainter implements NodePainter{
	protected final Stroke polyStroke; // Line style for Show node polygon/polyline operation
	private final Color colorSelectedFrom = Color.BLUE;
	private final Color colorSelectedTo = Color.RED;
	private final Color colorSelected;

	public NodePainterImpl(){
		this(new Color(0, 0, 255));
	}

	public NodePainterImpl(final Color colorSelected){
		this.colorSelected = colorSelected;
		polyStroke = new BasicStroke(5.0f);
	}

	@Override
	public boolean paint(final Node n, final Graphics2D g, final double zoomf, final boolean picked,
						 final boolean showLabel, final boolean showExpandCounter, final ChartFilter cFilter, final ChartParams cParams){

		final FontMetrics fm = g.getFontMetrics();
		Node matrixPointedNode = SystemGlobals.MainFrame.Doc.getMatrixPointedNode();

		if (!n.isActive()){
			return false;
		}
		final Image metaphorIcon = n.Obj.getIcon();
		n.setMetaphorWidth(metaphorIcon.getWidth(null));
		n.setMetaphorHeight(metaphorIcon.getHeight(null));
		n.setChartNegativePartHeight(0);

		if (n.getScaledMetaphorWidth(false) < 2 || n.equals(matrixPointedNode)){
			if (n.equals(matrixPointedNode)){
				n.scaleFactor /= 1.5;
				if (n.scaleFactor <= Node.MinMetaphorBlink){
					n.scaleFactor = Node.MaxMetaphorBlink;
				}
			}
			// TODO remove all calculations from painter - this is not right place for this logic
			n.setMetaphorRadius(Math.sqrt(n.getMetaphorWidth() * n.getMetaphorWidth() + n.getMetaphorHeight()
					* n.getMetaphorHeight()));
		}

		Color[] halos = n.getHalos();
		if (!n.hasChart()){
			if (halos.length > 0){
				Double typeRadius = Node.maxGraphHaloRadiusPerType.get(n.Type);
				if (typeRadius == null){
					typeRadius = Node.maxGraphHaloR;
				}
				n.setMetaphorRadius(drawHalo(g, n.getX(), n.getY(), typeRadius, 4.0, halos));
			}
		}

		if (n.selected || n.selectedFrom || n.selectedTo)
			paintSelection(g, n, cFilter, cParams);

		if (!n.hasChart()){
			n.setMetaphorWidth((int) (n.getMetaphorWidth() * Node.nodeScaleGraph));
			n.setMetaphorHeight((int) (n.getMetaphorHeight() * Node.nodeScaleGraph));
			if (halos.length == 0){
				n.setMetaphorRadius(Math.sqrt(n.getMetaphorWidth() * n.getMetaphorWidth() + n.getMetaphorHeight()
						* n.getMetaphorHeight()));
			}

			g.drawImage(metaphorIcon, (int) (n.getX() - n.getScaledMetaphorWidth(false) / 2.0), (int) (n.getY() - n
					.getScaledMetaphorHeight(false) / 2.0), n.getScaledMetaphorWidth(false), n
					.getScaledMetaphorHeight(false), null);
			if (Node.markFocusNodes && n.isLeading()){
				int aW, aH;
				aW = (int) (Node.anchorW * n.scaleFactor);
				aH = (int) (Node.anchorH * n.scaleFactor);
				g.drawImage(Node.anchor, (int) (n.getX() + n.getScaledMetaphorWidth(false) / 2.0 - aW), (int) (n.getY()
						+ n.getScaledMetaphorHeight(false) / 2.0 - aH), aW, aH, null);
			}
		} else{
			calculateChartParams(n, cParams);
			final int r = (int) n.getChartR();
			int width = r;
			int height = r;
			if (ChartType.Bar.equals(cParams.getChartType())){
				int w = getBarChartWidth(n, cFilter);
				if (w > 0){
					width = w;
					height = getBarChartHeight(n, cFilter, cParams);
					final int addHeight = Math.abs(getBarChartNegativePartHeight(n, cFilter, cParams));
					n.setChartNegativePartHeight(addHeight);
				}
			}
			n.setMetaphorWidth(width);
			n.setMetaphorHeight(height);
			n.setMetaphorRadius(r / 1.5);

			paintChart(n, g, (int) n.getX(), (int) n.getY(), n.scaleFactor, false, cFilter, cParams);
		}

		g.setColor(Color.black);

		if (showExpandCounter)
			paintEdgeCounter(n, g, cFilter, cParams);

		if (showLabel)
			paintNodeLabel(n, g, fm, cParams, n.getX(), n.getY());

		return true;

	}

	private void paintEdgeCounter(Node n, Graphics2D g, ChartFilter cFilter, ChartParams cParams){
		int x, y;
		if (n.hasChart()){
			if (cParams.getChartType() == ChartType.Bar){
				int yOffset = 0;
				int xOffset = (int) (getBarChartWidth(n, cFilter) * n.scaleFactor);
				if (xOffset == 0){
					xOffset = (int) (n.getChartR() * n.scaleFactor / 2) + 8;
					yOffset = xOffset;
				} else{
					yOffset = (int) (getBarChartHeight(n, cFilter, cParams) * n.scaleFactor);
				}
				y = (int) (n.getY() - yOffset);
				x = (int) (n.getX() + xOffset);
			} else{
				x = (int) (n.getX() + (n.getChartR() * n.scaleFactor) / 2);
				y = (int) (n.getY() - (n.getChartR() * n.scaleFactor) / 2) + 5;
			}
		} else{
			x = (int) n.getX() + n.getScaledMetaphorWidth(false) - 10;
			y = (int) n.getY() - n.getScaledMetaphorHeight(false) / 2 + 4;
		}
		if (n.getExternalRelatives() != 0){
			g.drawString("+" + n.getExternalRelatives(), x, y);
		} else if (n.contractedRelativesCount != 0){
			g.drawString("+" + n.contractedRelativesCount, x, y);
		}
	}

	private int getBarChartHeight(Node n, ChartFilter cFilter, ChartParams cParams){
		double maxVal = cParams.isAbsolute() ? cParams.getChartSliceMaxVal() : cParams.getCurrentGraphMaxValueDiff();
		double nMax = 0;
		for (int c = 0; c < n.getChartCount(); c++){
			double curr = n.getChartValue(c);
			if (curr > 0 && isSliceDisplayable(c, n, cFilter)){
				if (curr > nMax){
					nMax = curr;
				}
			}
		}

		int ht = 0;
		if (maxVal != 0.0){
			ht = (int) ((cParams.getChartMaxScale() / maxVal) * nMax);
		}
		if (ht < 0)
			ht = 0;
		return ht;
	}

	private int getBarChartNegativePartHeight(Node n, ChartFilter cFilter, ChartParams cParams){
		double maxVal = cParams.isAbsolute() ? cParams.getChartSliceMaxVal() : cParams.getCurrentGraphMaxValueDiff();
		double nMin = 0;
		for (int c = 0; c < n.getChartCount(); c++){
			double curr = n.getChartValue(c);
			if (curr < 0 && isSliceDisplayable(c, n, cFilter)){
				if (curr < nMin){
					nMin = curr;
				}
			}
		}

		int ht = 0;
		if (maxVal != 0.0){
			ht = (int) ((cParams.getChartMaxScale() / maxVal) * nMin);
		}
		if (ht > 0)
			ht = 0;
		return ht;
	}

	private int getBarChartWidth(Node n, ChartFilter cFilter){
		int width = 0;
		for (int c = 0; c < n.getChartCount(); c++)
			if (isSliceDisplayable(c, n, cFilter))
				width += 20;
		return width;
	}

	private void paintSelection(Graphics2D g, Node n, ChartFilter cFilter, ChartParams cParams){
		int x, y, w, h;
		Color color;
		if (n.selectedFrom)
			color = colorSelectedFrom;
		else if (n.selectedTo)
			color = colorSelectedTo;
		else
			color = colorSelected;
		if (n.hasChart()){
			if (cParams.getChartType() == ChartType.Bar){
				y = (int) (n.getY() - (n.getChartR() * n.scaleFactor)) - 5;
				int dx = (int) (20 * n.scaleFactor);
				x = (int) n.getX() - 5;
				w = 10;
				for (int c = 0; c < n.getChartCount(); c++)
					if (isSliceDisplayable(c, n, cFilter))
						w += dx;
			} else{
				y = (int) (n.getY() - (n.getChartR() * n.scaleFactor) / 2.0) - 5;
				x = (int) (n.getX() - (n.getChartR() * n.scaleFactor) / 2.0) - 5;
				w = (int) (n.getChartR() * n.scaleFactor) + 10;
			}
			h = (int) (n.getChartR() * n.scaleFactor) + 10;
		} else{
			x = (int) (n.getX() - n.getScaledMetaphorWidth(false) / 2.0) - 5;
			y = (int) (n.getY() - n.getScaledMetaphorHeight(false) / 2.0) - 5;
			w = n.getScaledMetaphorWidth(false) + 10;
			h = n.getScaledMetaphorHeight(false) + 10;
		}

		paintSelectionSimple(g, x, y, w, h, color, n.selected);
	}

	private void paintSelectionSimple(Graphics2D g, int x, int y, int w, int h, Color color, boolean fill){
		g.setStroke(new BasicStroke(1));
		g.setColor(color);
		if (fill){
			g.fillRect(x, y, w, h);
		} else{
			g.drawRect(x, y, w, h);
		}
	}

	private void paintSummary(final Node n, final Graphics2D g2, final int x, final int y, final double scale,
							  final boolean drawInGIS, final ChartFilter cFilter, final ChartParams cParams){
		final Font oldFont = g2.getFont();

		final Font sumFont = cParams.getSummaryFont();
		if (drawInGIS){
			g2.setFont(sumFont.deriveFont((float) (sumFont.getSize() * scale * 1.5)));
		} else{
			g2.setFont(sumFont);
		}

		FontMetrics fm;
		fm = g2.getFontMetrics();

		g2.setColor(cParams.getChartSumColor());
		int xw = 0, yh = 0;

		double Cumulative, min, max;

		Cumulative = 0.0;
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;

		final NumberFormat format = cParams.getSummaryFormat();

		for (int c = 0; c < n.getChartCount(); c++){
			if (isSliceDisplayable(c, n, cFilter)){
				Cumulative += n.getChartValue(c);
				min = Math.min(min, n.getChartValue(c));
				max = Math.max(max, n.getChartValue(c));
			}
		}

		String s;
		switch (cParams.getDisplayOperation()){
			case Sum:
				s = format.format(Cumulative);
				break;

			case Avg:
				s = format.format(Cumulative / n.getChartCount());
				break;

			case Min:
				s = format.format(min);
				break;

			case Max:
				s = format.format(max);
				break;

			case Count:
				s = format.format(n.getChartCount());
				break;

			default:
				s = "";
				break;
		}

		if (fm != null){
			xw = fm.stringWidth(s);
			yh = fm.getHeight();
		}

		g2.drawString(s, (x - xw / 2), (y + yh / 2));
		g2.setFont(oldFont);
	}

	private boolean isSliceDisplayable(final int index, final Node n, final ChartFilter cFilter){
		return n.getChartValue(index) != 0.0 && !cFilter.isExcluded(index)
				&& n.getChartValue(index) >= cFilter.getMinChartAttrVal(index) - 0.0005
				&& n.getChartValue(index) <= cFilter.getMaxChartAttrVal(index) + 0.0005;
	}

	void paintNodeLabel(Node n, Graphics2D g, FontMetrics fm, final ChartParams cParams, double x, double y){
		if (n.lbl == null && n.Obj != null){
			n.lbl = n.Obj.getLabel();
		}
		if (n.lbl == null)
			return;
		if (n.hasChart() && cParams != null && cParams.getChartType() == ChartType.Bar){
			final int xw = fm.stringWidth(n.lbl);
			g.drawString(n.lbl, (int) x - (xw - 10) / 2, ((int) y + 8 / 2) + fm.getAscent()
					+ (n.ZigZag ? fm.getHeight() : 0));
		} else{

			if (n.lbl != null){
				if (getLabelWrapLength() > 0 && n.lbl.length() > getLabelWrapLength()){
					final List<String> lines = n.getSplittedLabel(getLabelWrapLength());
					for (int ln = 0; ln < lines.size(); ln++){
						final int xw = fm.stringWidth(lines.get(ln));
						final int xln = (int) x - (xw - 10) / 2;
						final int yln = ((int) y + (n.getScaledMetaphorHeight(false) + 4) / 2) + fm.getAscent() + ln
								* 10;
						g.drawString(lines.get(ln), xln, yln);
						n.labelWidth = xln;
					}
				} else{
					final int xw = fm.stringWidth(n.lbl);
					g.drawString(n.lbl, (int) x - (xw - 10) / 2,
							((int) y + (n.getScaledMetaphorHeight(false) + 4) / 2) + fm.getAscent()
									+ (n.ZigZag ? fm.getHeight() : 0));
					n.labelWidth = xw;
				}
			}
		}
	}

	protected void paintChart(final Node n, final Graphics2D g2, final int x, final int y, final double scale,
							  final boolean drawInGIS, final ChartFilter cFilter, ChartParams cParams){

		switch (cParams.getChartType()){
			case Pie:
				paintPie(n, g2, x, y, scale, cFilter, cParams);
				break;
			case Stacked:
				paintStacked(n, g2, x, y, scale, cFilter, cParams);
				break;
			case Bar:
				paintBar(n, g2, x, y, scale, cFilter, cParams);
				break;
		}

		if (cParams.isShowSummary() && n.getChartTotal() != 0.0){
			paintSummary(n, g2, x, y, scale, drawInGIS, cFilter, cParams);
		}
	}

	protected void calculateChartParams(final Node n, final ChartParams chartParams){
		if (chartParams != null){
			final double chartMaxScale = chartParams.getChartMaxScale();
			final double chartMinScale = chartParams.getChartMinScale();
			final double chartTotal = n.getChartTotal();
			if (chartTotal <= 0){
				n.setChartR(chartMinScale);
			} else if (chartParams.isAbsolute()){
				final double chartMinVal = chartParams.getChartMinVal();
				final double chartMaxVal = chartParams.getChartMaxVal();
				if (chartMaxVal == chartMinVal){
					n.setChartR(chartMinScale);
				} else{
					n.setChartR(chartMinScale + (Math.min(chartMaxVal, Math.max(chartMinVal, chartTotal)) - chartMinVal)
							* (chartMaxScale - chartMinScale) / (chartMaxVal - chartMinVal));
				}
			} else{
				final double currGraphMinTotal = chartParams.getCurrentGraphMinTotal();
				final double currGraphMaxTotal = chartParams.getCurrentGraphMaxTotal();
				if (currGraphMaxTotal == currGraphMinTotal){
					n.setChartR(chartParams.getChartDefaultScale());
				} else{
					n.setChartR(chartMinScale
							+ (Math.min(currGraphMaxTotal, Math.max(currGraphMinTotal, chartTotal)) - currGraphMinTotal)
							* (chartMaxScale - chartMinScale) / (currGraphMaxTotal - currGraphMinTotal));
				}
			}
		}
	}

	protected void paintPie(final Node n, final Graphics2D g2, final int x, final int y, final double scale,
							final ChartFilter cFilter, final ChartParams cParams){

		double positiveTotal = getPositiveTotal(n, cFilter);

		if (positiveTotal == 0){
			g2.setColor(Color.black);
			final BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			double chartR = cParams.getChartMinScale();
			g2.drawOval((int) (x - chartR / 2.0 * scale), (int) (y - chartR / 2.0 * scale), (int) (chartR * scale),
					(int) (chartR * scale));
		}

		int Start, Angle;
		double Cumulative;

		Start = 0;
		Cumulative = 0.0;

		if (positiveTotal != 0){
			for (int c = 0; c < n.getChartCount(); c++){
				if (isSliceDisplayable(c, n, cFilter) && n.getChartValue(c) > 0){
					Cumulative += n.getChartValue(c);

					if (Cumulative >= positiveTotal){
						Angle = 360 - Start;
					} else{
						Angle = (int) ((360.0 / positiveTotal) * n.getChartValue(c));
					}

					g2.setColor(n.getChartColor(c));
					g2.fillArc((int) (x - n.getChartR() / 2.0 * scale), (int) (y - n.getChartR() / 2.0 * scale), (int) (n
							.getChartR() * scale), (int) (n.getChartR() * scale), Start, Angle);

					Start += Angle;
				}
			}
		}
	}

	private double getPositiveTotal(Node node, ChartFilter cFilter){
		double total = 0;
		for (int n = 0; n < node.getChartCount(); n++){
			double value = node.getChartValue(n);
			if (value > 0.0 && !cFilter.isExcluded(n) && value >= cFilter.getMinChartAttrVal(n) - 0.0005
					&& value <= cFilter.getMaxChartAttrVal(n) + 0.0005){
				total += value;
			}
		}
		return total;
	}

	void paintBar(final Node n, final Graphics2D g2, final int x, final int y, final double scale,
				  final ChartFilter cFilter, ChartParams cParams){

		double positiveTotal = getPositiveTotal(n, cFilter);
		if (n.getChartTotal() == 0 && positiveTotal == 0){
			g2.setColor(Color.black);
			final BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			double chartR = cParams.getChartMinScale();
			g2.drawRect((int) (x), (int) (y - chartR * scale), (int) (chartR * scale), (int) (chartR * scale));
		} else{

			int xb = 0;
			int dx = (int) (20 * scale);

			double maxVal = cParams.isAbsolute() ? cParams.getChartSliceMaxVal() : cParams.getCurrentGraphMaxValueDiff();
			for (int c = 0; c < n.getChartCount(); c++){
				if (isSliceDisplayable(c, n, cFilter)){
					g2.setColor(n.getChartColor(c));

					int ht = 0;
					if (maxVal != 0.0)
						ht = (int) (cParams.getChartMaxScale() / maxVal * n.getChartValue(c) * scale);
					if (n.getChartValue(c) > 0 && ht <= 0)
						ht = 1;

					int yy;
					if (ht > 0)
						yy = y - ht + 1;
					else
						yy = y - 1;
					g2.fillRect(x + xb - 1, yy, dx, Math.abs(ht));
					xb += dx;
				}
			}
		}

	}

	protected void paintStacked(final Node n, final Graphics2D g2, final int x, final int y, final double scale,
								final ChartFilter cFilter, final ChartParams cParams){
		if (n.getChartTotal() == 0){
			g2.setColor(Color.black);
			final BasicStroke bst = new BasicStroke(1);
			g2.setStroke(bst);
			g2.drawRect((int) (x - 5 * scale), (int) (y - n.getChartR() / 2.0 * scale), (int) (10 * scale), (int) (n
					.getChartR() * scale));
		}

		if (n.getChartTotal() != 0){
			double Total;
			double dy;
			int ys;

			Total = 0.0;

			for (int c = 0; c < n.getChartCount(); c++){
				Total += n.getChartValue(c);
			}

			ys = (int) (y - (n.getChartR() / 2.0 * scale));
			dy = (n.getChartR() / Total) * scale;

			for (int c = 0; c < n.getChartCount(); c++){
				if (isSliceDisplayable(c, n, cFilter)){
					g2.setColor(n.getChartColor(c));

					g2.fillRect((int) (x - 5 * scale), ys, (int) (10 * scale), (int) (dy * n.getChartValue(c)));
				}

				ys += (int) (dy * n.getChartValue(c));
			}
		}
	}

	@Override
	public void paintPolygon(final Node n, final Graphics2D g, final boolean fillPoly, final float alpha,
							 final Color polyColor){
		if (!fillPoly && !n.toBlink()){
			return;
		}

		List<Point> childrenPoints = new ArrayList<Point>(50);
		childrenPoints.add(new Point((int) n.getX(), (int) n.getY()));

		for (final Edge e : n.inEdges){
			if (e.isActive() && e.from.isActive() /* && e.from.degree > degree */){
				final Point p = new Point((int) e.from.getX(), (int) e.from.getY());
				if (!childrenPoints.contains(p)){
					childrenPoints.add(p);
				}
			}
		}

		for (final Edge e : n.outEdges){
			if (e.isActive() && e.to.isActive() /* && e.to.degree > degree */){
				final Point p = new Point((int) e.to.getX(), (int) e.to.getY());
				if (!childrenPoints.contains(p)){
					childrenPoints.add(p);
				}
			}
		}

		if (childrenPoints.size() < 3)
			return;

		Polygon polygon;
		if (childrenPoints.size() > 3){ // If triangle no work is needed
			polygon = new ConvexHull(childrenPoints).getConvexHull();
		} else{
			int[] x = new int[childrenPoints.size()];
			int[] y = new int[childrenPoints.size()];
			for (int i = 0; i < childrenPoints.size(); i++){
				x[i] = childrenPoints.get(i).x;
				y[i] = childrenPoints.get(i).y;
			}
			polygon = new Polygon(x, y, childrenPoints.size());
		}

		final Composite originalComposite = g.getComposite();
		g.setColor(polyColor);

		if (fillPoly){
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g.fillPolygon(polygon);
		} else{
			g.setStroke(polyStroke);
			g.drawPolygon(polygon);
		}

		g.setComposite(originalComposite);
	}

}
