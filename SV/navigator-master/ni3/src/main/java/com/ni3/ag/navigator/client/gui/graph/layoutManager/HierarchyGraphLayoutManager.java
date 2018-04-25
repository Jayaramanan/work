/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.layoutManager.layoutManagerSettings.HierarchyManagerSettings;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;

public class HierarchyGraphLayoutManager extends DefaultGraphLayoutManager{
	public static final String NAME = "Hierarchy";

	private static final int HORIZONTAL_LAYOUT = 2;
	private static final int VERTICAL_LAYOUT = 1;
	private static final int DEFAULT_CELL_WIDTH = 80;
	private static final int DEFAULT_CELL_HEIGHT = 80;

	private int nodeCellWidth = 80;
	private int nodeCellHeight = 80;

	private HierarchyManagerSettings settings;

	public ArrayList<Value> hierarchyValues;

	private ArrayList<Edge> hierarchy;
	private ArrayList<Node> grid;
	private HashMap<Integer, Node> rootNodes;
	private MatrixSortOrder order = null;

	private boolean ZigZag[];

	public HierarchyGraphLayoutManager(){
		super();

		String hierarchies = UserSettings.getStringAppletProperty("Hierarchies", "");

		Value val;

		hierarchyValues = new ArrayList<Value>();

		StringTokenizerEx tok = new StringTokenizerEx(hierarchies, ";");
		while (tok.hasMoreTokens()){
			val = Schema.getValue(tok.nextIntegerToken());
			if (val != null){
				hierarchyValues.add(val);
			}
		}

		settings = new HierarchyManagerSettings();

		options = new String[3];

		options[0] = "Horisontal layout";
		options[1] = "Vertical layout";
		options[2] = "Auto";

		hierarchy = new ArrayList<Edge>();
		grid = new ArrayList<Node>();
		rootNodes = new HashMap<Integer, Node>();

		order = SystemGlobals.MainFrame.Doc.getMatrixSort();

		ZigZag = new boolean[100];
	}

	void separateGraph(GraphCollection graph, ArrayList<Value> val){
		hierarchy.clear();
		grid.clear();

		boolean valAdded;

		for (Edge e : graph.getEdges()){
			if (e.isDirected() && e.isActive()){
				valAdded = false;
				for (Value v : val){
					if (e.Obj.getEntity().ID == v.getAttribute().ent.ID && e.Obj.getValue(v.getAttribute().ID) == v){
						hierarchy.add(e);
						e.inHierarchy = true;
						e.edgeStyle = settings.hideHierarchyEdges() ? Edge.ES_None : Edge.ES_Ortho;
						valAdded = true;
						break;
					}
				}

				if (!valAdded){
					e.inHierarchy = false;
					e.edgeStyle = Edge.ES_Line;
				}
			} else{
				e.inHierarchy = false;
				e.edgeStyle = Edge.ES_Line;
			}

		}

		for (Node n : graph.getNodes()){
			n.ilayout1 = 0;
			n.layoutHierarchyLevel = -1; // Hierarchy level;
			n.layoutChildrenCount = 0; // Number of children in hierarchy
			n.setLayoutParent(-1); // parent
			if (n.isActive()){
				grid.add(n);
			}

			n.inHierarchy = false;
		}

		rootNodes.clear();

		for (Edge e : hierarchy)
			rootNodes.put(e.from.ID, e.from);

		for (Edge e : hierarchy)
			rootNodes.remove(e.to.ID);

		for (Node n : rootNodes.values()){
			n.layoutHierarchyLevel = 0;
			markLevel(n);
		}

		for (Edge e : hierarchy){
			grid.remove(e.from);
			grid.remove(e.to);

			e.from.inHierarchy = true;
			e.to.inHierarchy = true;

			e.from.layoutChildrenCount++;
		}

		order = SystemGlobals.MainFrame.Doc.getMatrixSort();

		Collections.sort(grid, new NodeComparator(order));
	}

	private void markLevel(Node n){
		for (Edge e : hierarchy)
			if (e.from.ID == n.ID && e.to.layoutHierarchyLevel == -1){
				e.to.layoutHierarchyLevel = n.layoutHierarchyLevel + 1;
				e.to.setLayoutParent(n.ID);
				markLevel(e.to);
			}
	}

