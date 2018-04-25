/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.graph;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.controller.HistoryManager.HistoryItem;
import com.ni3.ag.navigator.client.controller.search.SearchController;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.domain.Value;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.common.Ni3FileChooser;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.model.SystemGlobals;

public class GraphController{
	private static final Logger log = Logger.getLogger(GraphController.class);
	private MainPanel mainFrame;
	private Ni3Document doc;
	private GraphGateway graphGateway;

	public GraphController(MainPanel mainFrame){
		this.mainFrame = mainFrame;
		this.doc = mainFrame.Doc;
		graphGateway = new HttpGraphGatewayImpl();
	}

	public void setGraph(final HistoryItem hi, final boolean executeQueries, final boolean fireEvents){

		doc.setQueryStack(hi.getQueries());
		doc.Subgraph = hi.getGraph();
		doc.DS = hi.getDS();
		doc.setGraphPanelSettings(hi.getGpset());
		doc.setFilter(hi.getFilter());
		doc.DB.setDataFilter(hi.getPrefilter());
		doc.setCommandPanelSettings(hi.getCommandPanelSettings());

		doc.setMetaphorSet(hi.getMetaphorSet());

		final SearchController searchController = new SearchController(doc);
		if (executeQueries){
			boolean firstSearch = true;
			for (final Query q : hi.getQueries()){
				searchController.combineSearch(q, firstSearch, false);
				firstSearch = false;
			}
		}

		doc.setChartParams(hi.getChartParams());
		doc.setChart(hi.getChartID(), fireEvents, fireEvents);

		doc.clearThematicData();

		if (doc.getMapID() != hi.getMapID()){
			doc.setMap(hi.getMapID());
		}
		if (hi.getMapID() > 0 && hi.getThematicMapID() > 0){
			mainFrame.loadThematicDataSet(hi.getThematicMapID());
		} else{
			doc.setGeoLegendData(null, null, null);
		}

		doc.removeOverlays();
		if (hi.getOverlayIds() != null){
			for (Integer overlayId : hi.getOverlayIds()){
				doc.addOverlay(overlayId);
			}
		}

		if (fireEvents){
			try{
				doc.dispatchEvent(Ni3ItemListener.MSG_NewSubgraph, Ni3ItemListener.SRC_Doc, null, null);
			} catch (final Exception e){
				log.error(e);
			}
		}

		if (fireEvents){
			doc.DB.refreshDynamicAttributes(doc.Subgraph);
			doc.updateMapPosition(hi.getMapSettings());
		} else{
			doc.setMapSettings(hi.getMapSettings());
		}

		doc.setShowNumericMetaphors(hi.isShowNumericMetaphors());
		doc.setMatrixSort(hi.getMatrixSort(), true);
		doc.setPolygonModel(hi.getPolyModel());
	}

	public void removeNodeFromGraph(Node selectedNode){
		doc.setUndoRedoPoint(true);
		doc.Subgraph.removeRoot(selectedNode);
		doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(doc.Subgraph.getDataSet());
		doc.resetInPathEdges();
	}

	public void removeNodesFromGraph(List<Node> nodes){
		doc.setUndoRedoPoint(true);

		synchronized (doc.Subgraph){
			for (final Node n : nodes){
				doc.Subgraph.removeRoot(n);
			}
		}

		doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(doc.Subgraph.getDataSet());
		doc.resetInPathEdges();
	}

	public void removeNodeFromGraph(DBObject selectedNode){
		Node node = doc.Subgraph.findNode(selectedNode.getId());
		if (node != null){
			removeNodeFromGraph(node);
		}
	}

	public void removeSelectedNodesFromGraph(){
		List<Node> nodes = doc.Subgraph.getSelectedNodes();
		if (!nodes.isEmpty()){
			removeNodesFromGraph(nodes);
		}
	}

	public void removeNodesFromGraphByIds(List<Integer> nodeIds){
		List<Node> nodes = new ArrayList<Node>();
		for (final Integer nId : nodeIds){
			Node n = doc.Subgraph.findNode(nId);
			if (n != null){
				nodes.add(n);
			}
		}
		removeNodesFromGraph(nodes);
	}

