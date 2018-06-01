/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.List;

public class GisOverlayGeometry implements Serializable{
	private static final long serialVersionUID = -1068754636605905622L;
	private int geometryId;
	private List<GisOverlayPolygon> polygons;
	private transient Rectangle2D.Double bounds;

	public GisOverlayGeometry(int geometryId, List<GisOverlayPolygon> polygons){
		this.polygons = polygons;
	}

	public int getGeometryId(){
		return geometryId;
	}

	public void setGeometryId(int geometryId){
		this.geometryId = geometryId;
	}

	public List<GisOverlayPolygon> getPolygons(){
		return polygons;
	}

	public void setPolygons(List<GisOverlayPolygon> polygons){
		this.polygons = polygons;
	}

	public Rectangle2D.Double getBounds(){
		return bounds;
	}

	public void setBounds(Rectangle2D.Double bounds){
		this.bounds = bounds;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((polygons == null) ? 0 : polygons.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GisOverlayGeometry other = (GisOverlayGeometry) obj;
		if (polygons == null){
			if (other.polygons != null)
				return false;
		} else if (!polygons.equals(other.polygons))
			return false;
		return true;
	}
}