	@Override
	public synchronized boolean doLayout(GraphCollection graph){
		separateGraph(graph, hierarchyValues);

		final List<Node> displayedNodes = graph.getDisplayedNodes();
		final int maxWidth = getMaxCellWidth(displayedNodes);
		final int maxHeight = getMaxCellHeight(displayedNodes);
		nodeCellWidth = Math.max(DEFAULT_CELL_WIDTH, maxWidth);
		nodeCellHeight = Math.max(DEFAULT_CELL_HEIGHT, maxHeight + 5);

		Dimension dim = SystemGlobals.MainFrame.Doc.getGraphVisualSettings().getCanvasSize();
		int x = (int) ((dim.width / 2) - (Math.sqrt(graph.getNodes().size()) * maxWidth * graph.NodeSpace / 2));
		int y = (int) ((dim.height / 2) - (Math.sqrt(graph.getNodes().size()) * maxHeight * graph.NodeSpace / 2));
		if (x < 0)
			x = 10;
		if (y < 0)
			y = 10;

		synchronized (graph.getNodes()){
			for (Node n : graph.getNodes()){
				n.setLayouted(false);
				n.ilayout2 = -1;
			}
		}

		for (int n = 0; n < ZigZag.length; n++)
			ZigZag[n] = false;

		synchronized (graph.getNodes()){
			for (Node n : rootNodes.values()){
				if (settings.isVerticalNode(n.ID))
					dim = layoutChildrenVerticaly(n, x, y, 0);
				else
					dim = layoutChildren(n, x, y, 0);

				x += dim.width;
			}
		}

		int gx, gy;
		int gxSz, gySz;
		int gridX, gridY;
		int gridPos;

		gridY = (int) Math.sqrt(grid.size());
		if (gridY != 0)
			gridX = (grid.size() / gridY) + 1;
		else
			gridX = 0;

		gx = x;
		gy = y;// gridY * gridY;

		gxSz = (int) ((maxWidth + 10) * graph.NodeSpace);
		gySz = (int) ((maxHeight + 10) * graph.NodeSpace);

		boolean gridZigZag = false;
		synchronized (graph.getNodes()){
			gridPos = 0;
			for (Node n : grid){
				n.setLayouted(true);
				n.setX(gx + gridPos % gridX * gxSz);
				n.setY(gy + gridPos / gridX * gySz);
				n.ZigZag = gridZigZag;
				gridZigZag = !gridZigZag;
				gridPos++;
			}
		}

		/*
		 * synchronized (graph.Roots) { for (Node root : graph.Roots) { if (root.isActive()) { if (root.ilayout3 == 1)
		 * layoutChildrenVerticaly(root, x, y); else x += layoutChildren(root, x, y); } } }
		 */
		synchronized (graph.getEdges()){
			for (Edge e1 : hierarchy){
				if (e1.edgeStyle == Edge.ES_Line){
					for (Edge e2 : hierarchy){
						if (e1.ID != e2.ID
								&& ((e1.from == e2.from && e1.to == e2.to) || (e1.from == e2.to && e1.to == e2.from))){
							if (e2.edgeStyle != Edge.ES_Line){
								e1.edgeStyle = e2.edgeStyle;
								break;
							}
						}
					}
				}
			}
		}

		return true;
	}

