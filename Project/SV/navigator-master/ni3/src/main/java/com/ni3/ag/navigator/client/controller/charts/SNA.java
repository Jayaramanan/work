/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.controller.charts;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import com.ni3.ag.navigator.client.controller.Ni3ItemListener;
import com.ni3.ag.navigator.client.domain.Attribute;
import com.ni3.ag.navigator.client.domain.ChartAttributeDescriptor;
import com.ni3.ag.navigator.client.domain.ChartParams;
import com.ni3.ag.navigator.client.domain.Entity;
import com.ni3.ag.navigator.client.domain.Schema;
import com.ni3.ag.navigator.client.gui.datalist.ItemsPanel;
import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.model.Ni3Document;
import com.ni3.ag.navigator.client.util.Utility;
import com.ni3.ag.navigator.shared.domain.ChartType;
import com.ni3.ag.navigator.shared.domain.DisplayOperation;

public class SNA implements Ni3ItemListener{
	public static final int SNA_CHART_ID = -2;
	public static int MaxNodes = 500;
	public static int MaxEdges = 2000;

	private Ni3Document doc;
	public boolean inProcess = false;
	public volatile List<Node> snaNodes = null;

	private List<ChartAttributeDescriptor> attributes;

	public enum SNAAttribute{
		DEGREE(-1, "Degree", "#FF00FF"), CLOSENESS(-2, "Closeness", "#FFFF00"), BETWEENNESS(-3, "Betweenness", "#FF0000"), EIGENVECTOR(
				-4, "Eigenvector", "#00FF00"), CLUSTERING(-5, "Clustering", "#0000FF");
		private String label;
		private String color;
		private int id;

		private SNAAttribute(int id, String label, String color){
			this.id = id;
			this.label = label;
			this.color = color;
		}

		public String getLabel(){
			return label;
		}

		String getColor(){
			return color;
		}

		public int getId(){
			return id;
		}
	}

	public SNA(Ni3Document doc){
		this.doc = doc;
	}

	public void doIt(){
		doc.registerListener(this);

		synchronized (doc.Subgraph){
			snaNodes = new ArrayList<Node>();

			for (Node n : doc.Subgraph.getNodes())
				if (!n.isFilteredOut()){
					n.betweenness = 0.0;
					n.closeness = 0.0;
					n.eigenvector = 0.0;
					n.degreeCentrality = 0;
					n.clustering = 0.0;
					snaNodes.add(n);
				}
			computeDegree();
			computeCloseness();
			computeBetweenness();
			computeEigenvector();
			computeClustering();

			addSnaAttributes();

			showChart();
		}
	}

	private void addSnaAttributes(){
		final Schema schema = doc.DB.schema;
		for (Entity entity : schema.definitions){
			if (entity.isNode() && !entity.hasSnaAttribute()){
				for (SNAAttribute sna : SNAAttribute.values()){
					entity.addAttribute(new Attribute(entity, sna));
				}
			}
		}
	}

