/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gui.graph.layoutManager;

import java.util.Comparator;

import com.ni3.ag.navigator.client.domain.DBObjectComparator;
import com.ni3.ag.navigator.client.gui.datalist.MatrixSortOrder;
import com.ni3.ag.navigator.client.gui.graph.Node;

public class NodeComparator implements Comparator<Node>{
	private DBObjectComparator comparator;

	public NodeComparator(MatrixSortOrder order){
		comparator = new DBObjectComparator(order);
	}

	@Override
	public int compare(Node o1, Node o2){
		return comparator.compare(o1.Obj, o2.Obj);
	}

}
