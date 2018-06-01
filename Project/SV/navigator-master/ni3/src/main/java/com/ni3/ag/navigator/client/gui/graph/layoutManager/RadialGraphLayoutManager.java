/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public class RadialGraphLayoutManager extends DefaultGraphLayoutManager{
	public static final String NAME = "Radial";
	private static final double DEFAULT_NODE_CELL_R = 80;

	private double r[];
	private double step[];

	private int maxDegree;

	private double nodeCellR;
	private double minDegreeDistance;

	private boolean upDown;

	public RadialGraphLayoutManager(){
		super();

		r = new double[101];
		step = new double[101];
	}

	@Override
	public synchronized boolean doLayout(GraphCollection graph){
		boolean allZeros = true;
		synchronized (graph.getNodes()){
			for (Node n : graph.getNodes()){
				if (n.getX() != 0 || n.getY() != 0)
					allZeros = false;
			}
		}

		if (allZeros)
			randomPlace(graph);

		upDown = true;

		double angle = 0;

		initialize(graph);

		synchronized (graph.getEdges()){
			for (Edge e : graph.getEdges())
				e.edgeStyle = Edge.ES_Line;
		}

		List<Node> roots;
		synchronized (graph.getRoots()){
			roots = getOrderedNodeArray(graph.getRoots());
		}

		for (Node root : roots){
			if (root.isActive())
				angle = layoutOnCircle(root, (int) root.getX(), (int) root.getY(), angle, false);
		}

		return true;
	}

	private List<Node> getOrderedNodeArray(Collection<Node> list){
		List<Node> res = new ArrayList<Node>(list);
		Collections.sort(res, new Comparator<Node>(){
			@Override
			public int compare(Node o1, Node o2){
				return new Integer(o2.degree).compareTo(o1.degree);
			}
		});

		return res;
	}

	private synchronized double layoutOnCircle(Node root, int cx, int cy, double startAngle, boolean moveRoot){
		double angle = startAngle;

		getCellWidthAngle(root);

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.to.isActive() && e.to.degree < 100 && !e.to.isLayouted() && e.to.degree == root.degree + 1){
					angle = layoutOnCircle(e.to, cx, cy, angle, true);
				}
			}
		}

		synchronized (root.outEdges){
			for (Edge e : root.inEdges){
				if (e.from.isActive() && e.from.degree < 100 && !e.from.isLayouted() && e.from.degree == root.degree + 1){
					angle = layoutOnCircle(e.from, cx, cy, angle, true);
				}
			}
		}

		if (moveRoot){
			root.setX(cx + (upDown && root.degree > 0 ? r[root.degree] : (r[root.degree] - nodeCellR / 2.0))
			        * Math.cos(startAngle));
			root.setY(cy + (upDown && root.degree > 0 ? r[root.degree] : (r[root.degree] - nodeCellR / 2.0))
			        * Math.sin(startAngle));
			upDown = !upDown;
		}

		root.setLayouted(true);

		return startAngle + step[root.degree];
	}

	private synchronized int getCountForDegree(List<Node> nodes, int degree){
		int count = 0;

		for (Node n : nodes)
			if (n.isActive() && n.degree == degree)
				count++;

		return count;
	}

	private double getCellWidthAngle(Node root){
		double w = 0;

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.to.isActive() && e.to.degree < 100
				        && (e.to.getLayoutParent() == -1 || e.to.getLayoutParent() == root.ID)
				        && (e.to.degree == root.degree + 1)){
					w += getCellWidthAngle(e.to);
					e.to.setLayoutParent(root.ID);
				}
			}
		}

		synchronized (root.inEdges){
			for (Edge e : root.inEdges){
				if (e.from.isActive() && e.from.degree < 100
				        && (e.from.getLayoutParent() == -1 || e.from.getLayoutParent() == root.ID)
				        && e.from.degree == root.degree + 1){
					w += getCellWidthAngle(e.from);
					e.from.setLayoutParent(root.ID);
				}
			}
		}

		return Math.max(w, step[root.degree]);
	}

	public void initialize(GraphCollection graph){
		maxDegree = 0;

		final List<Node> displayedNodes = graph.getDisplayedNodes();
		for (Node n : displayedNodes){
			n.setLayouted(false);
			n.setLayoutParent(-1);
			if (n.degree > maxDegree && n.degree < 100)
				maxDegree = n.degree;
		}

		final int maxWidth = getMaxCellWidth(displayedNodes) * 2;
		final int maxHeight = getMaxCellHeight(displayedNodes) * 2;
		nodeCellR = Math.max(DEFAULT_NODE_CELL_R, Math.max(maxWidth, maxHeight));
		minDegreeDistance = nodeCellR + 20;

		for (int i = 0; i <= maxDegree; i++){
			int count = getCountForDegree(displayedNodes, i);
			r[i] = Math.max((count * nodeCellR / ((i > 0) ? 2 : 1)) / (2 * Math.PI), minDegreeDistance);
			if (i > 0)
				r[i] = Math.max(r[i - 1] + minDegreeDistance, r[i]);

			step[i] = count == 0 ? 2 * Math.PI : 2 * Math.PI / count;
		}
	}

	@Override
	public boolean needLayout(GraphCollection graph){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void fromXML(NanoXML nextX){
		// TODO Auto-generated method stub

	}

	@Override
	public void showPropertyDialog(JFrame parent){
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void action(String option, GraphObject object){
		// TODO Auto-generated method stub

	}

	@Override
	public void editSettings(){
		// TODO Auto-generated method stub

	}

	@Override
	public void graphChanged(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOptionEnabled(Node n, int index){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName(){
		return NAME;
	}
}