	private void computeBetweenness(){
		inProcess = true;
		Stack<Node> S = new Stack<Node>();
		ArrayList<Node> P = new ArrayList<Node>();
		Queue<Node> Q = new LinkedList<Node>();
		ArrayList<Node> neighbor = new ArrayList<Node>();

		for (Node s : snaNodes){
			for (Node n : snaNodes){
				n.sigma = 0;
				n.d = -1;
				n.delta = 0.0;
			}
			s.sigma = 1;
			s.d = 0;

			Q.add(s);

			while (!Q.isEmpty()){
				Node v = Q.poll();
				S.push(v);
				neighbor.clear();
				for (Edge e : v.outEdges)
					if (!e.contracted && !e.isFilteredOut() && !neighbor.contains(e.to))
						neighbor.add(e.to);

				for (Edge e : v.inEdges)
					if (!e.contracted && !e.isFilteredOut() && !neighbor.contains(e.from))
						neighbor.add(e.from);

				for (Node w : neighbor){
					if (w.d < 0){
						Q.add(w);
						w.d = v.d + 1;
					}
					if (w.d == v.d + 1){
						w.sigma = w.sigma + v.sigma;
						P.add(v);
					}
				}
			}

			while (!S.isEmpty()){
				Node w = S.pop();
				for (Node v : P)
					v.delta = v.delta + (v.sigma / w.sigma) * (1 + w.delta);
				if (w.ID != s.ID)
					w.betweenness = w.betweenness + w.delta;
			}
			Q.clear();
			P.clear();
			S.clear();
		}
		// Normalize Betweenness
		double maxDegree = 0, maxVal = 0;
		for (Node n : snaNodes){
			if (maxVal < n.betweenness)
				maxVal = n.betweenness;
			if (maxDegree < n.degreeCentrality)
				maxDegree = n.degreeCentrality;
		}

		maxVal = Math.log(maxVal + 1);
		maxDegree = Math.floor(maxDegree / 100) * 100;
		if (maxDegree == 0.0)
			maxDegree = 100;
		if (maxVal > 0)
			for (Node n : snaNodes)
				n.betweenness = Math.log(n.betweenness + 1) / maxVal * maxDegree;
		inProcess = false;
	}

	private void computeCloseness(){
		double maxDegree = 0, maxVal = 0;
		int maxDistance = 100;

		Queue<Node> Q = new LinkedList<Node>();
		ArrayList<Node> neighbors = new ArrayList<Node>();
		Node nCurrent;

		for (Node nf : snaNodes){
			nf.d = 0;
			for (Node nt : snaNodes){
				if (nt.ID != nf.ID)
					nt.d = maxDistance;
			}

			Q.add(nf);
			while (!Q.isEmpty()){
				nCurrent = Q.poll();
				neighbors.clear();
				for (Edge e : nCurrent.outEdges)
					if (!e.contracted && !e.isFilteredOut() && !neighbors.contains(e.to))
						neighbors.add(e.to);
				for (Edge e : nCurrent.inEdges)
					if (!e.contracted && !e.isFilteredOut() && !neighbors.contains(e.from))
						neighbors.add(e.from);
				if (neighbors.size() > 0)
					for (Node nn : neighbors){
						// Skip Focus Node (nf) and nodes that were not visited (still have max distance)
						if (nn.d > 0 && nn.d < maxDistance){
							// Check if this path is shorter then the previous one
							if (nn.d > nCurrent.d + 1)
								nn.d = nCurrent.d + 1;
						}
						// Add non-visited node to the Queue and calculate it's distance from the Focus Node (nf)
						else{
							Q.add(nn);
							nn.d = nCurrent.d + 1;
						}
					}
			}

			nf.closeness = 0;
			int i = 0;
			for (Node n : snaNodes){
				if (n.d < maxDistance && n.ID != nf.ID)
					i++;
				nf.closeness = nf.closeness + n.d;
			}
			if (i > 0)
				nf.closeness = 1 / nf.closeness;
			else
				nf.closeness = 0;
			neighbors.clear();
			Q.clear();
		}

		// Normalize Closeness
		for (Node n : snaNodes){
			if (maxVal < n.closeness)
				maxVal = n.closeness;
			if (maxDegree < n.degreeCentrality)
				maxDegree = n.degreeCentrality;
		}
		maxDegree = Math.floor(maxDegree / 100) * 100;
		if (maxDegree == 0.0)
			maxDegree = 100;
		if (maxVal > 0)
			for (Node n : snaNodes)
				n.closeness = n.closeness / maxVal * maxDegree;
	}

	private void computeDegree(){
		inProcess = true;
		for (Node n : snaNodes){
			n.degreeCentrality = 0;
			for (Edge e : n.outEdges)
				if (!e.contracted && !e.isFilteredOut())
					n.degreeCentrality++;

			for (Edge e : n.inEdges)
				if (!e.contracted && !e.isFilteredOut())
					n.degreeCentrality++;
		}
		inProcess = false;
	}

