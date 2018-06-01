/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.shared.domain.GISOverlay;
import com.ni3.ag.navigator.shared.domain.GISPolygon;
import com.ni3.ag.navigator.shared.domain.GisOverlayGeometry;
import com.ni3.ag.navigator.shared.domain.GisOverlayPolygon;
import com.ni3.ag.navigator.shared.domain.GisThematicGeometry;

public class GisDataPainter{
	private static final Logger log = Logger.getLogger(GisDataPainter.class);
	private MapPanel mapPanel;
	private Map<Integer, List<Area>> thematicDataCache;
	private Map<Integer, Map<Integer, List<Polygon>>> overlayCache;

	public GisDataPainter(MapPanel mapPanel){
		this.mapPanel = mapPanel;
		thematicDataCache = new HashMap<Integer, List<Area>>();
		overlayCache = new HashMap<Integer, Map<Integer, List<Polygon>>>();
	}

	public void paintThematicData(Graphics2D g, List<GisThematicGeometry> geometries){
		final int zoom = mapPanel.getZoom();
		final Point mp = mapPanel.getMapPosition();
		final Rectangle2D.Double visibleCoords = getVisibleRectangleInCoords(zoom, mp);
		final Rectangle visibleRect = mapPanel.getVisibleRect();
		visibleRect.translate(mp.x, mp.y);

		synchronized (geometries){
			for (GisThematicGeometry geometry : geometries){
				final boolean boundsSet = geometry.getBounds() != null;
				if (boundsSet && !visibleCoords.intersects(geometry.getBounds())){
					continue; // skip geometry, because it's not visible
				} else if (!boundsSet){
					setThematicBounds(geometry);
				}

				Color c = geometry.getColor();
				if (c != null){
					c = getTransparentColor(c);
				}
				g.setColor(c);

				final List<Area> areas = getThematicAreas(geometry, zoom, mp);

				for (Area area : areas){
					if (area.intersects(visibleRect)){
						final Area areaClone = (Area) area.clone();
						areaClone.transform(AffineTransform.getTranslateInstance(-mp.x, -mp.y));
						g.fill(areaClone);
					}
				}
			}
		}
	}

	private List<Area> getThematicAreas(GisThematicGeometry geometry, int zoom, Point mp){
		List<Area> areas;
		if (thematicDataCache.get(geometry.getGeometryId()) != null){
			// load areas from cache
			areas = thematicDataCache.get(geometry.getGeometryId());
		} else{
			// create areas from points
			areas = new ArrayList<Area>();
			thematicDataCache.put(geometry.getGeometryId(), areas);

			final List<GISPolygon> gisPolygons = geometry.getPolygons();
			if (gisPolygons != null && !gisPolygons.isEmpty()){
				final int count = gisPolygons.size();
				for (int k = 0; k < count; k++){
					GISPolygon gisPolygon = gisPolygons.get(k);
					Area area = getArea(gisPolygon, mp);
					areas.add(area);
				}
			}
		}
		return areas;
	}

