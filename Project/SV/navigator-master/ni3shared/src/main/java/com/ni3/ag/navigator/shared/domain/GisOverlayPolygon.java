/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.shared.domain;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * WARNING! WARNING! WARNING!
 * 
 * If you change structure of this class, you MUST also change readObject()/writeObject() methods!
 * 
 */

public class GisOverlayPolygon implements Serializable{
	private static final long serialVersionUID = 1803215634465706123L;

	private List<Point2D.Double> points;

	public GisOverlayPolygon(List<Point2D.Double> points){
		this.points = points;
	}

	public List<Point2D.Double> getPoints(){
		return points;
	}

	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		result = prime * result + ((points == null) ? 0 : points.hashCode());
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
		GisOverlayPolygon other = (GisOverlayPolygon) obj;
		if (points == null){
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		return true;
	}
	
	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException{
		final int size = in.readInt();
		points = new ArrayList<Point2D.Double>(size);
		for (int i = 0; i < size; i++){
			final double x = in.readDouble();
			final double y = in.readDouble();
			points.add(new Point2D.Double(x, y));
		}
	}

	private void writeObject(final ObjectOutputStream out) throws IOException{
		out.writeInt(points.size());
		for (Point2D.Double point : points){
			out.writeDouble(point.x);
			out.writeDouble(point.y);
		}
	}
}
