/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.awt.Point;

import junit.framework.TestCase;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.map.MapView;

public class MapEdgePainterImplTest extends TestCase{
	private MapEdgePainterImpl painter;
	private MapView mapView;

	@SuppressWarnings("serial")
    @Override
	protected void setUp() throws Exception{
		mapView = new MapView(){
			@Override
			public TileServer getTileServer(){
				return new TileServer(null, 15);
			}
		};
		UserSettings.initEmptySettings();
		painter = new MapEdgePainterImpl(mapView);
	}

	public void testGetCenterPositionOnEdge(){
		Point result = painter.getCenterPositionOnEdge(new Point(2, 15), new Point(4, 7));
		assertEquals(new Point(3, 11), result);

		result = painter.getCenterPositionOnEdge(new Point(5, 7), new Point(2, 14));
		assertEquals(new Point(3, 10), result);

		result = painter.getCenterPositionOnEdge(new Point(5, 7), new Point(5, 11));
		assertEquals(new Point(5, 9), result);

		result = painter.getCenterPositionOnEdge(new Point(11, 7), new Point(5, 7));
		assertEquals(new Point(8, 7), result);
	}

	public void testGetPositionOnEdge(){
		Double coeff = 0.6;
		Point result = painter.getPositionOnEdge(new Point(2, 15), new Point(4, 7), coeff);
		assertEquals(new Point(3, 10), result);

		result = painter.getPositionOnEdge(new Point(5, 7), new Point(2, 14), coeff);
		assertEquals(new Point(3, 11), result);

		result = painter.getPositionOnEdge(new Point(5, 7), new Point(5, 11), coeff);
		assertEquals(new Point(5, 9), result);

		result = painter.getPositionOnEdge(new Point(11, 7), new Point(5, 7), coeff);
		assertEquals(new Point(7, 7), result);
	}
}
