package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ni3.ag.navigator.client.gui.graph.Edge;
import com.ni3.ag.navigator.client.model.GraphCollection;
import com.ni3.ag.navigator.client.gui.graph.Node;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.model.SystemGlobals;

public class LineGraphLayoutManager extends DefaultGraphLayoutManager{
	public static final String NAME = "Line";
	private static final int NODE_GAP = 15;

	private boolean needLayout = true;
	private MatrixSortOrder order;

	@Override
	public boolean isOptionEnabled(Node n, int index){
		return false;
	}

	@Override
	public String getName(){
		return NAME;
	}

	@Override
	public void graphChanged(GraphCollection graph){
		needLayout = true;
	}

	@Override
	public boolean needLayout(GraphCollection graph){
		return needLayout;
	}

	@Override
	public boolean doLayout(GraphCollection graph){
		int startX = NODE_GAP;
		int startY = NODE_GAP * 10;
		List<Node> sortedNodes = new ArrayList<Node>();
		synchronized (graph.getEdges()){
			for (Edge e : graph.getEdges())
				e.edgeStyle = Edge.ES_Angular;
		}
		synchronized (graph.getNodes()){
			sortedNodes.addAll(graph.getNodes());
		}
		order = SystemGlobals.MainFrame.Doc.getMatrixSort();
		Collections.sort(sortedNodes, new NodeComparator(order));
		int maxWidth = getMaxCellWidth(graph.getDisplayedNodes());

		boolean gridZigZag = false;
		for (Node n : sortedNodes){
			if (!n.isActive())
				continue;
			n.setY(startY);
			n.setX(startX);
			startX += (maxWidth + NODE_GAP) * graph.NodeSpace;
			n.ZigZag = gridZigZag;
			gridZigZag = !gridZigZag;
		}
		return true;
	}
}
