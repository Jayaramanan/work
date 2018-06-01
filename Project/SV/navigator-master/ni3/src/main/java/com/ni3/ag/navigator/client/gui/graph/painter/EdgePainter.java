/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter;

import java.awt.Graphics;

import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;

public interface EdgePainter{

	public boolean paint(Edge e, Graphics g, boolean showArrows, Node selected, boolean showLabel,
	        boolean showEdgeThickness, double zoomf, boolean inPathEdge);

	void nextPaintState();
}
