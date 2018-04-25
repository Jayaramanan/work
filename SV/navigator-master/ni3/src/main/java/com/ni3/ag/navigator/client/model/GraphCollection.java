/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.model;

import java.io.File;
import java.util.*;
import java.util.List;
import java.awt.*;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.XLSDataExporter;
import com.ni3.ag.navigator.client.controller.charts.SNA;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.graph.layoutManager.*;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;
import com.ni3.ag.navigator.shared.domain.FavoriteMode;
import com.ni3.ag.navigator.shared.util.StringTokenizerEx;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXML;
import com.ni3.ag.navigator.shared.util.nanoXML.NanoXMLAttribute;
import org.apache.log4j.Logger;

public class GraphCollection{
	private static final Logger log = Logger.getLogger(GraphCollection.class);
	private volatile List<GraphObject> Objects;
	private final Set<Node> Nodes;
	private final Set<Edge> Edges;
	private final Set<Node> Roots;

	public List<Integer> ExpandedManualy;

	public volatile int MaxDegree, MaxLevel;

	private Boolean leadingNodesOnly; // If this flag is true save will store only leading and manualy expanded nodes

	private GraphLayoutManager graphLayoutManager;
	private GraphLayoutManager previousLayout;

	public double NodeSpace; // Minimum space between two nodes for layout purposes

	public boolean layoutManagerChanged; // true If layout manager changed lookout of graph after last render
	private boolean callRecalculateStatistics;

	public GraphCollection(final GraphCollection g){
		this();
		callRecalculateStatistics = g.callRecalculateStatistics;
		graphLayoutManager = g.graphLayoutManager;
		MaxDegree = g.MaxDegree;

		Edge newEdge;
		synchronized (Edges){
			for (final Edge e : g.Edges){
				newEdge = new Edge(e);
				Edges.add(newEdge);
				Objects.add(newEdge);
			}
		}

		Node n1;
		Edge e1;
		for (final Node n : g.Nodes){
			n1 = new Node(n);
			Nodes.add(n1);
			Objects.add(n1);
			for (final Edge e : n.inEdges){
				e1 = findEdge(e.ID);
				if (e1 != null){
					n1.inEdges.add(e1);
					e1.to = n1;
				}
			}

			for (final Edge e : n.outEdges){
				e1 = findEdge(e.ID);
				if (e1 != null){
					n1.outEdges.add(e1);
					e1.from = n1;
				}
			}
		}

		for (final Node n : g.Roots){
			n1 = findNode(n.ID);
			if (n1 != null)
				Roots.add(n1);
		}

		NodeSpace = g.NodeSpace;

		layoutManagerChanged = false;
	}

	public GraphCollection(boolean callRecalculateStatistics){
		this();
		this.callRecalculateStatistics = callRecalculateStatistics;
		NodeSpace = 1.0;
		setGraphLayoutManager(SpringGraphLayoutManager.NAME);
		layoutManagerChanged = false;
	}

	GraphCollection(){
		Objects = Collections.synchronizedList(new ArrayList<GraphObject>());
		Nodes = Collections.synchronizedSet(new HashSet<Node> ());
		Roots = new HashSet<Node>();
		Edges = Collections.synchronizedSet(new HashSet<Edge> ());
		ExpandedManualy = new ArrayList<Integer>();
	}

	public Boolean isLeadingNodesOnly(){
		return leadingNodesOnly;
	}

	public Node addOrUpdateNode(Node node){
		Node result = findNode(node.ID);
		if (result != null){
			result.status = node.status;
			result.setChildrenCount(node.getChildrenCount());
			result.setFathersCount(node.getFathersCount());
		} else{
			result = addNode(node, null, 0);
		}
		return result;
	}

	public Edge addOrUpdateEdge(Edge edge, int newLevel){
		Edge result = findEdge(edge.ID);
		if (result != null){
			result.setConnectionType(edge.getConnectionType());
			result.setDirected(edge.getDirected());
			result.setStrength(edge.getStrength());
		} else{
			result = addEdge(edge, newLevel);
		}
		updateEdgeMetaphor(result);
		return result;
	}

	private Edge addEdge(Edge edge, int newLevel){
		Node from = findNode(edge.from.ID);
		Node to = findNode(edge.to.ID);

		edge.contracted = false;

		if (from == null){
			from = addNode(edge.from, to, newLevel);
		}

		if (to == null){
			to = addNode(edge.to, from, newLevel);
		}

		edge.from = from;
		edge.to = to;

		edge.from.outEdges.add(edge);
		edge.to.inEdges.add(edge);

		Edges.add(edge);
		Objects.add(edge);

		return edge;
	}

	private Node addNode(final Node n, final Node addNear, int newLevel){
		if (n.isLeading()){
			n.setLevel(0);
		} else if (newLevel > 0){
			n.setLevel(newLevel);
		}
		n.inEdges.clear();
		n.outEdges.clear();

		if (addNear != null){
			final int childrenIndex = addNear.outEdges.size() + addNear.inEdges.size();

			int indexCorrection;

			final int circleIndex = (int) ((Math.sqrt(4 * childrenIndex / 3) - 1) / 2.0);
			indexCorrection = (int) ((circleIndex + 1) * (circleIndex / 2.0));
			final int onCircle = (circleIndex + 1) * 6;

			final double dst = 60 * (circleIndex) + 60;

			double a;
			if (onCircle == 0){
				a = 0.0;
			} else{
				a = (2 * Math.PI / onCircle) * (childrenIndex - indexCorrection) + (2 * Math.PI / 50.0) * (circleIndex);
			}

			if (addNear.getX() == 0 || addNear.getY() == 0){
				Point p = SystemGlobals.MainFrame.Doc.getGraphVisualSettings().getRandomPoint();
				addNear.setX(p.x);
				addNear.setY(p.y);
			}
			n.setX(addNear.getX() + dst * Math.sin(a));
			n.setY(addNear.getY() + dst * Math.cos(a));
		} else if (n.getX() == 0 && n.getY() == 0){
			Point p = SystemGlobals.MainFrame.Doc.getGraphVisualSettings().getRandomPoint();
			n.setX(p.x);
			n.setY(p.y);
		}

		Nodes.add(n);
		Objects.add(n);
		return n;
	}

	public List<GraphObject> addResultToGraph(List<?> gObjects){
		return addResultToGraph(gObjects, 0);
	}

