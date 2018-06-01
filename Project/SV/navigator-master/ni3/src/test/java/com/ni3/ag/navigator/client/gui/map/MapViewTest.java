/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.map;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import junit.framework.TestCase;

import com.ni3.ag.navigator.client.gui.map.MapPanel.DragListener;

public class MapViewTest extends TestCase{

	private MapView mapView;

	@Override
	protected void setUp() throws Exception{
		mapView = new MapView();
	}

	public void testGetBoundingRectangleOneNode(){
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(0, 0);
		n.Obj = new DBObject();
		n.setLon(10);
		n.setLat(20);
		nodes.add(n);

		final Double rect = mapView.getBoundingRectangle(nodes);

		assertEquals(10, rect.getX(), 0);
		assertEquals(20, rect.getY(), 0);
		assertEquals(0, rect.getWidth(), 0);
		assertEquals(0, rect.getHeight(), 0);
	}

	public void testGetBoundingRectangle(){
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(0, 0);
		n.Obj = new DBObject();
		n.setLon(10);
		n.setLat(20);
		nodes.add(n);

		Node n1 = new Node(0, 0);
		n1.Obj = new DBObject();
		n1.setLon(30);
		n1.setLat(30);
		nodes.add(n1);

		Node n2 = new Node(0, 0);
		n2.Obj = new DBObject();
		n2.setLon(70);
		n2.setLat(70);
		nodes.add(n2);

		final Double rect = mapView.getBoundingRectangle(nodes);

		assertEquals(10, rect.getX(), 0);
		assertEquals(20, rect.getY(), 0);
		assertEquals(60, rect.getWidth(), 0);
		assertEquals(50, rect.getHeight(), 0);
	}

	public void testGetBoundingRectangleSouthernHemisphere(){
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(0, 0);
		n.Obj = new DBObject();
		n.setLon(-10);
		n.setLat(-20);
		nodes.add(n);

		Node n1 = new Node(0, 0);
		n1.Obj = new DBObject();
		n1.setLon(-30);
		n1.setLat(-30);
		nodes.add(n1);

		Node n2 = new Node(0, 0);
		n2.Obj = new DBObject();
		n2.setLon(-70);
		n2.setLat(-70);
		nodes.add(n2);

		final Double rect = mapView.getBoundingRectangle(nodes);

		assertEquals(-70, rect.getX(), 0);
		assertEquals(-70, rect.getY(), 0);
		assertEquals(60, rect.getWidth(), 0);
		assertEquals(50, rect.getHeight(), 0);
	}

	public void testGetBoundingRectangleDifferentHemispheres(){
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(0, 0);
		n.Obj = new DBObject();
		n.setLon(10);
		n.setLat(20);
		nodes.add(n);

		Node n1 = new Node(0, 0);
		n1.Obj = new DBObject();
		n1.setLon(-30);
		n1.setLat(-30);
		nodes.add(n1);

		Node n2 = new Node(0, 0);
		n2.Obj = new DBObject();
		n2.setLon(-70);
		n2.setLat(-70);
		nodes.add(n2);

		final Double rect = mapView.getBoundingRectangle(nodes);

		assertEquals(-70, rect.getX(), 0);
		assertEquals(-70, rect.getY(), 0);
		assertEquals(80, rect.getWidth(), 0);
		assertEquals(90, rect.getHeight(), 0);
	}

	public void testGetBoundingRectangleIncorrectCoordinates(){
		List<Node> nodes = new ArrayList<Node>();
		Node n = new Node(0, 0);
		n.Obj = new DBObject();
		n.setLon(200);
		n.setLat(100);
		nodes.add(n);

		Node n1 = new Node(0, 0);
		n1.Obj = new DBObject();
		n1.setLon(-200);
		n1.setLat(-100);
		nodes.add(n1);

		final Double rect = mapView.getBoundingRectangle(nodes);

		assertEquals(-180, rect.getX(), 0);
		assertEquals(-90, rect.getY(), 0);
		assertEquals(360, rect.getWidth(), 0);
		assertEquals(180, rect.getHeight(), 0);
	}

	@SuppressWarnings("serial")
	public void testCalculateZoomLevelOneCoordinate(){
		mapView = new MapView(){
			@Override
			public TileServer getTileServer(){
				return new TileServer(null, 15);
			}
		};

		Double rect = new Double(10, 20, 0, 0);
		int zoom = mapView.calculateZoomLevel(rect);
		assertEquals(15, zoom);
	}

	@SuppressWarnings("serial")
	public void testCalculateZoomLevel1x1degreesRect(){
		mapView = new MapView(){
			@Override
			public TileServer getTileServer(){
				return new TileServer(null, 15);
			}
		};
		Double rect = new Double(10, 20, 1, 1);
		// zoom level = 10, width = 728.0, height = 778

		mapView.setSize(700, 800);
		int zoom = mapView.calculateZoomLevel(rect);
		assertEquals(9, zoom);

		mapView.setSize(800, 700);
		zoom = mapView.calculateZoomLevel(rect);
		assertEquals(9, zoom);

		mapView.setSize(800, 800);
		zoom = mapView.calculateZoomLevel(rect);
		assertEquals(10, zoom);
	}

	@SuppressWarnings("serial")
	public void testCalculateZoomLevel10x10degreesRect(){
		mapView = new MapView(){
			@Override
			public TileServer getTileServer(){
				return new TileServer(null, 15);
			}
		};
		Double rect = new Double(-10, -20, 10, 10);
		// zoom level = 6, width = 456.0, height = 472

		mapView.setSize(450, 500);
		int zoom = mapView.calculateZoomLevel(rect);
		assertEquals(5, zoom);

		mapView.setSize(500, 450);
		zoom = mapView.calculateZoomLevel(rect);
		assertEquals(5, zoom);

		mapView.setSize(500, 500);
		zoom = mapView.calculateZoomLevel(rect);
		assertEquals(6, zoom);
	}

	public void testGetNewCenterPosition(){
		mapView = new MapView(){
			@Override
			public TileServer getTileServer(){
				return new TileServer(null, 15);
			}
		};
		mapView.setZoom(5);
		mapView.setCenterPosition(new Point(200, 200));

		Point position = mapView.getNewCenterPosition(10);
		assertEquals(200 * 32, position.x, 1);
		assertEquals(200 * 32, position.y, 1);

		mapView.setZoom(10);
		mapView.setCenterPosition(new Point(6400, 6400));
		position = mapView.getNewCenterPosition(5);
		assertEquals(6400 / 32, position.x, 1);
		assertEquals(6400 / 32, position.y, 1);
	}

	public void testGetSearchRectangle(){
		DragListener lsn = mapView.new MouseDragListener(){
			public Point getDownCoords(){
				return new Point(100, 50);
			};

			public Point getMouseCoords(){
				return new Point(200, 300);
			};
		};

		mapView.setDragListener(lsn);

		Rectangle rect = mapView.getSearchRectangle();
		assertEquals(new Rectangle(100, 50, 100, 250), rect);
	}

	public void testGetSearchRectangle2(){
		DragListener lsn = mapView.new MouseDragListener(){
			public Point getDownCoords(){
				return new Point(200, 300);
			};

			public Point getMouseCoords(){
				return new Point(100, 50);
			};
		};

		mapView.setDragListener(lsn);

		Rectangle rect = mapView.getSearchRectangle();
		assertEquals(new Rectangle(100, 50, 100, 250), rect);
	}
}
