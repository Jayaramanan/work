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

public class GISPolygon implements Serializable{
	private static final long serialVersionUID = 5563606713464927001L;

	private int gisId;
	private List<Point2D.Double> points;
	private List<List<Point2D.Double>> exclusions;

	public GISPolygon(final int gisId){
		this.gisId = gisId;
		exclusions = new ArrayList<List<Point2D.Double>>();
	}

	public GISPolygon(final int gisId, final List<Point2D.Double> points){
		this(gisId);
		this.points = points;
	}

	public void addExclusion(final List<Point2D.Double> exclusion){
		exclusions.add(exclusion);
	}

	public List<List<Point2D.Double>> getExclusions(){
		return exclusions;
	}

	public int getGisId(){
		return gisId;
	}

	public List<Point2D.Double> getPoints(){
		return points;
	}

	public void setGisId(final int gisId){
		this.gisId = gisId;
	}

	public void setPoints(final List<Point2D.Double> points){
		this.points = points;
	}

	private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException{
		gisId = in.readInt();

		final int pointsSize = in.readInt();
		points = new ArrayList<Point2D.Double>(pointsSize);
		for (int i = 0; i < pointsSize; i++){
			final double x = in.readDouble();
			final double y = in.readDouble();
			points.add(new Point2D.Double(x, y));
		}

		final int exclusionsSize = in.readInt();
		exclusions = new ArrayList<List<Point2D.Double>>(exclusionsSize);
		for (int i = 0; i < exclusionsSize; i++){
			final int exclusionPointsSize = in.readInt();
			final List<Point2D.Double> exclusionPoints = new ArrayList<Point2D.Double>(exclusionPointsSize);
			for (int j = 0; j < exclusionPointsSize; j++){
				final double x = in.readDouble();
				final double y = in.readDouble();
				exclusionPoints.add(new Point2D.Double(x, y));
			}
			exclusions.add(exclusionPoints);
		}

	}

	private void writeObject(final ObjectOutputStream out) throws IOException{
		out.writeInt(gisId);

		out.writeInt(points.size());
		for (Point2D.Double point : points){
			out.writeDouble(point.x);
			out.writeDouble(point.y);
		}

		out.writeInt(exclusions.size());
		for (List<Point2D.Double> exclusionPoints : exclusions){
			out.writeInt(exclusionPoints.size());
			for (Point2D.Double point : exclusionPoints){
				out.writeDouble(point.x);
				out.writeDouble(point.y);
			}
		}
	}

	@Override
	public String toString(){
		return "GISPolygon [gisId=" + gisId + ", points=" + points + ", exclusions=" + exclusions + "]";
	}

}