	public List<GraphObject> addResultToGraph(List<?> gObjects, int newLevel){
		List<GraphObject> result = new ArrayList<GraphObject>();
		for (Object gObject : gObjects){
			if (gObject instanceof Node){
				Node n = addOrUpdateNode((Node) gObject);
				result.add(n);
			} else if (gObject instanceof Edge){
				Edge e = addOrUpdateEdge((Edge) gObject, newLevel);
				result.add(e);
			}
		}
		return result;
	}

	public int getTotalNodeCount(List<?> gObjects){
		Set<Integer> set = new HashSet<Integer>();
		for (Node n : Nodes){
			set.add(n.ID);
		}
		for (Object gObject : gObjects){
			if (gObject instanceof Node){
				set.add(((Node) gObject).ID);
			} else if (gObject instanceof Edge){
				set.add(((Edge) gObject).from.ID);
				set.add(((Edge) gObject).to.ID);
			}
		}
		return set.size();
	}

	private void updateEdgeMetaphor(Edge edge){
		// TODO rewrite edge metaphor logics
		edge.resolveMetaphor(SystemGlobals.MainFrame.Doc.DB.schema.edgeMetaphor);
	}

	synchronized public List<Node> checkAccessRights(final List<DBObject> ds, final DataFilter prefilter){
		final List<Node> toDelete = new ArrayList<Node>();

		for (final Node n : Nodes){
			if (n.Obj != null && (!n.Obj.getEntity().CanRead || prefilter.isObjectFilteredOut(n.Obj))){
				toDelete.add(n);
			}
		}

		for (final Node n : toDelete){
			ds.remove(n.Obj);
			simpleRemoveNode(n);
		}

		final List<Edge> toDeleteEdge = new ArrayList<Edge>();

		for (final Edge e : Edges){
			if (e.Obj != null && (!e.Obj.getEntity().CanRead || prefilter.isObjectFilteredOut(e.Obj))){
				toDeleteEdge.add(e);
			}
		}

		for (final Edge e : toDeleteEdge){
			ds.remove(e.Obj);
			removeEdge(e);
		}
		return toDelete;
	}

	public List<Node> getNodesToRemove(final Collection<Node> nodes, final DataFilter prefilter){
		final List<Node> toDelete = new ArrayList<Node>();

		for (final Node n : nodes){
			if (n.Obj != null && (!n.Obj.getEntity().CanRead || prefilter.isObjectFilteredOut(n.Obj))){
				toDelete.add(n);
			}
		}

		return toDelete;
	}

	public List<Edge> getEdgesToRemove(final Collection<Edge> edges, final DataFilter prefilter){
		final List<Edge> toDelete = new ArrayList<Edge>();

		for (final Edge e : edges){
			if (e.Obj != null && (!e.Obj.getEntity().CanRead || prefilter.isObjectFilteredOut(e.Obj))){
				toDelete.add(e);
			}
		}

		return toDelete;
	}

	public void clear(){
		Objects.clear();
		synchronized (Nodes){
			Nodes.clear();
		}
		synchronized (Edges){
			Edges.clear();
		}
		Roots.clear();
	}

	public synchronized void ClearMarks(){
		for (final Node n : Nodes){
			n.setMarked(false);
		}
	}

	public void clearSelection(){
		SystemGlobals.MainFrame.Doc.clearPolyshapes();
		for (final Node n : Nodes){
			n.selectedTo = n.selectedFrom = n.selected = false;
		}
	}

	public synchronized int countSelected(){
		int ret = 0;
		for (final Node n : Nodes){
			if (n.selected){
				ret++;
			}
		}

		return ret;
	}

	public int countSelectedFrom(){
		int ret = 0;
		for (final Node n : Nodes){
			if (n.selectedFrom){
				ret++;
			}
		}

		return ret;
	}

	public int countSelectedTo(){
		int ret = 0;
		for (final Node n : Nodes){
			if (n.selectedTo){
				ret++;
			}
		}

		return ret;
	}

	public boolean doGraphLayout(){
		if (graphLayoutManager != null && graphLayoutManager.needLayout(this)){
			return graphLayoutManager.doLayout(this);
		}

		return false;
	}

	public void contract(final Node node){
		ClearMarks();
		MarkDegree();
		markSubgraph(node);

		node.contract(true);
		node.contracted = false;

		node.setSelectiveExpandDataFilter(null);

		final List<Node> toRemove = new ArrayList<Node>();

		for (final Node n : Nodes){
			if (n.contracted){
				toRemove.add(n);
			}
		}

		for (final Node n : toRemove){
			simpleRemoveNode(n);
		}

		final List<Edge> toRemoveE = new ArrayList<Edge>();

		for (final Edge e : Edges){
			if (e.contracted){
				toRemoveE.add(e);
			}
		}

		for (final Edge e : toRemoveE){
			removeEdge(e);
		}
	}

	public String exportDataAsXLS(final File file, final int schemaID){

		final StringBuilder nodeParam = new StringBuilder();
		boolean first = true;
		for (final Node n : Nodes){
			if (n.Obj != null && n.isActive()){
				if (!first){
					nodeParam.append(",");
				}
				nodeParam.append(n.ID);
				first = false;
			}
		}

		first = true;
		final StringBuilder edgeParam = new StringBuilder();
		for (final Edge e : Edges){
			if (e.Obj != null && e.isActive()){
				if (!first){
					edgeParam.append(",");
				}
				edgeParam.append(e.ID);
				first = false;
			}
		}

		if ("Nodes=".equals(nodeParam.toString())){
			return UserSettings.getWord("MsgNoDataFoundForExport");
		}

		final XLSDataExporter exporter = new XLSDataExporter();
		return exporter.exportData(file, schemaID, nodeParam.toString(), edgeParam.toString());
	}

	public synchronized List<DBObject> filter(final DataFilter filter, final int favoritesID){
		return filter(filter, favoritesID, true);
	}