	private void computeEigenvector(){
		ArrayList<Node> neighbors = new ArrayList<Node>();
		inProcess = true;
		for (Node n : snaNodes){
			neighbors.clear();
			n.eigenvector = 0;
			for (Edge e : n.outEdges)
				if (!e.contracted && !e.isFilteredOut() && !neighbors.contains(e.to)){
					neighbors.add(e.to);
					n.eigenvector += e.to.degreeCentrality;
				}

			for (Edge e : n.inEdges)
				if (!e.contracted && !e.isFilteredOut() && !neighbors.contains(e.from)){
					neighbors.add(e.from);
					n.eigenvector += e.from.degreeCentrality;
				}
		}

		// Normalize Eigenvector
		double maxVal = 0.0, maxDegree = 0.0;
		for (Node n : snaNodes){
			if (maxVal < n.eigenvector)
				maxVal = n.eigenvector;
			if (maxDegree < n.degreeCentrality)
				maxDegree = n.degreeCentrality;
		}
		maxDegree = Math.floor(maxDegree / 100) * 100;
		if (maxDegree == 0.0)
			maxDegree = 100;
		if (maxVal > 0)
			for (Node n : snaNodes)
				n.eigenvector = n.eigenvector / maxVal * maxDegree;

		inProcess = false;

	}

	private void computeClustering(){
		Set<Node> neighbors = new HashSet<Node>();
		Set<Edge> edges = new HashSet<Edge>(); // all edges of neighbors
		int edgeCount; // edge count between neighbours
		for (Node n : snaNodes){
			edges.clear();
			n.clustering = 0.0;
			edgeCount = 0;
			neighbors.clear();
			for (Edge e : n.outEdges)
				if (!e.contracted && !e.isFilteredOut()){
					neighbors.add(e.to);
					edges.addAll(e.to.outEdges);
					edges.addAll(e.to.inEdges);
				}
			for (Edge e : n.inEdges)
				if (!e.contracted && !e.isFilteredOut()){
					neighbors.add(e.from);
					edges.addAll(e.from.outEdges);
					edges.addAll(e.from.inEdges);
				}
			for (Edge e : edges){
				if (e.contracted || e.isFilteredOut())
					continue;
				if (neighbors.contains(e.from) && neighbors.contains(e.to)){// edge between n's neighbors?
					edgeCount++;
					if (!e.isDirected())
						edgeCount++;
				}
			}
			// actual formula is proportion between `edgeCount between neighbors` and max possible edge count between
			// neighbors
			if (neighbors.size() > 1)
				n.clustering = ((double) edgeCount / ((double) neighbors.size() * (double) (neighbors.size() - 1))) * 100;
		}
	}

	private void showChart(){
		double minVal = Double.MAX_VALUE, maxVal = 0, temp;
		double minScale = 20, maxScale = 250;

		for (Node n : snaNodes){
			temp = n.betweenness + n.closeness + n.degreeCentrality + n.eigenvector + n.clustering;
			if (minVal > temp)
				minVal = temp;
			if (maxVal < temp)
				maxVal = temp;
		}

		ChartParams chartParams = new ChartParams(SNA_CHART_ID, true, ((Double) minVal).intValue(), ((Double) maxVal)
				.intValue(), minScale, maxScale);
		chartParams.setChartType(ChartType.Pie);
		chartParams.setDisplayOperation(DisplayOperation.Sum);
		chartParams.setShowSummary(true);
		chartParams.setSummaryFormat(new DecimalFormat("#.00"));
		chartParams.setSummaryFont(Utility.createFont("Dialog,1,13"));
		chartParams.setShowLabelOnLegend(true);
		chartParams.setAbsolute(false);

		attributes = getSNAChartAttributes();
		chartParams.setChartAttributes(attributes);

		doc.setChartParams(chartParams, Entity.COMMON_ENTITY_ID);

		for (Node n : snaNodes){
			n.clearChartValues();

			fillChartValues(attributes, n);

			n.chartTotalWithoutFiltering = n.betweenness + n.closeness + n.degreeCentrality + n.eigenvector + n.clustering;
		}
	}

