/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class GisThematicGeometry{
	private int geometryId;
	private List<GISPolygon> polygons;
	private Color color;
	private Rectangle2D.Double bounds;

	public GisThematicGeometry(int geometryId, List<GISPolygon> polygons){
		super();
		this.geometryId = geometryId;
		this.polygons = polygons;
	}

	public int getGeometryId(){
		return geometryId;
	}

	public void setGeometryId(int geometryId){
		this.geometryId = geometryId;
	}

	public List<GISPolygon> getPolygons(){
		return polygons;
	}

	public void setPolygons(List<GISPolygon> polygons){
		this.polygons = polygons;
	}

	public Color getColor(){
		return color;
	}

	public void setColor(Color color){
		this.color = color;
	}

	public Rectangle2D.Double getBounds(){
		return bounds;
	}

	public void setBounds(Rectangle2D.Double bounds){
		this.bounds = bounds;
	}

}