	public synchronized List<DBObject> filter(final DataFilter filter, final int favoritesID, final boolean checkChartFilter){

		for (final Node n : Nodes){
			n.setFilteredOut(false);
			n.filteredOutByChartAF = false;
		}

		for (final Edge e : Edges){
			e.setFilteredOut(false);
		}

		if (filter.currentFavoritesonly){
			for (final Edge e : Edges){
				if (e.Obj != null && e.Obj.getEntity().isContextEdge()
						&& (e.favoritesID != 0 && e.favoritesID != favoritesID)){
					e.setFilteredOut(true);
				}
			}
		}

		if (!filter.FilterFrom){
			for (final Edge e : Edges){
				if (e.isDirected() && e.from.degree > e.to.degree){
					e.setFilteredOut(true);
				}
			}
		}

		if (!filter.FilterTo){
			for (final Edge e : Edges){
				if (e.isDirected() && e.to.degree > e.from.degree){
					e.setFilteredOut(true);
				}
			}
		}

		if (filter.topicMode == 0){
			for (final Edge e : Edges){
				if (e.Obj.getEntity().isContextEdge() && e.userID != SystemGlobals.getUserId()){
					e.setFilteredOut(true);
				}
			}
		}

		if (filter.topicMode == 1){
			for (final Edge e : Edges){
				if (e.Obj.getEntity().isContextEdge() && e.groupID != SystemGlobals.GroupID){
					e.setFilteredOut(true);
				}
			}
		}

		if (filter != null && !filter.filter.isEmpty()){
			for (final GraphObject go : Objects){
				if (go.Obj != null && !go.isFilteredOut() && go instanceof Edge
						|| (go instanceof Node && (!((Node) go).isLeading() || !filter.dontFilterFocusNodes))){
					boolean filteredOut = filter.isObjectFilteredOut(go.Obj);
					go.setFilteredOut(filteredOut);;
				}
			}
		}

		final int chartId = SystemGlobals.MainFrame.Doc.getCurrentChartId();

		for (final Node n : Nodes){
			if (checkChartFilter && n.isActive()){
				if (!(n.isLeading() && filter.dontFilterFocusNodes) && n.hasChart()){
					ChartFilter cf = filter.getChartFilter(chartId != SNA.SNA_CHART_ID ? n.Obj.getEntity().ID
							: Entity.COMMON_ENTITY_ID);
					n.recalculateGraphValues(filter);

					if ((n.chartTotalWithoutFiltering < cf.getMinChartVal() - 0.0005 || n.chartTotalWithoutFiltering > cf
							.getMaxChartVal() + 0.0005)){
						n.filteredOutByChartAF = true;
						n.setFilteredOut(true);
						continue;
					}

					if (n.isFilteredOut()){
						continue;
					}
				}

				if (filter.FilterEmptyCharts && n.hasChart() && n.getChartTotal() == 0){
					n.setFilteredOut(true);
				}
			}
		}

		MarkDegree();

		if (filter != null && filter.isNoOrphans()){
			while (propagateFilterOrphans()){
				;
			}
		}

		if (filter != null && filter.isConnectedOnly()){
			while (propagateFilterNonRelated(filter.dontFilterFocusNodes)){
				;
			}
		}

		if (filter.NoSingles){
			for (final Node n : Nodes){
				if (!n.isFilteredOut()){
					if (n.getExternalRelatives() == 0 && n.outEdges.size() + n.inEdges.size() == 1){
						n.setFilteredOut(true);
					}
				}
			}

		}

		if (filter.dontFilterFocusNodes){
			for (final Node n : Nodes){
				if (n.isLeading()){
					n.setFilteredOut(false);
				}
			}
		}

		for (final Edge e : Edges){
			if (e.from.isFilteredOut() || e.to.isFilteredOut()){
				e.setFilteredOut(true);
			}
		}

		final List<DBObject> ds = new ArrayList<DBObject>();
		for (final Node n : Nodes){
			if (!n.isFilteredOut() && n.isActive()){
				ds.add(n.Obj);
			}
		}

		if (callRecalculateStatistics)
			SystemGlobals.MainFrame.Doc.dispatchEvent(Ni3ItemListener.MSG_RecalculateStatistics, Ni3ItemListener.SRC_Doc,
					null, this);

		recalculateDynamicValues();

		return ds;
	}

	public synchronized void filterForExpand(final DataFilter filter){
		for (final GraphObject o : Objects){
			o.setFilteredOut(true);
		}

		for (final GraphObject o : Objects){
			for (final Value v : filter.filter.values()){
				if (v.getId() > 0 && o.Obj.getEntity().ID == v.getAttribute().ent.ID
						&& o.Obj.getValue(v.getAttribute().ID) != null){
					Object val = o.Obj.getValue(v.getAttribute().ID);
					if (v.getAttribute().multivalue){
						for (final Value vo : (Value[]) (val)){
							if (v.getId() == vo.getId()){
								o.setFilteredOut(false);
								break;
							}
						}

						if (!o.isFilteredOut()){
							break;
						}
					} else{
						if (((Value) (val)).getId() == v.getId()){
							o.setFilteredOut(false);

							break;
						}
					}
				}

				if (!o.isFilteredOut()){
					break;
				}
			}
		}

		for (final Node n : Nodes){
			if (!n.isFilteredOut()){
				for (final Edge e : n.inEdges){
					e.setFilteredOut(false);
				}

				for (final Edge e : n.outEdges){
					e.setFilteredOut(false);
				}
			}
		}

		for (final Edge e : Edges){
			if (!e.isFilteredOut()){
				e.from.setFilteredOut(false);
				e.to.setFilteredOut(false);
			}
		}
	}

	public Edge findEdge(final double x, final double y){
		for (final Edge e : Edges){
			if (e.isActive() && e.IsPointOnEdgeCenterPoint(x, y)){
				return e;
			}
		}

		for (final Edge e : Edges){
			if (e.isActive() && e.IsPointOnEdgeLine(x, y)){
				return e;
			}
		}

		return null;
	}

	public Edge findEdge(final int ID){
		for (final Edge e : Edges){
			if (e.ID == ID){
				return e;
			}
		}
		return null;
	}

	public GraphObject findGraphObject(final int ID){
		for (final GraphObject n : Objects){
			if (n.ID == ID){
				return n;
			}
		}
		return null;
	}

	public Node findNode(final int ID){
		for (final Node n : Nodes){
			if (n.ID == ID){
				return n;
			}
		}
		return null;
	}

	public List<Integer> getNodeIds(){
		List<Integer> nodeIds = new ArrayList<Integer>();
		for (Node n : Nodes){
			nodeIds.add(n.ID);
		}
		return nodeIds;
	}