	public void addNodeToGraph(final DBObject node, Point point){
		final List<Integer> nodeIds = new ArrayList<Integer>();
		nodeIds.add(node.getId());
		addNodesToGraph(nodeIds, point != null ? Arrays.asList(point) : null);
	}

	public void addNodesToGraph(final List<Integer> nodeIds, List<Point> points){
		doc.setUndoRedoPoint(true);

		final List<Node> nodes = graphGateway.getNodes(nodeIds, doc.SchemaID, doc.DB.getDataFilter());
		if (nodes == null){
			doc.undoredoManager.back();
		} else{
			int nodeCount = doc.Subgraph.getTotalNodeCount(nodes);
			if (nodeCount > doc.DB.getMaximumNodeCount()){
				log.warn("Too much nodes to add to the graph: " + nodeCount);
				showNodeLimitError();
				doc.undoredoManager.back();
			} else{
				for (int i = 0; i < nodes.size(); i++){
					Node node = nodes.get(i);
					final Point p;
					if (points != null && points.size() > i){
						p = points.get(i);
					} else{
						p = doc.getGraphVisualSettings().getRandomPoint();
					}
					node.setX(p.x);
					node.setY(p.y);

				}
				doc.showSubgraph(nodes, true);
			}
		}
	}

	public void contractOneLevel(){
		doc.resetInPathEdges();
		final List<Node> nodes = new ArrayList<Node>();

		final GraphCollection subgraph = doc.Subgraph;
		final int level = subgraph.getMaxExpandLevel();

		if (level <= 0){
			return;
		}

		doc.setUndoRedoPoint(true);

		for (final Node n : subgraph.getNodes()){
			if (n.getLevel() == level){
				nodes.add(n);
			} else if (n.getLevel() == level - 1){
				n.setExpandedManualy(false);
				n.setSelectiveExpandDataFilter(null);
			}
		}

		for (final Node n : nodes){
			synchronized (n.outEdges){
				subgraph.simpleRemoveNode(n);
			}
		}

		subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(subgraph.getDataSet());
	}

	public void contractNode(Node selectedNode){
		doc.setUndoRedoPoint(true);

		doc.resetInPathEdges();
		doc.Subgraph.contract(selectedNode);

		selectedNode.setExpandedManualy(false);

		doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(doc.Subgraph.getDataSet());
	}

	public boolean expandNodeOneLevel(final Node selected, boolean resetChartFilter, final boolean noLimit){
		final ArrayList<Integer> roots = new ArrayList<Integer>();
		roots.add(selected.Obj.getId());

		int level = selected.getLevel();

		expandNodesOneLevel(roots, resetChartFilter, level + 1, noLimit);

		return selected.isExpandedManualy();
	}

	public boolean expandNodesOneLevel(final List<Integer> roots, final boolean resetChartFilter, final int level,
			final boolean noLimit){
		if (roots.isEmpty())
			return true;

		doc.setUndoRedoPoint(true);

		final GraphCollection subgraph = doc.Subgraph;
		int maxNodeCount = noLimit ? 0 : doc.DB.getMaximumNodeCount();

		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, doc.SchemaID, doc.DB.getDataFilter(),
				maxNodeCount);
		if (graphObjects == null){
			doc.undoredoManager.back();
			return false;
		} else{
			int nodeCount = doc.Subgraph.getTotalNodeCount(graphObjects);
			if (nodeCount > doc.DB.getMaximumNodeCount()){
				log.warn("Too much nodes to add to the graph: " + nodeCount);
				showNodeLimitError();
				doc.undoredoManager.back();
				return false;
			}

			doc.showSubgraph(graphObjects, false, level);

			subgraph.setMultiEdgeIndexes();
			subgraph.MarkDegree();

			for (final Node n : subgraph.getNodes()){
				if (roots.contains(n.ID)){
					n.setExpandedManualy(true);
					n.setSelectiveExpandDataFilter(null);
				}

				if (n.getLevel() == level){
					subgraph.optimizeHugeNode(n);
				}
			}

			if (resetChartFilter){
				doc.filter.resetChartFilters();
			}
			subgraph.filter(doc.filter, doc.getFavoritesID());
			doc.setDataSet(subgraph.getDataSet());
		}

