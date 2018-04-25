/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.painter.impl;

import java.awt.Point;
import java.awt.image.BufferedImage;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import junit.framework.TestCase;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.map.MapView;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;
import com.ni3.ag.navigator.shared.domain.NodeMetaphor;

public class MapNodePainterImplTest extends TestCase{
	private MapView mapView;
	private MapNodePainterImpl painter;

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
		painter = new MapNodePainterImpl(mapView);
	}

	public void testGetIconPositionTestNumericMetaphor(){
		Point position = new Point(100, 200);

		Point p = painter.getIconPosition(position, 10, 20, true);
		assertEquals(97, p.x);
		assertEquals(180, p.y);
	}

	public void testGetIconPositionTestNotNumericMetaphor(){
		Point position = new Point(100, 200);

		Point p = painter.getIconPosition(position, 10, 20, false);
		assertEquals(95, p.x);
		assertEquals(190, p.y);
	}

	public void testIsPointOnNode(){
		Node node = new Node(0, 0);
		DBObject obj = new DBObject();
		NodeMetaphor nm = new NodeMetaphor();
		nm.setAssignedMetaphor(new MetaphorIcon("icon.png", 1));
		obj.setMetaphor(nm);
		obj.getMetaphor().setIcon(new BufferedImage(10, 20, BufferedImage.TYPE_BYTE_GRAY));
		node.Obj = obj;
		node.setLon(10);
		node.setLat(10);

		mapView.setZoom(5);
		mapView.setMapPosition(new Point(4000, 3500));

		// position: 323, 367
		// expected rectangle of image: 318, 357, 10, 20

		assertFalse(painter.isPointOnNode(node, new Point(0, 0), false, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(320, 350), false, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(330, 360), false, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(320, 380), false, null, 1));

		assertTrue(painter.isPointOnNode(node, new Point(318, 357), false, null, 1));
		assertTrue(painter.isPointOnNode(node, new Point(327, 376), false, null, 1));
	}

	public void testIsPointOnNodeNumericMetaphors(){
		Node node = new Node(0, 0);
		DBObject obj = new DBObject();
		NodeMetaphor nm = new NodeMetaphor();
		nm.setAssignedMetaphor(new MetaphorIcon("icon.png", 1));
		obj.setMetaphor(nm);
		obj.getMetaphor().setIcon(new BufferedImage(10, 20, BufferedImage.TYPE_BYTE_GRAY));
		node.Obj = obj;
		node.setLon(10);
		node.setLat(10);

		mapView.setZoom(5);
		mapView.setMapPosition(new Point(4000, 3500));

		// position: 323, 367
		// expected rectangle of icon: 320, 347, 10, 20

		assertFalse(painter.isPointOnNode(node, new Point(0, 0), true, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(300, 350), true, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(335, 360), true, null, 1));
		assertFalse(painter.isPointOnNode(node, new Point(320, 380), true, null, 1));

		assertTrue(painter.isPointOnNode(node, new Point(320, 347), true, null, 1));
		assertTrue(painter.isPointOnNode(node, new Point(329, 366), true, null, 1));
	}
}
