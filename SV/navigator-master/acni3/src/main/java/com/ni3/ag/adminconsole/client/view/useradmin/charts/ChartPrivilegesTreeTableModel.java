/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.charts;

import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.common.treetable.AbstractTreeTableModel;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;

public class ChartPrivilegesTreeTableModel extends AbstractTreeTableModel{

	private List<Schema> schemas;
	private ACRootNode rootNode = new ACRootNode("");

	public ChartPrivilegesTreeTableModel(List<Schema> schemas){
		this.schemas = schemas;
	}

	@Override
	public Object getChild(Object parent, int index){
		if (rootNode.equals(parent)){
			return schemas.get(index);
		} else if (parent instanceof Schema){
			List<Chart> charts = ((Schema) parent).getCharts();
			return charts.get(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent){
		if (rootNode.equals(parent)){
			return schemas != null ? schemas.size() : 0;
		} else if (parent instanceof Schema){
			List<Chart> charts = ((Schema) parent).getCharts();
			return charts != null ? charts.size() : 0;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child){
		if (rootNode.equals(parent)){
			return schemas.indexOf(child);
		} else if (parent instanceof Schema){
			List<Chart> charts = ((Schema) parent).getCharts();
			return charts.indexOf(child);
		}
		return -1;
	}

	@Override
	public Object getRoot(){
		return rootNode;
	}

	@Override
	public boolean isLeaf(Object node){
		if (rootNode.equals(node)){
			return schemas == null || schemas.isEmpty();
		} else if (node instanceof Schema){
			List<Chart> charts = ((Schema) node).getCharts();
			return charts == null || charts.isEmpty();
		}
		return true;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue){
	}

}