	public List<Integer> getEdgeIds(){
		List<Integer> edgeIds = new ArrayList<Integer>();
		for (Edge e : Edges){
			edgeIds.add(e.ID);
		}
		return edgeIds;
	}

	public List<DBObject> getDataSet(){
		final List<DBObject> ret = new ArrayList<DBObject>();

		for (final Node n : Nodes){
			ret.add(n.Obj);
		}

		return ret;
	}

	public List<DBObject> getDisplayedDataSet(){
		final List<DBObject> ret = new ArrayList<DBObject>();

		for (final Node n : Nodes){
			if (n.isActive() && !n.filteredOutByChartAF){
				ret.add(n.Obj);
			}
		}

		return ret;
	}

	public List<Node> getDisplayedNodes(){
		List<Node> ret = new ArrayList<Node>();
		for (final Node n : Nodes){
			if (n.isActive() && !n.filteredOutByChartAF && n.Obj != null){
				ret.add(n);
			}
		}
		return ret;
	}

	public List<Edge> getDisplayedEdges(){
		List<Edge> ret = new ArrayList<Edge>();
		for (final Edge e : Edges){
			if (e.isActive() && e.Obj != null){
				ret.add(e);
			}
		}
		return ret;
	}

	public Node[] getFromNodes(){
		final int count = countSelectedFrom();
		int c;
		final Node ret[] = new Node[count];

		c = 0;
		for (final Node n : Nodes){
			if (n.selectedFrom){
				ret[c] = n;
				c++;
			}
		}

		return ret;
	}

	public int getMaxExpandLevel(){
		int ret = -1;
		for (final Node n : Nodes){
			if (n.getLevel() > ret){
				ret = n.getLevel();
			}
		}

		return ret;
	}

	public synchronized List<DBObject> getSelected(){
		final List<DBObject> ds = new ArrayList<DBObject>();
		for (final Node n : Nodes){
			if (n.selected){
				ds.add(n.Obj);
			}
		}

		return ds;
	}

	public synchronized List<Node> getSelectedNodes(){
		final List<Node> nodes = new ArrayList<Node>();
		for (final Node n : Nodes){
			if (n.selected){
				nodes.add(n);
			}
		}
		return nodes;
	}

	public Node[] getToNodes(){
		final int count = countSelectedTo();
		int c;
		final Node ret[] = new Node[count];

		c = 0;
		for (final Node n : Nodes){
			if (n.selectedTo){
				ret[c] = n;
				c++;
			}
		}

		return ret;
	}

	public synchronized void MarkDegree(){
		MaxDegree = 0;

		for (final Node n : Nodes){
			n.degree = 100;
		}

		for (final Node Root : Roots){
			Root.degree = 0;
		}

		for (final Node Root : Roots){
			MarkDegree(Root, 0, 1);
		}

		for (final Node n : Nodes){
			if (n.degree == 100){
				if (n.isExpandedManualy()){
					n.degree = 0;
					MarkDegree(n, 100, 1);
				}
				// else
				// n.degree = 1;
			}
		}
	}

	void MarkDegree(final Node node, final int Degree, final int Increment){
		if (node.degree >= Degree){
			if (Degree > MaxDegree){
				MaxDegree = Degree;
			}

			for (final Edge e : node.outEdges){
				if (e.isActive() && e.getMetaindex() >= 0 && e.to.degree > Degree){
					e.to.degree = Degree + Increment;
					MarkDegree(e.to, Degree + Increment, Increment);
				}
			}

			for (final Edge e : node.inEdges){
				if (e.isActive() && e.getMetaindex() >= 0 && e.from.degree > Degree){
					e.from.degree = Degree + Increment;
					MarkDegree(e.from, Degree + Increment, Increment);
				}
			}
		}
	}

	public synchronized void markSubgraph(final Node node){
		node.setMarked(true);

		for (final Edge e : node.outEdges){
			if (!e.to.isMarked() && e.to.degree > node.degree){
				markSubgraph(e.to);
			}
		}

		for (final Edge e : node.inEdges){
			if (!e.from.isMarked() && e.from.degree > node.degree){
				markSubgraph(e.from);
			}
		}
	}

	/**
	 * @param n
	 */
	public void optimizeHugeNode(final Node n){
		int childrenIndex = 0;

		if (n.outEdges.size() + n.inEdges.size() > 30){
			for (final Edge child : n.outEdges){
				if (child.to.isActive() && child.to.degree > n.degree){
					childrenIndex++;

					int indexCorrection;

					final int circleIndex = (int) ((Math.sqrt(4 * childrenIndex / 3) - 1) / 2.0);
					indexCorrection = (int) ((circleIndex + 1) * (circleIndex / 2.0));
					final int onCircle = (circleIndex + 1) * 6;

					final double dst = 60 * (circleIndex) + 60;

					double a;
					if (onCircle == 0){
						a = 0.0;
					} else{
						a = (2 * Math.PI / onCircle) * (childrenIndex - indexCorrection) + (2 * Math.PI / 50.0)
								* (circleIndex);
					}

					// double a = Math.random()*2*Math.PI;
					child.to.setX(n.getX() + dst * Math.sin(a));
					child.to.setY(n.getY() + dst * Math.cos(a));
				}
			}

			for (final Edge child : n.inEdges){
				if (child.from.isActive() && child.from.degree > n.degree){
					childrenIndex++;

					int indexCorrection;

					final int circleIndex = (int) ((Math.sqrt(4 * childrenIndex / 3) - 1) / 2.0);
					indexCorrection = (int) ((circleIndex + 1) * (circleIndex / 2.0));
					final int onCircle = (circleIndex + 1) * 6;

					final double dst = 60 * (circleIndex) + 60;

					double a;
					if (onCircle == 0){
						a = 0.0;
					} else{
						a = (2 * Math.PI / onCircle) * (childrenIndex - indexCorrection) + (2 * Math.PI / 50.0)
								* (circleIndex);
					}

					// double a = Math.random()*2*Math.PI;
					child.from.setX(n.getX() + dst * Math.sin(a));
					child.from.setY(n.getY() + dst * Math.cos(a));
				}
			}

		}
	}

