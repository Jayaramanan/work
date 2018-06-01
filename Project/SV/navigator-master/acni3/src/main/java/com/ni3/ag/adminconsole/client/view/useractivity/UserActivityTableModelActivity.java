/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserActivityTableModelActivity extends ACTableModel{

	private static final long serialVersionUID = 1L;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DataType.DATE_TIME_FORMAT);

	private JTree tree;

	public UserActivityTableModelActivity(JTree tree){
		this.tree = tree;
		addColumn(get(TextID.Action), false, TreeModel.class, false);
		addColumn(get(TextID.DateTime), false, String.class, false);

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
				return getActivityTime(node);
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
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column){
	}

	private String getActivityTime(Object node){
		if (node instanceof UserActivity){
			UserActivity activity = (UserActivity) node;
			if (activity.getDateTime() != null)
				return DATE_FORMAT.format(activity.getDateTime());
		}
		return null;
	}

}
