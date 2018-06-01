/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

public class GraphGeometry{
	// ********************************************************************************
	// line equasion is coef[0]*X + coef[1]*Y + coef[2] = 0
	public static void lineCoef(double x1, double y1, double x2, double y2, double[] coef){
		if (x1 == x2){ // vertical
			coef[0] = 1;
			coef[1] = 0;
			coef[2] = x1;
		} else if (y1 == y2){ // horizontal
			coef[0] = 0;
			coef[1] = 1;
			coef[2] = y1;
		} else{
			coef[0] = 1 / (x2 - x1);
			coef[1] = 1 / (y1 - y2);
			coef[2] = y1 / (y2 - y1) - x1 / (x2 - x1);

			coef[1] /= coef[0];
			coef[2] /= coef[0];
			coef[0] = 1;
		}
	}

	// ********************************************************************************
	public static boolean inSegment(double x1, double y1, double x2, double y2, double[] p){
		double c = 0.01, t;
		if (x1 > x2){
			t = x1;
			x1 = x2;
			x2 = t;
		}
		if (y1 > y2){
			t = y1;
			y1 = y2;
			y2 = t;
		}

		boolean res = false;
		if (x2 - x1 > 4 * c && p[0] - x1 > c && x2 - p[0] > c)
			res = true;

		if (y2 - y1 > 4 * c && p[1] - y1 > c && y2 - p[1] > c)
			res = true;

		return res;
	}

	// ********************************************************************************
	public static void intersect(double[] l1, double[] l2, double[] p){
		if (l1[0] == 0){
			p[1] = -l1[2];
			p[0] = -(l2[1] * p[1] + l2[2]);
		} else if (l2[0] == 0){
			p[1] = -l2[2];
			p[0] = -(l1[1] * p[1] + l1[2]);
		} else{
			p[1] = -(l1[2] - l2[2]) / (l1[1] - l2[1]);
			p[0] = -(l1[1] * p[1] + l1[2]);
		}
	}

	// ********************************************************************************
	public static void perpend(double[] line, double[] point, double[] perp){
		// perp line eq: Bx - Ay + D = 0, where the original one is Ax + By + C
		// = 0
		perp[0] = line[1];
		perp[1] = -line[0];
		perp[2] = -(perp[0] * point[0] + perp[1] * point[1]);
	}

	// ********************************************************************************
	public static double distance(double[] p1, double[] p2){
		double x = p1[0] - p2[0], y = p1[1] - p2[1];
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculate the mid point of a quad curve
	 * 
	 * @param curve
	 *            the cubic curve, we only need start- and endpoint.
	 * @param control
	 *            is the control point of the quad curve.
	 */
	public static Point2D quadMidPoint(CubicCurve2D curve, Point2D control){
		double x = curve.getX1() * 0.25 + control.getX() * 0.5 + curve.getX2() * 0.25;
		double y = curve.getY1() * 0.25 + control.getY() * 0.5 + curve.getY2() * 0.25;

		return new Point2D.Double(x, y);
	}

	/**
	 * Returns the distance of p3 to the segment defined by p1,p2;
	 * 
	 * @param p1
	 *            First point of the segment
	 * @param p2
	 *            Second point of the segment
	 * @param p3
	 *            Point to which we want to know the distance of the segment defined by p1,p2
	 * @return The distance of p3 to the segment defined by p1,p2
	 */
	public static double distanceToSegment(double p1x, double p1y, double p2x, double p2y, double p3x, double p3y){
		double xDelta = p2x - p1x;
		double yDelta = p2y - p1y;

		if ((xDelta == 0) && (yDelta == 0)){
			return 1000000.0;
		}

		double u = ((p3x - p1x) * xDelta + (p3y - p1y) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

		if (u < 0){
			return 1000000.0;
		} else if (u > 1){
			return 1000000.0;
		}

		xDelta = p1x + u * xDelta - p3x;
		yDelta = p1y + u * yDelta - p3y;
		return Math.sqrt(xDelta * xDelta + yDelta * yDelta);
	}

}
