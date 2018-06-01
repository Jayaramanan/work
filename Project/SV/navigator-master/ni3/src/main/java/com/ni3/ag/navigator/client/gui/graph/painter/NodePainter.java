/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter;

import java.awt.*;

import com.ni3.ag.navigator.client.domain.ChartFilter;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.gui.graph.Node;

public interface NodePainter{

	boolean paint(Node n, Graphics2D g, double zoomf, boolean picked, boolean showLabel, boolean showExpandCounter, ChartFilter filter, ChartParams chartParams);

	void paintPolygon(Node n, Graphics2D g, boolean fillPoly, float alpha, Color polyColor);

}