	public boolean propagateFilterNonRelated(final boolean keepFocus){
		boolean ret = false;

		for (final Node n : Nodes){
			if (!n.isFilteredOut() && !(n.isLeading() && keepFocus)){
				boolean NonRelated = true;
				for (final Edge e : n.inEdges){
					if (!e.isFilteredOut()){
						NonRelated = false;
						break;
					}
				}

				if (NonRelated){
					for (final Edge e : n.outEdges){
						if (!e.isFilteredOut()){
							NonRelated = false;
							break;
						}
					}
				}

				if (NonRelated){
					n.setFilteredOut(true);
					ret = true;
				}
			}
		}

		for (final Edge e : Edges){
			if (!e.isFilteredOut() && (e.from.isFilteredOut() || e.to.isFilteredOut())){
				e.setFilteredOut(true);
				ret = true;
			}
		}

		return ret;
	}

	public boolean propagateFilterOrphans(){
		boolean ret = false;

		for (final Node n : Nodes){
			if (!n.isLeading() && !n.isFilteredOut()){
				boolean orphan = true;
				for (final Edge e : n.inEdges){
					if (!e.isFilteredOut() && e.from.degree < n.degree){
						orphan = false;
						break;
					}
				}

				if (orphan){
					for (final Edge e : n.outEdges){
						if (!e.isFilteredOut() && e.to.degree < n.degree){
							orphan = false;
							break;
						}
					}
				}

				if (orphan){
					n.setFilteredOut(true);
					ret = true;
				}
			}
		}

		for (final Edge e : Edges){
			if (!e.isFilteredOut() && (e.from.isFilteredOut() || e.to.isFilteredOut())){
				e.setFilteredOut(true);
				ret = true;
			}
		}

		return ret;
	}

	synchronized public void recalculateGraphValues(Map<Integer, ChartParams> paramsMap, final DataFilter filter,
			final boolean updateFilter){
		// to prevent ConcurrentModificationException make key set copy
		Integer[] entityIds = filter.getChartFilterEntities().toArray(new Integer[filter.getChartFilterEntities().size()]);
		for (Integer entityId : entityIds){
			double chartMinTotal = Integer.MAX_VALUE;
			double chartMaxTotal = Integer.MIN_VALUE;

			double chartValueMaxDiff = 0.0;
			for (final Node n : Nodes){
				if (!n.isActive() || (entityId != Entity.COMMON_ENTITY_ID && entityId != n.Obj.getEntity().ID)){
					continue;
				}
				if (!n.hasChart())
					continue;
				n.recalculateGraphValues(filter);
				if (n.getChartTotal() < chartMinTotal){
					chartMinTotal = n.getChartTotal();
				}
				if (n.getChartTotal() > chartMaxTotal){
					chartMaxTotal = n.getChartTotal();
				}
				ChartFilter cFilter = filter.getChartFilter(entityId);
				double chartMaxValue = 0.0;
				double chartMinValue = 0.0;
				for (int i = 0; i < n.getChartCount(); i++){
					double current = n.getChartValue(i);
					if (!cFilter.isExcluded(i) && current >= cFilter.getMinChartAttrVal(i) - 0.0005
							&& current <= cFilter.getMaxChartAttrVal(i) + 0.0005){
						if (current > chartMaxValue){
							chartMaxValue = current;
						}
						if (current < chartMinValue){
							chartMinValue = current;
						}
					}
				}
				final double diff = Math.abs(chartMaxValue - chartMinValue);
				if (diff > 0 && diff > chartValueMaxDiff){
					chartValueMaxDiff = diff;
				}
			}

			if (updateFilter){
				ChartFilter cFilter = filter.getChartFilter(entityId);
				cFilter.setMinChartVal(chartMinTotal);
				cFilter.setMaxChartVal(chartMaxTotal);
			}

			ChartParams chartParams = paramsMap.get(entityId);
			if (chartParams != null){
				chartParams.setCurrentGraphMinTotal(chartMinTotal);
				chartParams.setCurrentGraphMaxTotal(chartMaxTotal);
				chartParams.setCurrentGraphMaxValueDiff(chartValueMaxDiff);
			}
		}
	}

	public void recalculateChartData(Map<Integer, ChartParams> paramsMap){
		resetChart();

		for (Integer entityId : paramsMap.keySet()){
			final ChartParams params = paramsMap.get(entityId);

			for (Node node : Nodes){
				if (node.Obj.getEntity().ID == entityId){
					for (ChartAttributeDescriptor attr : params.getChartAttributes()){
						final Object value = node.Obj.getValue(attr.getAttribute().ID);
						double doubleVal = 0.0;
						if (value instanceof Double){
							doubleVal = (Double) value;
						} else if (value instanceof Value){
							try{
								doubleVal = Double.valueOf(((Value) value).getValue());
							} catch (NumberFormatException ex){
								log.warn("Cannot parse predefined value for chart: " + value);
							}
						} else if (value != null){
							doubleVal = Double.valueOf(value.toString());
						}
						node.addChartValue(doubleVal, attr.getColor());
						node.setChartTotal(node.getChartTotal() + doubleVal);
					}
				}
				node.chartTotalWithoutFiltering = node.getChartTotal();
			}
		}
	}

	public void recalculateMaxHaloR(){
		Node.maxGraphHaloR = Double.MIN_VALUE;
		Node.maxGraphHaloRadiusPerType.clear();

		synchronized (Nodes){
			for (final Node n : Nodes){
				final double r = n.getMaxDimension();
				if (r > Node.maxGraphHaloR){
					Node.maxGraphHaloR = r;
				}

				Double radiusForType = Node.maxGraphHaloRadiusPerType.get(n.Type);
				if (radiusForType == null){
					radiusForType = 0.0;
				}
				if (r > radiusForType){
					Node.maxGraphHaloRadiusPerType.put(n.Type, r);
				}
			}
		}
	}

