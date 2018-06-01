/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.DBObject;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.NodeConnectionMergeRow;
import com.ni3.ag.navigator.client.domain.NodeMergeRow;
import com.ni3.ag.navigator.client.gui.datamerge.NodeConnectionMergeTableModel;
import com.ni3.ag.navigator.client.gui.datamerge.NodeMergeDialog;
import com.ni3.ag.navigator.client.gui.datamerge.NodeMergeTableModel;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;

public class NodeMerger{
	private NodeMergeDialog dlg;

	public NodeMerger(Node fromNode, Node toNode){
		dlg = new NodeMergeDialog();

		final List<NodeMergeRow> rows = fillMergeRows(fromNode.Obj, toNode.Obj);
		dlg.setTableModel(new NodeMergeTableModel(rows));
		final List<NodeConnectionMergeRow> cRows = fillConnectionRows(fromNode, toNode);
		dlg.setConnectionTableModel(new NodeConnectionMergeTableModel(cRows));
	}

	public void showDialog(){
		dlg.showIt();
	}

	private List<NodeMergeRow> fillMergeRows(DBObject from, DBObject to){
		final Entity entity = from.getEntity();
		final List<NodeMergeRow> rows = new ArrayList<NodeMergeRow>();

		for (Attribute attr : entity.getReadableAttributes()){
			if (!attr.isSystemAttribute() && !attr.isFormula() && !attr.inContext){
				final Object fromValue = from.getValue(attr.ID);
				final Object toValue = to.getValue(attr.ID);
				final boolean selected = (toValue == null && fromValue != null);
				final NodeMergeRow row = new NodeMergeRow(attr, fromValue, toValue, selected);
				rows.add(row);
			}
		}

		return rows;
	}

	private List<NodeConnectionMergeRow> fillConnectionRows(Node fromNode, Node toNode){
		List<NodeConnectionMergeRow> rows = new ArrayList<NodeConnectionMergeRow>();
		rows.addAll(addConnectionRows(fromNode, toNode.ID));
		rows.addAll(addConnectionRows(toNode, fromNode.ID));
		return rows;
	}

	private List<NodeConnectionMergeRow> addConnectionRows(Node node, int excludeId){
		final List<Edge> inEdges = node.inEdges;
		final List<Edge> outEdges = node.outEdges;

		List<NodeConnectionMergeRow> rows = new ArrayList<NodeConnectionMergeRow>();
		for (Edge edge : inEdges){
			if (edge.from.ID != excludeId){
				NodeConnectionMergeRow row = new NodeConnectionMergeRow(edge.Obj, edge.from.Obj, true);
				rows.add(row);
			}
		}

		for (Edge edge : outEdges){
			if (edge.to.ID != excludeId){
				NodeConnectionMergeRow row = new NodeConnectionMergeRow(edge.Obj, edge.to.Obj, true);
				rows.add(row);
			}
		}
		return rows;
	}

	public List<Integer> getAttributesToMerge(){
		List<Integer> attributes = new ArrayList<Integer>();

		NodeMergeTableModel model = dlg.getTableModel();
		List<NodeMergeRow> rows = model.getRows();

		for (NodeMergeRow row : rows){
			if (row.isSelected()){
				attributes.add(row.getAttribute().ID);
			}
		}
		return attributes;
	}

	public List<Integer> getConnectionsToMerge(){
		List<Integer> connections = new ArrayList<Integer>();

		NodeConnectionMergeTableModel model = dlg.getConnectionTableModel();
		List<NodeConnectionMergeRow> rows = model.getRows();

		for (NodeConnectionMergeRow row : rows){
			if (row.isSelected()){
				connections.add(row.getEdge().getId());
			}
		}
		return connections;
	}

	public boolean isOkPressed(){
		return dlg.isOkPressed();
	}

}