		return true;
	}

	public boolean expandOneLevel(final boolean resetChartFilter, final boolean noLimit){
		final ArrayList<Integer> roots = new ArrayList<Integer>();

		for (final Node n : doc.Subgraph.getNodes()){
			if (n.isActive() && n.getExternalRelatives() > 0 && n.getLevel() != -100){
				if (n.Obj != null){
					roots.add(n.Obj.getId());
				}
			}
		}

		if (!expandNodesOneLevel(roots, resetChartFilter, doc.Subgraph.getMaxExpandLevel() + 1, noLimit)){
			return false;
		}

		doc.Subgraph.ClearMarks();
		doc.Subgraph.MarkDegree();

		return true;
	}

	public void refocusNode(final int nodeID){
		doc.setUndoRedoPoint(true);

		final ArrayList<Integer> roots = new ArrayList<Integer>();
		roots.add(nodeID);

		doc.clearGraph(false, false);

		final List<Node> nodes = graphGateway.getNodes(roots, doc.SchemaID, doc.DB.getDataFilter());
		if (nodes == null){
			doc.undoredoManager.back();
		} else{
			final List<GraphObject> resNodes = doc.Subgraph.addResultToGraph(nodes);
			doc.DB.prepareSubgraph(doc.Subgraph, false);
			doc.Subgraph.setRootNodes(resNodes);

			expandOneLevel(false, false);

			moveSubgraphToCenter(nodeID);

			doc.clearPolyshapes();
		}
	}

	private void moveSubgraphToCenter(final int nodeID){
		Node node = doc.Subgraph.findNode(nodeID);
		Point center = doc.getGraphVisualSettings().getCenterPoint();
		int diffX = (int) (center.x - node.getX());
		int diffY = (int) (center.y - node.getY());

		for (Node n : doc.Subgraph.getNodes()){
			n.setX(n.getX() + diffX);
			n.setY(n.getY() + diffY);
		}
	}

	public void refocusNodeAsIs(final int nodeID){
		doc.setUndoRedoPoint(true);

		final GraphCollection subgraph = doc.Subgraph;
		subgraph.isolateBunch(nodeID);
		subgraph.changeRootNode(nodeID);

		subgraph.setExpandLevels();
		subgraph.ClearMarks();
		subgraph.MarkDegree();

		subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(subgraph.getDataSet());
	}

	public void selectiveExpand(final Node node, final DataFilter antifilter, final boolean fireEvent){
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(node);
		selectiveExpand(nodes, antifilter, fireEvent);
	}

	public void selectiveExpand(final List<Node> nodes, final DataFilter antifilter, final boolean fireEvent){

		GraphCollection bunch = new GraphCollection(false);
		List<Integer> roots = new ArrayList<Integer>();
		for (Node n : nodes){
			roots.add(n.ID);
		}

		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, doc.SchemaID, doc.DB.getDataFilter(), 0);

		if (graphObjects != null){
			bunch.addResultToGraph(graphObjects);
			doc.DB.prepareSubgraph(bunch, false);
		} else{
			return;
		}

		doc.setUndoRedoPoint(true);

		boolean expanded = false;
		final GraphCollection subgraph = doc.Subgraph;
		int newLevel = subgraph.getMaxExpandLevel() + 1;

		bunch.filterForExpand(antifilter);
		for (Node node : nodes){
			try{
				expanded |= subgraph.selectiveExpand(node, bunch, antifilter, doc.DB.getMaximumNodeCount(), newLevel);
			} catch (RuntimeException ignore){
				log.debug(ignore.getMessage());
				return;
			}
		}
		if (expanded){
			subgraph.setMultiEdgeIndexes();
			subgraph.MarkDegree();
		}

		if (fireEvent){
			subgraph.filter(doc.filter, doc.getFavoritesID());
			doc.setDataSet(subgraph.getDataSet());
		}
	}

	public static void showNodeLimitError(){
		SystemGlobals.MainFrame.Doc.setStatus(UserSettings.getWord("Too many nodes"));
		SystemGlobals.MainFrame.showNoResultWindow(MainPanel.TOO_MANY_SEARCH_RESULT);
	}

	public void selectiveExpandAll(DataFilter antifilter){
		final ArrayList<Node> nodes = new ArrayList<Node>();
		nodes.addAll(doc.Subgraph.getNodes());
		selectiveExpand(nodes, antifilter, true);
	}

	public void applyDataFilterToGraph(DataFilter dataFilter){
		List<Node> nodesToRemove = doc.Subgraph.getNodesToRemove(doc.Subgraph.getNodes(), dataFilter);
		if (!nodesToRemove.isEmpty()){
			removeNodesFromGraph(nodesToRemove);
		}
		List<Edge> edgesToRemove = doc.Subgraph.getEdgesToRemove(doc.Subgraph.getEdges(), dataFilter);
		if (!edgesToRemove.isEmpty()){
			for (Edge e : edgesToRemove){
				doc.Subgraph.removeEdge(e);
			}
		}

		List<Integer> nodeIds = doc.Subgraph.getNodeIds();
		if (!nodeIds.isEmpty()){
			GraphGateway graphGateway = new HttpGraphGatewayImpl();
			List<Node> nodes = graphGateway.getNodes(nodeIds, doc.SchemaID, dataFilter);
			doc.Subgraph.addResultToGraph(nodes);
		}
		doc.Subgraph.filter(doc.filter, doc.getFavoritesID());
		doc.setDataSet(doc.Subgraph.getDataSet());
	}

	public static ValueUsageStatistics calculateStatistics(List<GraphObject> objects, boolean forEdgesOnly){
		ValueUsageStatistics statistics = new ValueUsageStatistics();
		for (GraphObject go : objects){
			countUsagesByObject(statistics, go, forEdgesOnly);
		}
		return statistics;
	}

	private static void countUsagesByObject(ValueUsageStatistics statistics, GraphObject go, boolean forEdgesOnly){
		Entity entity = go.Obj.getEntity();
		if (entity.isNode() && forEdgesOnly)
			return;
		for (final Attribute a : entity.getReadableAttributes()){
			if (!a.predefined || (!a.inFilter && !a.containsChildPredefineds()))
				continue;
			Object value = go.Obj.getValue(a.ID);
			if (value == null)
				continue;
			if (a.multivalue){
				Value[] attributeValues = (Value[]) value;
				for (Value attributeValue : attributeValues){
					incrementValueUsage(statistics, go.isFilteredOut(), attributeValue);
				}
			} else{
				Value attributeValue = (Value) value;
				incrementValueUsage(statistics, go.isFilteredOut(), attributeValue);
			}
		}
	}

	private static void incrementValueUsage(ValueUsageStatistics statistics, boolean filteredOut, Value attributeValue){
		statistics.increment(attributeValue);
		if (!filteredOut)
			statistics.incrementDisplayed(attributeValue);
	}

	public void copySubgraphDataToClipboard(){
		String data = "";

		for (final Entity ent : doc.DB.schema.definitions){
			if (ent.CanRead && (ent.isNode() || ent.isEdge())){
				data += getSubgraphDataAsText(ent);
			}
		}

		final StringSelection stringSelection = new StringSelection(data);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, mainFrame);
	}

	public void exportDataAsCSV(){
		final Ni3FileChooser jfc = new Ni3FileChooser(UserSettings.getWord("Export data"));

		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int returnVal = jfc.showOpenDialog(mainFrame);
		String filename = "";
		if (returnVal == JFileChooser.APPROVE_OPTION){
			try{
				for (final Entity ent : doc.DB.schema.definitions){
					if (ent.CanRead && (ent.isNode() || ent.isEdge())){
						filename = jfc.getSelectedFile().getPath() + "/" + ent.Name + ".csv";
						final File f = new File(filename);

						if (f.exists()){
							final int ret = Ni3OptionPane.showConfirmDialog(Ni3.mainF, UserSettings
									.getWord("Do you want to replace existing file")
									+ " " + filename, UserSettings.getWord("Replace file confirmation"),
									JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

							if (ret != 0){
								continue;
							}
						}

						final BufferedWriter out = new BufferedWriter(new FileWriter(filename));
						exportSubgraphData(out, ent);
						out.close();
					}
				}
			} catch (final IOException e){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("Unable to write file ") + " " + filename,
						UserSettings.getWord("Export data"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void exportSubgraphData(final BufferedWriter out, final Entity ent){
		final String separator = "\t";
		final String nullValue = " ";
		try{
			out.write(ent.getCSV(separator));
			out.newLine();

			for (final Node n : doc.Subgraph.getNodes()){
				if (n.Obj != null && n.Obj.getEntity().ID == ent.ID && n.isActive()){
					out.write(getObjectValuesAsText(n.Obj, separator, nullValue));
					out.newLine();
				}
			}

			for (final Edge e : doc.Subgraph.getEdges()){
				if (e.Obj != null && e.Obj.getEntity().ID == ent.ID && e.isActive()){
					if (e.from.Obj != null){
						out.write(e.from.Obj.getLabel());
					} else{
						out.write(nullValue);
					}
					out.write(separator);

					if (e.to.Obj != null){
						out.write(e.to.Obj.getLabel());
					} else{
						out.write(nullValue);
					}
					out.write(separator);

					out.write(getObjectValuesAsText(e.Obj, separator, nullValue));
					out.newLine();
				}
			}
		} catch (final IOException e1){
			e1.printStackTrace();
		}

	}

	private String getSubgraphDataAsText(final Entity ent){
		final String separator = "\t";
		final String nullValue = " ";
		final String verticalSeparator = "\n";
		final StringBuilder ret = new StringBuilder();

		ret.append(ent.getCSV(separator));
		ret.append(verticalSeparator);

		for (final Node n : doc.Subgraph.getNodes()){
			if (n.Obj != null && n.Obj.getEntity().ID == ent.ID && n.isActive()){
				ret.append(getObjectValuesAsText(n.Obj, separator, nullValue));
				ret.append(verticalSeparator);
			}
		}

		for (final Edge e : doc.Subgraph.getEdges()){
			if (e.Obj != null && e.Obj.getEntity().ID == ent.ID && e.isActive()){
				if (e.from.Obj != null){
					ret.append(e.from.Obj.getLabel());
				} else{
					ret.append(nullValue);
				}
				ret.append(separator);

				if (e.to.Obj != null){
					ret.append(e.to.Obj.getLabel());
				} else{
					ret.append(nullValue);
				}
				ret.append(separator);

				ret.append(getObjectValuesAsText(e.Obj, separator, nullValue));
				ret.append(verticalSeparator);
			}
		}

		return ret.toString();
	}

	private String getObjectValuesAsText(DBObject obj, final String separator, final String nullValue){
		boolean first = true;

		final StringBuilder ret = new StringBuilder();

		for (final Attribute a : obj.getEntity().getReadableAttributes()){
			if (!a.inExport){
				continue;
			}
			if (!first){
				ret.append(separator);
			}
			first = false;

			String value = null;
			if (a.predefined && obj.getValue(a.ID) != null){
				if (a.multivalue){
					value = a.displayValue(obj.getValue(a.ID));
				} else{
					value = ((obj.getValue(a.ID))).toString();
				}
			} else{
				value = a.displayInCSV(obj.getValue(a.ID));
			}

			if (value == null || "null".equals(value) || value.isEmpty()){
				ret.append(nullValue);
			} else{
				ret.append(value);
			}

		}

		return ret.toString();
	}
}
