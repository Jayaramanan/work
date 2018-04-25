package com.ni3.ag.navigator.client.controller;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;

import com.ni3.ag.navigator.client.controller.favorites.FavoritesController;
import com.ni3.ag.navigator.client.controller.graph.GraphController;
import com.ni3.ag.navigator.client.controller.graph.ValueUsageStatistics;
import com.ni3.ag.navigator.client.gateway.GraphGateway;
import com.ni3.ag.navigator.client.gateway.impl.HttpGraphGatewayImpl;
import com.ni3.ag.navigator.client.gui.DlgImageSelector;
import com.ni3.ag.navigator.client.gui.DlgNodeProperties;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.client.gui.filter.DlgFilterTree;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.GraphObject;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;

public class ObjectPopupListener implements ActionListener{

	public MainPanel parentMP;
	private Ni3Document doc;
	private DlgImageSelector selector;
	private GraphController graphController;

	private Node selectedNode;
	private Edge selectedEdge;

	public ObjectPopupListener(MainPanel mainFrame){
		this.parentMP = mainFrame;
		this.doc = mainFrame.Doc;
		this.graphController = new GraphController(mainFrame);
	}

	public void setSelectedObject(GraphObject selectedObject){
		selectedEdge = null;
		selectedNode = null;
		if (selectedObject instanceof Node){
			selectedNode = (Node) selectedObject;
		} else if (selectedObject instanceof Edge){
			selectedEdge = (Edge) selectedObject;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e){
		final String command = e.getActionCommand();
		final Component c = (Component) e.getSource();
		final Point location = c.getLocation();

		if (command.length() < 2){
			return;
		}

		if ("*L".equals(command.substring(0, 2))){
			doc.Subgraph.getGraphLayoutManager().action(command.substring(2), selectedNode);
		} else if ("Remove".equals(command)){
			if (selectedNode != null){
				graphController.removeNodeFromGraph(selectedNode);
			}
		} else if ("Delete".equals(command)){
			if (selectedEdge != null){
				parentMP.edgeDelete(selectedEdge, false);
			} else if (selectedNode != null){
				parentMP.nodeDelete(selectedNode);
			}
		} else if ("Details".equals(command)){
			if (selectedEdge != null){
				parentMP.edgeDetails(selectedEdge);
			} else if (selectedNode != null){
				parentMP.nodeDetails(selectedNode);
			}
		} else if ("Edit".equals(command)){
			if (selectedEdge != null){
				parentMP.edgeEdit(selectedEdge);
			} else if (selectedNode != null){
				parentMP.nodeEdit(selectedNode);
			}
		} else if ("Replicate".equals(command)){
			if (selectedNode != null){
				parentMP.nodeReplicate(selectedNode);
			}
		} else if ("SelectiveExpand".equals(command)){
			selectiveExpand(selectedNode, location);
		} else if ("ImageSelector".equals(command)){
			if (selectedNode != null){
				if (selector != null){
					selector.clearFilter();
					selector.setCurrent(selectedNode);
				} else{
					selector = new DlgImageSelector(parentMP, selectedNode);
				}
				selector.setLocation(location);
				selector.setVisible(true);
			}
		} else if ("JumpToTopic".equals(command)){
			new FavoritesController(doc).loadDocument(selectedEdge.favoritesID, doc.SchemaID);
		} else if ("Expand".equals(command)){
			graphController.expandNodeOneLevel(selectedNode, false, false);
		} else if ("Contract".equals(command)){
			graphController.contractNode(selectedNode);
		} else if ("Refocus".equals(command)){
			graphController.refocusNode(selectedNode.ID);
		} else if ("RefocusAsIs".equals(command)){
			graphController.refocusNodeAsIs(selectedNode.ID);
		} else if ("Polygon".equals(command)){
			final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.getState())
				doc.addPolygonNode(selectedNode.ID);
			else
				doc.removePolygonNode(selectedNode.ID);
		} else if ("Polyline".equals(command)){
			final JCheckBoxMenuItem item = (JCheckBoxMenuItem) e.getSource();
			if (item.getState())
				doc.addPolylineNode(selectedNode.ID);
			else
				doc.removePolylineNode(selectedNode.ID);
		} else{
			MainPanel.showInBrowser(command);
		}
	}

	private void selectiveExpand(final Node selectedNode, Point location){
		final GraphCollection bunch = new GraphCollection(false);
		final ArrayList<Integer> roots = new ArrayList<Integer>();
		roots.add(selectedNode.ID);
		GraphGateway graphGateway = new HttpGraphGatewayImpl();
		List<GraphObject> graphObjects = graphGateway.getNodesAndEdges(roots, doc.SchemaID, doc.DB.getDataFilter(), 0);
		if (graphObjects != null){
			bunch.addResultToGraph(graphObjects);
			doc.DB.prepareSubgraph(bunch, false);

			bunch.subtract(doc.Subgraph);
			ValueUsageStatistics statistics = GraphController.calculateStatistics(bunch.getObjects(), true);
			final DlgFilterTree dlg = new DlgFilterTree(doc, location.x, location.y, false, true, false, false, selectedNode
					.getSelectiveExpandDataFilter(), statistics);
			dlg.setVisible(true);

			if (dlg.getReturnStatus() == DlgNodeProperties.RET_OK){
				graphController.selectiveExpand(selectedNode, dlg.antiFilter, true);
			}
		}
	}
}