	private void setThematicBounds(GisThematicGeometry geometry){
		double minX = 180;
		double maxX = -180;
		double minY = 90;
		double maxY = -90;
		final List<GISPolygon> polygons = geometry.getPolygons();
		int polygonCount = polygons.size();
		for (int i = 0; i < polygonCount; i++){
			GISPolygon polygon = polygons.get(i);
			if (polygon.getPoints() != null && !polygon.getPoints().isEmpty()){
				for (Point2D.Double point : polygon.getPoints()){
					minX = Math.min(minX, point.x);
					maxX = Math.max(maxX, point.x);
					minY = Math.min(minY, point.y);
					maxY = Math.max(maxY, point.y);
				}
			}
		}
		geometry.setBounds(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
	}

	public void paintOverlayData(Graphics2D g, Set<GISOverlay> set){
		final int zoom = mapPanel.getZoom();
		final Point mp = mapPanel.getMapPosition();
		final Rectangle2D.Double visibleCoords = getVisibleRectangleInCoords(zoom, mp);
		final Rectangle visibleRect = mapPanel.getVisibleRect();
		visibleRect.translate(mp.x, mp.y);

		for (GISOverlay overlay : set){
			Map<Integer, List<Polygon>> cache = overlayCache.get(overlay.getId());
			if (cache == null){
				// init cache for overlay level
				cache = new HashMap<Integer, List<Polygon>>();
				overlayCache.put(overlay.getId(), cache);
			}

			Color c = overlay.getColor();
			if (overlay.isFilled() && c != null){
				c = getTransparentColor(c);
			}
			g.setColor(c);
			g.setStroke(new BasicStroke(overlay.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

			final List<GisOverlayGeometry> geometries = overlay.getGeometries();
			final int gCount = geometries.size();
			for (int i = 0; i < gCount; i++){
				final GisOverlayGeometry geometry = geometries.get(i);
				final boolean boundsSet = geometry.getBounds() != null;
				if (boundsSet && !visibleCoords.intersects(geometry.getBounds())){
					continue; // skip geometry, because it's not visible
				} else if (!boundsSet){
					setOverlayBounds(geometry);
				}

				final List<Polygon> polygons = getOverlayPolygons(cache, geometry, zoom);

				final int count = polygons.size();
				for (int k = 0; k < count; k++){
					final Polygon p = (Polygon) polygons.get(k);
					if (p.intersects(visibleRect)){
						final Polygon polygon = new Polygon(p.xpoints, p.ypoints, p.npoints); // clone polygon
						polygon.translate(-mp.x, -mp.y);
						if (overlay.isFilled()){
							g.fillPolygon(polygon);
						} else{
							g.drawPolygon(polygon);
						}
					}
				}
			}
		}
	}

	private List<Polygon> getOverlayPolygons(Map<Integer, List<Polygon>> cache, GisOverlayGeometry geometry, int zoom){
		List<Polygon> polygons;
		if (cache.get(geometry.getGeometryId()) != null){
			// load polygons from cache
			polygons = cache.get(geometry.getGeometryId());
		} else{
			// create polygons from points
			polygons = new ArrayList<Polygon>();
			cache.put(geometry.getGeometryId(), polygons);

			final List<GisOverlayPolygon> gisPolygons = geometry.getPolygons();
			if (gisPolygons != null && !gisPolygons.isEmpty()){
				final int count = gisPolygons.size();
				for (int k = 0; k < count; k++){
					GisOverlayPolygon overlayPolygon = gisPolygons.get(k);
					Polygon polygon = getPolygon(overlayPolygon, zoom);
					polygons.add(polygon);
				}
			}
		}
		return polygons;
	}

	private void setOverlayBounds(GisOverlayGeometry geometry){
		double minX = 180;
		double maxX = -180;
		double minY = 90;
		double maxY = -90;
		final List<GisOverlayPolygon> polygons = geometry.getPolygons();
		int polygonCount = polygons.size();
		for (int i = 0; i < polygonCount; i++){
			GisOverlayPolygon polygon = polygons.get(i);
			if (polygon.getPoints() != null && !polygon.getPoints().isEmpty()){
				for (Point2D.Double point : polygon.getPoints()){
					minX = Math.min(minX, point.x);
					maxX = Math.max(maxX, point.x);
					minY = Math.min(minY, point.y);
					maxY = Math.max(maxY, point.y);
				}
			}
		}
		geometry.setBounds(new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY));
	}

	public void clearThematicDataCache(){
		thematicDataCache.clear();
	}

	public void clearOverlayCache(){
		overlayCache.clear();
	}

	private Color getTransparentColor(Color c){
		c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 150); // transparent fill
		return c;
	}

	private Rectangle2D.Double getVisibleRectangleInCoords(int zoom, Point mp){
		final Rectangle visibleRect = mapPanel.getVisibleRect();
		visibleRect.translate(mp.x, mp.y);

		final double x = MapPanel.position2lon(visibleRect.x, zoom);
		final double y = MapPanel.position2lat(visibleRect.y, zoom);
		final double x2 = MapPanel.position2lon(visibleRect.x + visibleRect.width, zoom);
		final double y2 = MapPanel.position2lat(visibleRect.y + visibleRect.height, zoom);
		final Rectangle2D.Double rect = new Rectangle2D.Double(Math.min(x, x2), Math.min(y, y2), Math.abs(x2 - x), Math
		        .abs(y2 - y));
		return rect;
	}

	private Polygon getPolygon(GisOverlayPolygon gisPolygon, int zoom){
		Polygon polygon = createPolygon(gisPolygon.getPoints());
		// put polygon to the cache
		if (log.isTraceEnabled()){
			log.trace("zoom=" + zoom + ", old size=" + gisPolygon.getPoints().size() + ", new size=" + polygon.npoints);
		}

		return polygon;
	}

	private Area getArea(GISPolygon gisPolygon, Point mp){
		final Shape shape = createPolygon(gisPolygon.getPoints());
		final Area area = new Area(shape);
		if (gisPolygon.getExclusions() != null && !gisPolygon.getExclusions().isEmpty()){
			for (List<Point2D.Double> exclusion : gisPolygon.getExclusions()){
				final Polygon ePolygon = createPolygon(exclusion);
				area.subtract(new Area(ePolygon));
			}
		}

		return area;
	}

	private Polygon createPolygon(List<Point2D.Double> points){
		List<Point> pList = new ArrayList<Point>();
		Point lastPoint = new Point();
		for (Point2D.Double point : points){
			Point p = mapPanel.computePosition(point);
			if (p.distance(lastPoint) > 0){
				pList.add(p);
			}
			lastPoint = p;
		}

		int[] xPoints = new int[pList.size()];
		int[] yPoints = new int[pList.size()];
		for (int i = 0; i < pList.size(); i++){
			final Point point = pList.get(i);
			xPoints[i] = point.x;
			yPoints[i] = point.y;
		}
		Polygon p = new Polygon(xPoints, yPoints, pList.size());
		return p;
	}
}