	public synchronized List<DBObject> removeBySelection(final boolean SelectedStatus){
		final List<Node> toRemove = new ArrayList<Node>();
		final List<Edge> toRemoveE = new ArrayList<Edge>();

		final List<DBObject> ds = new ArrayList<DBObject>();
		for (final Node n : Nodes){
			if (n.selected != SelectedStatus){
				ds.add(n.Obj);
			} else{
				toRemove.add(n);
			}

			for (final Edge e : n.inEdges){
				if (((e.from.selected == SelectedStatus) || (e.to.selected == SelectedStatus))){
					Edges.remove(e);
					Objects.remove(e);
					if (n.selected != SelectedStatus){
						toRemoveE.add(e);
					}
				}
			}

			if (n.selected != SelectedStatus){
				for (final Edge e : toRemoveE){
					n.inEdges.remove(e);
				}

				toRemoveE.clear();
			}

			for (final Edge e : n.outEdges){
				if (((e.from.selected == SelectedStatus) || (e.to.selected == SelectedStatus))){
					Edges.remove(e);
					Objects.remove(e);
					if (n.selected != SelectedStatus){
						toRemoveE.add(e);
					}
				}
			}

			if (n.selected != SelectedStatus){
				for (final Edge e : toRemoveE){
					n.outEdges.remove(e);
				}

				toRemoveE.clear();
			}
		}

		for (final Node n : toRemove){
			Nodes.remove(n);
			Objects.remove(n);
			ds.remove(n.Obj);
		}

		return ds;
	}

	public synchronized void removeEdge(final Edge e){
		e.from.outEdges.remove(e);
		e.to.inEdges.remove(e);

		Edges.remove(e);
		Objects.remove(e);
	}

	public synchronized List<DBObject> removeRoot(final Node root){
		final int ID = root.ID;

		final List<DBObject> ds = new ArrayList<DBObject>();

		final List<Node> toDelete = new ArrayList<Node>();

		for (final Node n : Nodes){
			if (n == root || (n.removeRoot(ID) && n.getRoots().isEmpty())){
				for (final Edge e : n.inEdges){
					e.from.outEdges.remove(e);
					Edges.remove(e);
					Objects.remove(e);
				}
				for (final Edge e : n.outEdges){
					e.to.inEdges.remove(e);
					Edges.remove(e);
					Objects.remove(e);
				}
				Roots.remove(n);

				toDelete.add(n);
			} else{
				// if (!n.Active())
				ds.add(n.Obj);
			}
		}

		for (final Node n : toDelete){
			Nodes.remove(n);
			Objects.remove(n);
		}

		return ds;
	}

	public synchronized void removeNode(Node node){
		for (final Edge e : node.inEdges){
			e.from.outEdges.remove(e);
			Edges.remove(e);
			Objects.remove(e);
		}
		for (final Edge e : node.outEdges){
			e.to.inEdges.remove(e);
			Edges.remove(e);
			Objects.remove(e);
		}

		Roots.remove(node);
		Nodes.remove(node);
		Objects.remove(node);
	}

	public synchronized void resetChart(){
		for (final Node n : Nodes){
			n.resetChart();
		}
	}

	public boolean selectiveExpand(final Node selectedNode, final GraphCollection bunch, final DataFilter antifilter,
			int maxNodeCount, int newLevel){
		if (bunch == null){
			return false;
		}

		boolean expanded = false;

		selectedNode.setSelectiveExpandDataFilter(antifilter);

		int count = 0;
		for (final Node n : bunch.Nodes){
			if (n.isActive()){
				count++;
			}
		}

		if (count + Nodes.size() > maxNodeCount){
			GraphController.showNodeLimitError();
			throw new RuntimeException("Too many nodes to expand");
		}

		for (Edge edge : bunch.Edges){
			if (edge.isActive()){
				if (edge.from.ID == selectedNode.ID){
					edge = addOrUpdateEdge(edge, newLevel);
					expanded = true;
				} else if (edge.to.ID == selectedNode.ID){
					edge = addOrUpdateEdge(edge, newLevel);
					expanded = true;
				}
			}
		}

		if (expanded){
			selectedNode.setExpandedManualy(true);
		}
		return expanded;
	}

	public int setExpandLevels(){
		int ret = 0;
		int maxlevel;
		for (final Node n : Nodes){
			if (n.getLevel() < 0){
				maxlevel = -1;

				for (final Edge e : n.inEdges){
					if (e.from.getLevel() > maxlevel){
						maxlevel = e.from.getLevel();
					}
				}

				for (final Edge e : n.outEdges){
					if (e.to.getLevel() > maxlevel){
						maxlevel = e.to.getLevel();
					}
				}

				if (maxlevel > -1){
					n.setLevel(maxlevel + 1);
					ret++;
				}
			}
		}

		return ret;
	}

	public void setGraphLayoutManager(final String manager){
		if (SpringGraphLayoutManager.NAME.equals(manager)){
			graphLayoutManager = new SpringGraphLayoutManager();
		} else if (HierarchyGraphLayoutManager.NAME.equals(manager)){
			graphLayoutManager = new HierarchyGraphLayoutManager();
		} else if (RadialGraphLayoutManager.NAME.equals(manager)){
			graphLayoutManager = new RadialGraphLayoutManager();
		} else if (GridLayoutManager.NAME.equals(manager)){
			graphLayoutManager = new GridLayoutManager();
		} else if (LineGraphLayoutManager.NAME.equals(manager)){
			graphLayoutManager = new LineGraphLayoutManager();
		}

		layoutManagerChanged = true;
	}

	public void graphLayoutSettings(){
		graphLayoutManager.editSettings();
	}

	public void setMultiEdgeIndexes(){
		for (final Edge e : Edges){
			e.multiEdgeIndex = 0;
		}

		for (final Edge e1 : Edges){
			for (final Edge e2 : Edges){
				if (e1.ID == e2.ID){
					break;
				}

				if ((e1.from.ID == e2.from.ID && e1.to.ID == e2.to.ID) || (e1.from.ID == e2.to.ID && e1.to.ID == e2.from.ID)){
					if (e2.multiEdgeIndex == 0){
						e2.multiEdgeIndex = 2;
					} else{
						e2.multiEdgeIndex++;
					}

					if (e1.multiEdgeIndex == 0){
						e1.multiEdgeIndex = 1;
					}
				}
			}
		}
	}

	public void changeRootNode(final int ID){
		Roots.clear();
		for (Node node : Nodes){
			node.setLeading(false);
			node.setLevel(1);
			node.setExpandedManualy(false);
			node.setSelectiveExpandDataFilter(null);
		}
		final Node n = findNode(ID);
		if (n != null){
			setRootNode(n);
		}
	}

	public void setRootNode(final int ID, final double x, final double y){
		final Node n = findNode(ID);
		if (n != null){
			n.fixed = true;

			if (x != 0){
				n.setX(x);
				n.setY(y);
			}

			setRootNode(n);
		}
	}

	public void setRootNode(final Node n){
		n.setLevel(0);
		n.setLeading(true);
		if (!Roots.contains(n)){
			Roots.add(n);
		}
	}

