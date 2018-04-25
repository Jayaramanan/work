/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Set;

import com.ni3.ag.navigator.client.domain.EdgeMetaphor;
import com.ni3.ag.navigator.client.domain.InvisibleStroke;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.painter.MapEdgePainter;
import com.ni3.ag.navigator.client.gui.map.MapPanel;
import com.ni3.ag.navigator.client.gui.map.MapSettings;

public class MapEdgePainterImpl extends EdgePainterImpl implements MapEdgePainter{
	private MapPanel mapPanel;

	public MapEdgePainterImpl(MapPanel mapPanel){
		this.mapPanel = mapPanel;
	}

	@Override
	public void paint(Edge e, Graphics2D g, MapSettings settings, Set<Edge> inFocusEdges, Set<Edge> highlightedPathEdges,
			boolean showLabel, boolean directedEdges){
		final Node from = e.from;
		final Node to = e.to;
		if (e.Obj == null || !from.isActive() || !to.isActive() || from.getLat() == 0 || to.getLat() == 0){
			return; // start or end coordinates not set
		}

		final Point mp = mapPanel.getMapPosition();
		final Point fromPosition = mapPanel.computePosition(new Point2D.Double(from.getLon(), from.getLat()));
		fromPosition.translate(-mp.x, -mp.y);
		final Point toPosition = mapPanel.computePosition(new Point2D.Double(to.getLon(), to.getLat()));
		toPosition.translate(-mp.x, -mp.y);

		setEdgeProperties(g, e, settings, inFocusEdges, highlightedPathEdges);

		g.drawLine(fromPosition.x, fromPosition.y, toPosition.x, toPosition.y);

		if (directedEdges && e.isDirected()){
			drawArrow(g, fromPosition, toPosition, settings.getEdgeScale());
		}

		final Point middle = getCenterPositionOnEdge(fromPosition, toPosition);

		Color[] halos = e.getHalos();
		if (halos.length > 0)// TODO should halo be scaled with edge width?
			drawHalo(g, middle.x, middle.y, 6 * settings.getEdgeScale(), 4, halos);
	}

	private void drawArrow(Graphics2D g, Point from, Point to, double edgeScale){
		double dx = to.x - from.x;
		double dy = to.y - from.y;
		double angle = Math.atan2(dy, dx);
		AffineTransform prevAt = g.getTransform();

		final Point position = getPositionOnEdge(from, to, 0.6);
		if (position != null){
			g.translate(position.x, position.y);
			g.rotate(angle);
			g.fill(getArrow(edgeScale));

			g.setTransform(prevAt);
		}
	}

	@Override
	public boolean isPointOnEdge(Edge edge, Point point, double maxError){
		final Node from = edge.from;
		final Node to = edge.to;
		if (!from.isActive() || !to.isActive() || from.getLat() == 0 || to.getLat() == 0){
			return false; // start or end coordinates not set
		}
		final Point mp = mapPanel.getMapPosition();
		final Point fromPosition = mapPanel.computePosition(new Point2D.Double(from.getLon(), from.getLat()));
		fromPosition.translate(-mp.x, -mp.y);
		final Point toPosition = mapPanel.computePosition(new Point2D.Double(to.getLon(), to.getLat()));
		toPosition.translate(-mp.x, -mp.y);

		return isPointOnLine(fromPosition, toPosition, point, maxError);
	}

	private boolean isPointOnLine(Point from, Point to, Point point, double maxError){
		double xDelta = to.x - from.x;
		double yDelta = to.y - from.y;

		if ((xDelta == 0) && (yDelta == 0)){
			return false;
		}

		double u = ((point.x - from.x) * xDelta + (point.y - from.y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);
		if (u < 0 || u > 1){
			return false;
		}

		xDelta = from.x + u * xDelta - point.x;
		yDelta = from.y + u * yDelta - point.y;
		double distance = Math.sqrt(xDelta * xDelta + yDelta * yDelta);

		return distance <= maxError;
	}

	private void setEdgeProperties(Graphics2D g, Edge e, MapSettings settings, Set<Edge> inFocusEdges,
			Set<Edge> highlightedPathEdges){
		final EdgeMetaphor edgeMetaphor = e.Obj.getEntity().getSchema().edgeMetaphor;
		boolean inPathEdge = highlightedPathEdges.contains(e);
		double scale = settings.getEdgeScale() * (inPathEdge ? settings.getBlinkingEdgeScale() * 3 : 1);
		final Stroke stroke = edgeMetaphor.createStroke(e.getMetaindex(), scale, 2.0);
		g.setStroke(stroke);

		Color color = Color.BLACK;
		if (inFocusEdges.contains(e))
			color = getInFocusColor();
		else if (stroke instanceof InvisibleStroke){
			color = ((InvisibleStroke) stroke).getColor();
		} else{
			color = edgeMetaphor.getLineColor(e.getMetaindex());
		}
		g.setColor(color);
	}

	Point getCenterPositionOnEdge(Point from, Point to){
		return getPositionOnEdge(from, to, 0.5);
	}

	Point getPositionOnEdge(Point from, Point to, double coeff){
		final int xMid = (int) (from.x + (to.x - from.x) * coeff);
		final int yMid = (int) (from.y + (to.y - from.y) * coeff);
		return new Point(xMid, yMid);
	}

}