	private synchronized Dimension layoutChildren(Node root, int x, int y, int level){
		Dimension dim = new Dimension(getCellWidth(root, HORIZONTAL_LAYOUT), getCellHeight(root, HORIZONTAL_LAYOUT, level));
		root.ilayout1 = dim.width;

		root.setX(x + dim.width / 2);
		root.setY(y);
		root.setLayouted(true);

		if (dim.width > root.labelWidth){
			root.ZigZag = false;
			ZigZag[level] = true;
		} else{
			root.ZigZag = ZigZag[level];
			ZigZag[level] = !ZigZag[level];
		}

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.inHierarchy && e.to.getLayoutParent() == root.ID && e.to.isActive() && !e.to.isLayouted()
						&& e.isDirected()){
					e.edgeStyle = settings.hideHierarchyEdges() ? Edge.ES_None : Edge.ES_Ortho;

					Dimension d;
					if (settings.isVerticalNode(e.to.ID)){
						d = layoutChildrenVerticaly(e.to, x, y + nodeCellHeight, level + 1);
					} else{
						d = layoutChildren(e.to, x, y + nodeCellHeight, level + 1);
					}

					x += d.width;
					if (settings.isVerticalNode(root.ID)){
						y += d.height;
					}
				}
			}
		}

		return dim;
	}

	private synchronized int getCellWidth(Node root, int parentDirection){
		int w = 0, max = 0, res;

		int layoutDirection = 0;
		if (settings.isVerticalNode(root.ID))
			layoutDirection = VERTICAL_LAYOUT;
		else if (settings.isHorizontalNode(root.ID))
			layoutDirection = HORIZONTAL_LAYOUT;

		if (layoutDirection == 0)
			layoutDirection = parentDirection;

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.inHierarchy && e.to.getLayoutParent() == root.ID && e.to.isActive()
						&& (e.to.ilayout2 == -1 || e.to.ilayout2 == root.ID) && e.isDirected()){
					res = getCellWidth(e.to, layoutDirection);
					max = Math.max(res, max);
					w += res;
					e.to.ilayout2 = root.ID;
				}
			}
		}

		if (layoutDirection == VERTICAL_LAYOUT)
			return (int) (nodeCellWidth * SystemGlobals.MainFrame.Doc.Subgraph.NodeSpace + max);

		return Math.max(w, (int) (nodeCellWidth * SystemGlobals.MainFrame.Doc.Subgraph.NodeSpace));
	}

	private synchronized Dimension layoutChildrenVerticaly(Node root, int x, int y, int level){
		Dimension dim = new Dimension(getCellWidth(root, VERTICAL_LAYOUT), getCellHeight(root, VERTICAL_LAYOUT, level));
		root.ilayout1 = dim.height;

		root.setX(x);
		root.setY(y);
		root.setLayouted(true);

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.inHierarchy && e.to.isActive() && e.to.layoutHierarchyLevel == level + 1 && !e.to.isLayouted()
						&& e.isDirected()){
					e.edgeStyle = settings.hideHierarchyEdges() ? Edge.ES_None : Edge.ES_Angular;
					if (settings.isHorizontalNode(e.to.ID)){
						y += layoutChildren(e.to, x + nodeCellWidth, y + nodeCellHeight, level + 1).height;
					} else
						y += layoutChildrenVerticaly(e.to, x + nodeCellWidth, y + nodeCellHeight, level + 1).height;
				}
			}
		}

		return dim;
	}

	private synchronized int getCellHeight(Node root, int parentDirection, int level){
		int h = nodeCellHeight, max = 0, res;

		int layoutDirection = 0;
		if (settings.isVerticalNode(root.ID))
			layoutDirection = VERTICAL_LAYOUT;
		else if (settings.isHorizontalNode(root.ID))
			layoutDirection = HORIZONTAL_LAYOUT;
		if (layoutDirection == 0)
			layoutDirection = parentDirection;

		synchronized (root.outEdges){
			for (Edge e : root.outEdges){
				if (e.inHierarchy && e.to.isActive() && e.to.layoutHierarchyLevel == level + 1
						&& (e.to.ilayout2 == -1 || e.to.ilayout2 == root.ID) && e.isDirected()){
					res = getCellHeight(e.to, layoutDirection, level + 1);
					max = Math.max(res, max);
					h += res;
					e.to.ilayout2 = root.ID;
				}
			}
		}

		if (layoutDirection == HORIZONTAL_LAYOUT)
			return (int) (nodeCellHeight * SystemGlobals.MainFrame.Doc.Subgraph.NodeSpace) + max;

		return Math.max(h, (int) (nodeCellHeight * SystemGlobals.MainFrame.Doc.Subgraph.NodeSpace));
	}

	@Override
	public void initialize(GraphCollection graph){
		// TODO Auto-generated method stub

	}

	@Override
	public boolean needLayout(GraphCollection graph){
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void fromXML(NanoXML xml){
		settings.fromXML(xml);
	}

	@Override
	public void showPropertyDialog(JFrame parent){
		// TODO Auto-generated method stub

	}

	@Override
	public String toXML(){
		return settings.toXML();
	}

	@Override
	public void action(String option, GraphObject object){
		if (object instanceof Node){
			Node node = (Node) object;

			if (options[0].equals(option))
				settings.addHorizontalNode(node.ID);
			else if (options[1].equals(option))
				settings.addVerticalNode(node.ID);
			else if (options[2].equals(option))
				settings.setAutoNode(node.ID);
		}
	}

	@Override
	public void editSettings(){
		settings.editSettings();
	}

	@Override
	public void graphChanged(GraphCollection graph){
	}

	@Override
	public boolean isOptionEnabled(Node n, int index){
		return (n.layoutChildrenCount > 0);
	}

	@Override
	public boolean isOptionSelected(Node n, int index){
		switch (index){
			case 0:
				return settings.isHorizontalNode(n.ID);
			case 1:
				return settings.isVerticalNode(n.ID);
			case 2:
				return !settings.isHorizontalNode(n.ID) && !settings.isVerticalNode(n.ID);
			default:
				return false;
		}
	}

	@Override
	public String getName(){
		return NAME;
	}
}
