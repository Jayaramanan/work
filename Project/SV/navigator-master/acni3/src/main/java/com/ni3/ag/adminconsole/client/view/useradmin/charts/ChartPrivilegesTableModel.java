/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.charts;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.Chart;
import static com.ni3.ag.adminconsole.shared.language.TextID.HasAccess;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.Group;

public class ChartPrivilegesTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private Group group;

	public ChartPrivilegesTableModel(JTree tree, Group group){
		this.tree = tree;
		this.group = group;
		addColumn(get(Chart), false, TreeModel.class, false);
		addColumn(get(HasAccess), false, Boolean.class, false);

		tree.addTreeExpansionListener(new TreeExpansionListener(){
			public void treeExpanded(TreeExpansionEvent event){
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event){
				fireTableDataChanged();
			}
		});
	}

	@Override
	public int getRowCount(){
		return tree.getRowCount();
	}

	protected Object nodeForRow(int row){
		TreePath treePath = tree.getPathForRow(row);
		return treePath.getLastPathComponent();
	}

	@Override
	public Object getValueAt(int row, int column){
		Object node = nodeForRow(row);
		switch (column){
			case 0:
				return node;
			case 1:
				return hasAccess(node);
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int row, int column){
		return isCellEditable(nodeForRow(row), column);
	}

	boolean isCellEditable(Object node, int column){
		if (column == 0){
			return true;
		} else if (column == 1){
			return node instanceof Chart;
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column){
		Object node = nodeForRow(row);
		switch (column){
			case 0:
				break;
			case 1:
				setHasAccess(node, (Boolean) aValue);
				break;
			default:
				break;
		}
	}

	boolean hasAccess(Object node){
		if (node instanceof Chart){
			Chart chart = (Chart) node;
			for (ChartGroup cg : chart.getChartGroups()){
				if (cg.getGroup().equals(group)){
					return true;
				}
			}
		}
		return false;
	}

	public void setHasAccess(Object node, boolean value){
		if (!(node instanceof Chart))
			return;

		Chart chart = (Chart) node;
		ChartGroup found = null;
		for (ChartGroup cg : chart.getChartGroups()){
			if (cg.getGroup().equals(group)){
				found = cg;
				break;
			}
		}

		if (!value && found != null){
			chart.getChartGroups().remove(found);
		} else if (value && found == null){
			ChartGroup newGp = new ChartGroup(group, chart);
			chart.getChartGroups().add(newGp);
		}
	}
}
