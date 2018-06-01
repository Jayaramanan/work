/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter;

import java.awt.Graphics2D;
import java.awt.Point;

import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.shared.domain.ChartType;

public interface MapNodePainter{

	void paint(Node n, Graphics2D g, Ni3Document doc, double scale, double blinkScaleFactor, final boolean showLabel);

	boolean isPointOnNode(Node node, Point point, boolean showNumericMetaphors, ChartType chartType, double scale);
}