	private void fillChartValues(List<ChartAttributeDescriptor> attributes, Node n){
		for (ChartAttributeDescriptor ca : attributes){
			if (ca.getLabel().equalsIgnoreCase("Degree"))
				n.addChartValue(n.degreeCentrality, ca.getColor());
			else if (ca.getLabel().equalsIgnoreCase("Closeness"))
				n.addChartValue(n.closeness, ca.getColor());
			else if (ca.getLabel().equalsIgnoreCase("Betweenness"))
				n.addChartValue(n.betweenness, ca.getColor());
			else if (ca.getLabel().equalsIgnoreCase("Eigenvector"))
				n.addChartValue(n.eigenvector, ca.getColor());
			else if (ca.getLabel().equalsIgnoreCase("Clustering"))
				n.addChartValue(n.clustering, ca.getColor());
		}
		n.setChartTotal(n.betweenness + n.closeness + n.degreeCentrality + n.eigenvector + n.clustering);
	}

	@Override
	public int getListenerType(){
		return Ni3ItemListener.SRC_SNA;
	}

	@Override
	public void event(int EventCode, int SourceID, Object source, Object param){
		switch (EventCode){
			case MSG_SubgraphChanged:
				if (param != null && doc.getCurrentChartId() == SNA_CHART_ID)
					onSubgraphChanged(param);
				else if (param == null && doc.getCurrentChartId() == SNA_CHART_ID && source != null)
					onSubgraphChanged(((ItemsPanel) source).Doc.Subgraph);
				break;
		}
	}

	private void onSubgraphChanged(Object Param){
		double minVal = Double.MAX_VALUE, maxVal = 0, temp;

		GraphCollection Subgraph = (GraphCollection) Param;

		for (Node n : snaNodes){
			temp = n.getChartTotal();
			if (minVal > temp)
				minVal = temp;
			if (maxVal < temp)
				maxVal = temp;
		}

		final ChartParams chartParams = doc.getChartParams(Entity.COMMON_ENTITY_ID);
		if (chartParams != null){
			chartParams.setChartMinVal(((Double) minVal).intValue());
			chartParams.setChartMaxVal(((Double) maxVal).intValue());

			chartParams.setCurrentGraphMinTotal(((Double) minVal).intValue());
			chartParams.setCurrentGraphMaxTotal(((Double) maxVal).intValue());
		}

		for (Node n : Subgraph.getNodes())
			for (Node nn : snaNodes)
				if (n.ID == nn.ID){
					n.clearChartValues();

					n.betweenness = nn.betweenness;
					n.closeness = nn.closeness;
					n.degreeCentrality = nn.degreeCentrality;
					n.eigenvector = nn.eigenvector;
					n.clustering = nn.clustering;

					fillChartValues(attributes, n);

					n.chartTotalWithoutFiltering = nn.chartTotalWithoutFiltering;
				}
	}

	private List<ChartAttributeDescriptor> getSNAChartAttributes(){
		List<ChartAttributeDescriptor> snaAttributes = new ArrayList<ChartAttributeDescriptor>();
		int index = 0;
		for (SNAAttribute sna : SNAAttribute.values()){
			Color color = Utility.createColor(sna.getColor()); // TODO get color from settings
			final ChartAttributeDescriptor ca = new ChartAttributeDescriptor(sna.getId(), sna.getLabel(), color, index);
			snaAttributes.add(ca);
			index++;
		}
		return snaAttributes;
	}
}
