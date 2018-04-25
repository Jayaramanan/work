/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Set;

import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.map.MapSettings;

public interface MapEdgePainter{

	void paint(Edge e, Graphics2D g, MapSettings settings, Set<Edge> inFocusEdges, Set<Edge> highlightedPathEdges,
	        boolean showLabels, boolean directedEdges);

	boolean isPointOnEdge(Edge edge, Point point, double maxError);
}