	public void setRootNodes(List<GraphObject> gObjects){
		for (GraphObject go : gObjects){
			if (go instanceof Node){
				setRootNode((Node) go);
			} else if (go instanceof Edge){
				setRootNode(((Edge) go).from);
				setRootNode(((Edge) go).to);
			}
		}
	}

	public synchronized void simpleRemoveNode(final int NodeID){
		final Node n = findNode(NodeID);
		if (n != null){
			simpleRemoveNode(n);
		}
	}

	public synchronized void simpleRemoveEdge(Edge e){
		Node from = findNode(e.from.ID);
		if (from != null){
			// TODO make Edge overide equals ?? and use just remove
			for (Edge outEdge : from.outEdges){
				if (outEdge.ID == e.ID){
					from.outEdges.remove(outEdge);
					break;
				}
			}
			from.setChildrenCount(from.getChildrenCount() - 1);
		}
		Node to = findNode(e.to.ID);
		if (to != null){
			// TODO make Edge overide equals ?? and use just remove
			for (Edge inEdge : to.inEdges){
				if (inEdge.ID == e.ID){
					to.inEdges.remove(inEdge);
					break;
				}
			}
			to.fathersCount--;
		}
		Edges.remove(e);
		Objects.remove(e);
	}

	public synchronized void simpleRemoveNode(final Node node){
		if (Nodes.contains(node)){
			Roots.remove(node);

			while (node.inEdges.size() > 0){
				removeEdge(node.inEdges.get(0));
			}

			while (node.outEdges.size() > 0){
				removeEdge(node.outEdges.get(0));
			}

			Nodes.remove(node);
			Objects.remove(node);
		}
	}

	public int trimLooseEndsInPath(final List<DBObject> pathEnds){
		final ArrayList<Node> removeNodes = new ArrayList<Node>();

		for (final Edge e : Edges){
			if (SystemGlobals.MainFrame.Doc.getInPathEdges().contains(e)){
				if (e.from.degree != 0 && !pathEnds.contains(e.from.Obj)){
					if (e.from.outEdges.size() + e.from.inEdges.size() == 1 || e.from.haveMoreThanOneRelative()){
						removeNodes.add(e.from);
					}
				}

				if (e.to.degree != 0 && !pathEnds.contains(e.to.Obj)){
					if (e.to.outEdges.size() + e.to.inEdges.size() == 1 || e.to.haveMoreThanOneRelative()){
						removeNodes.add(e.to);
					}
				}
			}
		}

		for (final Node n : removeNodes){
			simpleRemoveNode(n);
		}

		return removeNodes.size();
	}

	public void isolateBunch(int nodeID){
		ArrayList<Edge> toRemoveEdges = new ArrayList<Edge>();
		ArrayList<Node> toRemoveNodes = new ArrayList<Node>();

		for (Node n : Nodes){
			if (nodeID != n.ID){
				toRemoveNodes.add(n);
			}
		}

		for (Edge e : Edges){
			if (e.from.ID != nodeID && e.to.ID != nodeID){
				toRemoveEdges.add(e);
			} else{
				toRemoveNodes.remove(e.from);
				toRemoveNodes.remove(e.to);
			}

		}

		for (Edge e : toRemoveEdges)
			removeEdge(e);

		for (Node n : toRemoveNodes)
			removeNode(n);
	}

	public void saveLayout(){
		previousLayout = graphLayoutManager;
		if (previousLayout == null)
			previousLayout = new SpringGraphLayoutManager();
	}

	public void restoreLayout(){
		if (previousLayout == null)
			return;
		graphLayoutManager = previousLayout;
		layoutManagerChanged = true;
		previousLayout = null;
		SystemGlobals.MainFrame.Doc.dispatchEvent(Ni3ItemListener.MSG_GraphLayoutManagerChanged, Ni3ItemListener.SRC_Graph,
				null, graphLayoutManager.getName());
	}

	public GraphLayoutManager getGraphLayoutManager(){
		return graphLayoutManager;
	}

	public void setAllNodesAsRoots(){
		for (final Node n : Nodes){
			setRootNode(n);
		}
	}

	public Collection<Edge> getEdges(){
		// TODO return Collections.unmodifiableList() - but remove all synchronization on tihs object
		return Edges;
	}

	public Collection<Node> getNodes(){
		// TODO return Collections.unmodifiableList() - but remove all synchronization on tihs object
		return Nodes;
	}

	public List<GraphObject> getObjects(){
		// TODO return Collections.unmodifiableList() - but remove all synchronization on tihs object
		return Objects;
	}

	public Collection<Node> getRoots(){
		// TODO return Collections.unmodifiableList() - but remove all synchronization on tihs object
		return Roots;
	}

	public void subtract(GraphCollection subGraph){
		for (GraphObject go : subGraph.getObjects()){
			Objects.remove(go);
		}
	}

	public void recalculateDynamicValues(){
		for (Node n : getNodes()){
			if (!n.Obj.getEntity().hasDynamicAttributes()){
				continue;
			}
			List<Attribute> dynamicAttributes = n.Obj.getEntity().getGraphDynamicAttributes();
			if (dynamicAttributes.isEmpty()){
				continue;
			}

			for (Attribute dAttr : dynamicAttributes){
				if (!n.isActive()){
					n.Obj.setValue(dAttr.ID, 0.0);
					continue;
				}
				final List<Double> relativeValues = calculateRelativeValues(n, dAttr);
				n.Obj.setValue(dAttr.ID, calculateDynamicValue(relativeValues, dAttr.getDynamicOperation()));
			}
		}
	}

	double calculateDynamicValue(List<Double> relativeValues, DynamicAttributeOperation operation){
		double result = 0.0;
		if (relativeValues.isEmpty()){
			return result;
		}
		switch (operation){
			case Sum:
			case Avg:
				for (Double value : relativeValues){
					result += value;
				}
				if (operation == DynamicAttributeOperation.Avg){
					result /= relativeValues.size();
				}
				break;
			case Min:
				result = Double.POSITIVE_INFINITY;
				for (Double value : relativeValues){
					if (value < result){
						result = value;
					}
				}
				break;
			case Max:
				result = Double.NEGATIVE_INFINITY;
				for (Double value : relativeValues){
					if (value > result){
						result = value;
					}
				}
				break;
		}

		return result;
	}

