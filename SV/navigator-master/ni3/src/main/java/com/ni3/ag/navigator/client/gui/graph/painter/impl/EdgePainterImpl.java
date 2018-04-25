/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.domain.EdgeMetaphor;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.InvisibleStroke;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.painter.EdgePainter;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Utility;

public class EdgePainterImpl extends ObjectPainter implements EdgePainter{
	private static final Logger log = Logger.getLogger(EdgePainterImpl.class);

	private static double highlightedEdgeScaleFactor = 1.0;

	private GeneralPath arrow = null;
	private final Color arcColor2 = Color.blue;
	private final Color arcColor3 = Color.red;
	private final Double inFocusWidth;
	private final Color inFocusColor;

	public EdgePainterImpl(){
		inFocusWidth = Double.valueOf(UserSettings.getProperty("Applet", "EdgeInFocusWidth", "2.0"));
		inFocusColor = UserSettings.getColor("Applet", "EdgeInFocusColor", Color.red);
	}

	public Color getInFocusColor(){
		return inFocusColor;
	}

	@Override
	// TODO optimize, split to multiple and simplify method
	public boolean paint(Edge e, Graphics g, boolean showArrows, Node selected, boolean showLabel,
			boolean showEdgeThickness, double zoomf, boolean inPathEdge){
		final FontMetrics fm = g.getFontMetrics();
		if (e.edgeStyle != Edge.ES_None && e.isActive() && e.from.Obj != null && e.from.Obj.getIcon() != null
				&& e.to.Obj != null && e.to.Obj.getIcon() != null && e.getMetaindex() >= 0){
			Graphics2D g2 = (Graphics2D) g;
			Color col;
			BasicStroke stroke = null;

			if (inPathEdge){
				try{
					Node fromNode = e.from;
					Entity ent = fromNode.Obj.getEntity();
					Schema sch = ent.getSchema();
					EdgeMetaphor edgeMetaphor = sch.edgeMetaphor;
					stroke = edgeMetaphor.createStroke(e.getMetaindex(), highlightedEdgeScaleFactor * 2);
				} catch (NullPointerException ex){
					log.error(ex.getMessage(), ex);
				}

				if (stroke == null)
					return false;

				g2.setStroke(stroke);
			} else{
				double FocusFactor = 1.0;

				if (SystemGlobals.MainFrame.Doc.getInFocusEdges().contains(e))
					FocusFactor = inFocusWidth;

				if (e.from.Obj != null){

					if (SystemGlobals.MainFrame.Doc.getInFocusEdges().contains(e)){
						stroke = e.from.Obj.getEntity().getSchema().edgeMetaphor.createStroke(e.getMetaindex(), FocusFactor
								/ zoomf);
					} else if (zoomf > Node.MaxMetaphorZoom)
						stroke = e.from.Obj.getEntity().getSchema().edgeMetaphor.createStroke(e.getMetaindex(),
								(Node.MaxMetaphorZoom / zoomf) * e.getStrength() * FocusFactor);
					else if (e.getStrength() == 1.0 || !showEdgeThickness){
						stroke = e.from.Obj.getEntity().getSchema().edgeMetaphor.getStroke(e.getMetaindex());
					} else{
						if (e.eStroke == null)
							e.eStroke = e.from.Obj.getEntity().getSchema().edgeMetaphor.createStroke(e.getMetaindex(),
									(Node.MaxMetaphorZoom / zoomf) * e.getStrength() / zoomf);
						stroke = e.eStroke;
					}

					if (stroke == null)
						return false;

					g2.setStroke(stroke);
				}
			}

			boolean isInvisible = (stroke instanceof InvisibleStroke);

			if (SystemGlobals.MainFrame.Doc.getInFocusEdges().contains(e))
				col = inFocusColor;
			else if (isInvisible){
				col = ((InvisibleStroke) stroke).getColor();
			} else if (e.from.Obj != null){
				col = e.from.Obj.getEntity().getSchema().edgeMetaphor.getLineColor(e.getMetaindex());
			} else
				col = Color.black;

			int x1, y1, x2, y2;

			switch (e.edgeStyle){
				case Edge.ES_Ortho:
				case Edge.ES_Angular:
					x1 = (int) e.from.getX();
					y1 = (int) e.from.getY();
					x2 = (int) e.to.getX();
					y2 = (int) e.to.getY();

					break;

				default:
					x1 = (int) e.from.getX();
					y1 = (int) e.from.getY();
					x2 = (int) e.to.getX();
					y2 = (int) e.to.getY();
					break;
			}

			double fx = (x2 - x1);
			double fy = (y2 - y1);

			if (selected != null){
				if (selected == e.from)
					col = arcColor2;
				else if (selected == e.to)
					col = arcColor3;
			}

			double ax, ay; // Mid point of edge

			{
				g2.setColor(col);

				ax = (x1 + x2) / 2;
				ay = (y1 + y2) / 2;

				if (e.multiEdgeIndex == 0){
					switch (e.edgeStyle){
						case Edge.ES_Line:
							g2.drawLine(x1, y1, x2, y2);
							break;

						case Edge.ES_Ortho: {
							g2.drawLine(x1, y1, x1, (y1 + y2) / 2);
							g2.drawLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2);
							g2.drawLine(x2, y2, x2, (y1 + y2) / 2);

							ax = x2;
							ay = (y2 + (y1 + y2) / 2 - e.to.getScaledMetaphorHeight(false) / 2) / 2;

							fx = 0;
							fy = y2 - (y1 + y2) / 2;
						}
							break;

						case Edge.ES_Angular: {
							g2.drawLine(x1, y1, x1, y2);
							g2.drawLine(x1, y2, x2, y2);

							int ww = e.to.getScaledMetaphorWidth(false);
							if (e.from.degree > e.to.degree)
								ww = e.from.getScaledMetaphorWidth(false);

							ax = (x1 + x2 - ww / 2) / 2;
							ay = y2;

							fx = x2 - x1;
							fy = 0;
						}
							break;
					}
				} else{
					switch (e.edgeStyle){
						case Edge.ES_Line:
							int position;
							if ((e.multiEdgeIndex + 1) % 2 == 0)
								position = -(e.multiEdgeIndex + 1) / 2;
							else
								position = (e.multiEdgeIndex + 1) / 2;

							if (e.from.ID < e.to.ID)
								position = -position;

							{
								double angle = Math.atan2(fy, fx) + Math.PI / 2.0;

								double len = 10;

								ax += position * len * Math.cos(angle);
								ay += position * len * Math.sin(angle);

								QuadCurve2D.Double curve;
								curve = new QuadCurve2D.Double();

								curve.setCurve(x1, y1, ax, ay, x2, y2);

								g2.draw(curve);

								ax = x1 * 0.25 + ax * 0.5 + x2 * 0.25;
								ay = y1 * 0.25 + ay * 0.5 + y2 * 0.25;
							}
							break;

						case Edge.ES_Ortho: {
							if ((e.multiEdgeIndex + 1) % 2 == 0)
								position = -(e.multiEdgeIndex + 1) / 2;
							else
								position = (e.multiEdgeIndex + 1) / 2;

							x2 += position * 4;

							g2.drawLine(x1, y1, x1, (y1 + y2) / 2);
							g2.drawLine(x1, (y1 + y2) / 2, x2, (y1 + y2) / 2);
							g2.drawLine(x2, y2, x2, (y1 + y2) / 2);

							ax = x2;
							ay = (y2 + (y1 + y2) / 2 - e.to.getScaledMetaphorHeight(false) / 2) / 2;

							fx = 0;
							fy = y2 - (y1 + y2) / 2;
						}
							break;

						case Edge.ES_Angular: {
							g2.drawLine(x1, y1, x1, y2);
							g2.drawLine(x1, y2, x2, y2);

							int ww = e.to.getScaledMetaphorWidth(false);
							if (e.from.degree > e.to.degree)
								ww = e.from.getScaledMetaphorWidth(false);

							ax = (x1 + x2 - ww / 2) / 2;
							ay = y2;

							fx = x2 - x1;
							fy = 0;
						}
							break;
					}
				}
			}

			Color[] halos = e.getHalos();

			if (showArrows || e.status == 0 || halos.length > 0){
				e.cx = ax;
				e.cy = ay;

				if (!isInvisible){
					AffineTransform prevAt;

					prevAt = g2.getTransform();
					g2.translate(e.cx, e.cy);
					g2.rotate(Math.atan2(fy, fx));
					g.setColor(Color.black);

					if (showArrows && e.getDirected() == 1){
						g2.fill(getArrow());
					}

					g2.setTransform(prevAt);

					if (e.status == 0){
						g2.fillOval((int) (e.cx - 3), (int) (e.cy - 3), 6, 6);
					} else{
						g2.fillRect((int) (e.cx - 1), (int) (e.cy - 1), 2, 2);
					}
				}

				if (halos.length > 0)
					drawHalo((Graphics2D) g, (int) e.cx, (int) e.cy, 6, 6, halos);
			}

			if (showLabel && e.Obj != null){
				if (e.lbl == null)
					e.lbl = e.Obj.getLabel();
				if (getLabelWrapLength() > 0 && e.lbl.length() > getLabelWrapLength()){
					List<String> lines = e.getSplittedLabel(getLabelWrapLength());
					for (int ln = 0; ln < lines.size(); ln++){
						int xw = fm.stringWidth(lines.get(ln));
						g.drawString(lines.get(ln), (int) ax - (xw - 10) / 2, ((int) ay) + fm.getAscent() + ln * 7);
					}
				} else{
					int xw = fm.stringWidth(e.lbl);
					g.drawString(e.lbl, (int) ax - (xw - 10) / 2, ((int) ay) + fm.getAscent());
				}
			}

			if (Utility.DEBUG){
				String id = String.valueOf(e.ID);
				int xw = fm.stringWidth(id);
				g.drawString(id, (int) ax - (xw - 10) / 2, ((int) ay) + fm.getAscent());
				g.drawString("(" + e.favoritesID + ")", (int) ax - (xw - 10) / 2, ((int) ay + 10) + fm.getAscent());
			}

			return true;
		}

		return false;
	}

	@Override
	public void nextPaintState(){
		highlightedEdgeScaleFactor /= 1.5;
		if (highlightedEdgeScaleFactor < 0.1)
			highlightedEdgeScaleFactor = 3.0;
	}

	private GeneralPath getArrow(){
		if (arrow == null){
			arrow = createArrow(1);
		}
		return arrow;
	}

	protected GeneralPath getArrow(double scale){
		GeneralPath currentArrow;
		if (scale == 1){
			currentArrow = getArrow();
		} else{
			currentArrow = createArrow(scale);
		}

		return currentArrow;
	}

	private GeneralPath createArrow(double scale){
		double height = 15 * scale;
		double base = 9 * scale;
		double notch_height = 3 * scale;
		GeneralPath arrow = new GeneralPath();
		arrow.moveTo(height / 2, 0);
		arrow.lineTo(-height / 2, base / 2.0f);
		arrow.lineTo(-(height / 2 - notch_height), 0);
		arrow.lineTo(-height / 2, -base / 2.0f);
		arrow.lineTo(height / 2, 0);
		return arrow;
	}
}
