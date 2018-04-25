package com.ni3.ag.navigator.client.gui.graph;

import java.util.*;
import java.util.List;
import java.awt.*;

public class ConvexHull{

	private List<Point> points;

	public ConvexHull(List<Point> points){
		this.points = points;
	}

	public List<Point> getPoints(){
		return points;
	}

	public Polygon getConvexHull(){
		Stack<Point> vertices = new Stack<Point>();
		List<Point> candidates = new ArrayList<Point>(getPoints());

		if (!candidates.isEmpty()){
			Point bottom = lowestVertex(candidates);
			vertices.push(bottom);
			candidates.remove(bottom);
			Collections.sort(candidates, PointComparator.CLOCKWISE_ORDER(bottom));
			Collections.reverse(candidates);
			Iterator<Point> i = candidates.iterator();

			if (i.hasNext()){
				Point p1 = i.next();
				vertices.push(p1);
			}

			if (i.hasNext()){
				Point p2 = i.next();
				vertices.push(p2);
			}

			while (i.hasNext()){
				Point next = i.next();
				boolean rightTurn;

				do{
					int size = vertices.size();

					Point top = vertices.elementAt(size - 1);
					Point ntop = vertices.elementAt(size - 2);

					rightTurn = (BasicTests.turns(ntop, top, next) == BasicTests.RIGHT);
					if (rightTurn){
						vertices.pop();
					}

				} while (rightTurn);

				vertices.push(next);
			}
		}

		return makePolygon(vertices);
	}

	private Polygon makePolygon(Stack<Point> vertices){
		int[] x = new int[vertices.size()];
		int[] y = new int[vertices.size()];
		for (int i = 0; i < vertices.size(); i++){
			x[i] = vertices.get(i).x;
			y[i] = vertices.get(i).y;
		}
		return new Polygon(x, y, vertices.size());
	}

	private Point lowestVertex(List<Point> candidates){
		Iterator<Point> i = candidates.iterator();
		Point lowest = i.next();

		while (i.hasNext()){
			Point p = i.next();
			if (PointComparator.Y_ORDER.compare(lowest, p) == -1)
				lowest = p;
		}

		return lowest;
	}

	private static class PointComparator implements Comparator<Point>{

		private int order;
		private Point origin;

		private static PointComparator Y_ORDER = new PointComparator(1);

		public static PointComparator CLOCKWISE_ORDER(Point origin){
			return new PointComparator(2, origin);
		}

		private PointComparator(int order){
			this.order = order;
		}

		private PointComparator(int order, Point origin){
			this.order = order;
			this.origin = (origin != null) ? origin : new Point(0, 0);
		}

		public int compare(Point p1, Point p2){
			switch (order){
				case 1: // Y_ORDER
					return yCompare(p1, p2);

				case 2: // CLOCKWISE_ORDER
					return clockwiseCompare(p1, p2);

				default: // impossible
					throw new IllegalArgumentException("Not a legal order");
			}
		}

		private int yCompare(Point p1, Point p2){
			if (p1.getY() < p2.getY()) return -1;
			if (p1.getY() > p2.getY()) return 1;

			// p1 and p2 have the same y-coordinate
			if (p1.getX() < p2.getX()) return -1;
			if (p1.getX() > p2.getX()) return 1;

			return 0;
		}

		public int distance2(Point p1, Point p2){
			int dx = (int) (p1.getX() - p2.getX());
			int dy = (int) (p1.getY() - p2.getY());

			return dx * dx + dy * dy;
		}

		private int clockwiseCompare(Point p1, Point p2){

			int turn = BasicTests.turns(origin, p1, p2);

			if (turn == BasicTests.RIGHT) return -1;
			if (turn == BasicTests.LEFT) return 1;

			// p1 and p2 are collinear
			int d1 = distance2(origin, p1);
			int d2 = distance2(origin, p2);

			if (d1 < d2) return -1;
			if (d1 > d2) return 1;

			return 0;
		}

	} // PointComparator

	private static class BasicTests{

		public static final int COLLINEAR = 0;
		public static final int LEFT = 1;
		public static final int RIGHT = 2;

		private BasicTests(){
		}

		public static int crossProduct(int x0, int y0, int x1, int y1){
			return -x0 * y1 + y0 * x1;
		}

		public static int crossProduct(Point p0, Point p1){
			return crossProduct(p0.x, p0.y, p1.x, p1.y);
		}

		public static Point sub(Point p0, Point p1){
			int x = (int) (p0.getX() - p1.getX());
			int y = (int) (p0.getY() - p1.getY());

			return new Point(x, y);
		}

		public static int turns(Point p0, Point p1, Point p2){
			int c = crossProduct(sub(p1, p0), sub(p2, p0));

			if (c > 0)
				return LEFT;
			else if (c < 0)
				return RIGHT;
			else // c == 0
				return COLLINEAR;
		}

	} // BasicTests
}