	List<Double> calculateRelativeValues(Node n, Attribute dAttr){
		List<Double> relativeValues = new ArrayList<Double>();
		Set<Integer> processedNodes = new HashSet<Integer>();
		final int dynynamicFromEntityId = dAttr.getDynamicFromEntity().ID;
		final Attribute dynamicFromAttribute = dAttr.getDynamicFromAttribute();
		for (Edge e : n.outEdges){
			if (e.isActive() && e.to.isActive()){
				if (e.Obj.getEntity().ID == dynynamicFromEntityId){
					relativeValues.add(e.Obj.getValueAsDouble(dynamicFromAttribute));
				} else if (e.to.Obj.getEntity().ID == dynynamicFromEntityId && !processedNodes.contains(e.to.Obj.getId())){
					relativeValues.add(e.to.Obj.getValueAsDouble(dynamicFromAttribute));
					processedNodes.add(e.to.Obj.getId());
				}
			}
		}

		for (Edge e : n.inEdges){
			if (e.isActive() && e.from.isActive()){
				if (e.Obj.getEntity().ID == dynynamicFromEntityId){
					relativeValues.add(e.Obj.getValueAsDouble(dynamicFromAttribute));
				} else if (e.from.Obj.getEntity().ID == dynynamicFromEntityId
						&& !processedNodes.contains(e.from.Obj.getId())){
					relativeValues.add(e.from.Obj.getValueAsDouble(dynamicFromAttribute));
					processedNodes.add(e.from.Obj.getId());
				}
			}
		}
		return relativeValues;
	}

	public void fromXML(final NanoXML xml){
		leadingNodesOnly = true;
		NanoXMLAttribute attr;

		while ((attr = xml.Tag.getNextAttribute()) != null){
			if ("nodeSpace".equals(attr.Name)){
				NodeSpace = Double.parseDouble(attr.Value);
			} else if ("layout".equals(attr.Name)){
				if (attr.Value.equals(LineGraphLayoutManager.NAME))
					saveLayout();
				setGraphLayoutManager(attr.Value);
				layoutManagerChanged = false;
			} else if ("MaxExpandLevel".equals(attr.Name)){
				MaxLevel = attr.getIntegerValue();
			} else if ("LeadingNodesOnly".equals(attr.Name)){
				leadingNodesOnly = attr.getBooleanValue();
			}
		}

		NanoXML nextX;
		while ((nextX = xml.getNextElement()) != null){
			if ("layoutSettings".equals(nextX.getName())){
				graphLayoutManager.fromXML(nextX);
			} else{
				if ("Edges".equals(nextX.getName())){
					NanoXML nextXX;
					while ((nextXX = nextX.getNextElement()) != null){
						int id = nextXX.Tag.getAttribute("ID").getIntegerValue();
						Edge edge = new Edge();
						edge.ID = id;
						edge.fromXML(nextXX);
						Edges.add(edge);
					}
				} else if ("Nodes".equals(nextX.getName())){
					NanoXML nextXX;
					while ((nextXX = nextX.getNextElement()) != null){
						int id = nextXX.Tag.getAttribute("ID").getIntegerValue();
						final Node node = new Node(10, 10);
						node.ID = id;
						node.fromXML(nextXX);
						Nodes.add(node);
					}
				} else if ("Roots".equals(nextX.getName())){
					attr = nextX.Tag.getAttribute("List");
					final StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
					while (tok.hasMoreTokens()){
						final int ID = Integer.valueOf(tok.nextToken());
						final Node node = findNode(ID);
						if (node != null){
							Roots.add(node);
						}
					}
				} else if ("ExpandedManualy".equals(nextX.getName())){
					attr = nextX.Tag.getAttribute("List");
					StringTokenizerEx tok = new StringTokenizerEx(attr.Value, ",", false);
					ExpandedManualy = new ArrayList<Integer>();
					while (tok.hasMoreTokens()){
						ExpandedManualy.add(tok.nextIntegerToken());
					}
				}
			}
		}
	}

	public String toXML(final FavoriteMode mode){
		final StringBuilder ret = new StringBuilder(65536);

		ret.append("<Graph nodeSpace='").append(NodeSpace).append("'");
		ret.append(" layout='").append(graphLayoutManager != null ? graphLayoutManager.getName() : "").append("'");
		ret.append(" MaxExpandLevel='");
		ret.append(getMaxExpandLevel()).append("' LeadingNodesOnly='false' EdgeCount='");

		// / ID Only
		ret.append(Edges.size()).append("' NodeCount='");
		ret.append(mode != FavoriteMode.QUERY ? Nodes.size() : 0).append("'>\n");

		boolean first = true;
		String graphLayoutManagerXML = graphLayoutManager.toXML();
		ret.append(graphLayoutManagerXML);
		ret.append("<Edges List='");
		if (mode != FavoriteMode.QUERY){
			for (final Edge e : Edges){
				if (e.Obj != null && !e.Obj.getEntity().isContextEdge()){
					if (!first){
						ret.append(",");
					}
					ret.append(e.ID);
					first = false;
				}
			}
		}

		ret.append("'>\n");

		// / Data
		if (mode != FavoriteMode.QUERY){
			for (final Edge e : Edges){
				ret.append(e.toXML());
			}
		}

		ret.append("</Edges>\n");

		first = true;
		ret.append("<Nodes List='");
		if (mode != FavoriteMode.QUERY){
			for (final Node n : Nodes){
				if (!first){
					ret.append(",");
				}
				ret.append(n.ID);
				first = false;
			}
		}
		ret.append("'>\n");

		if (mode != FavoriteMode.QUERY){
			for (final Node n : Nodes){
				ret.append(n.toXML());
			}
		}
		ret.append("</Nodes>\n");

		if (mode != FavoriteMode.QUERY){
			first = true;
			ret.append("<Roots List='");
			for (final Node n : Roots){
				if (!first){
					ret.append(",");
				}
				ret.append(n.ID);
				first = false;
			}
			ret.append("'/>\n");

			first = true;
			ret.append("<ExpandedManualy List='");
			for (final Node n : Nodes){
				if (n.isExpandedManualy()){
					if (!first){
						ret.append(",");
					}
					ret.append(n.ID);
					first = false;
				}
			}
			ret.append("'/>\n");
		}

		ret.append("</Graph>\n");
		return ret.toString();
	}
}
