/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.charts;

import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.BooleanCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.client.view.common.treetable.ACTreeTable;
import com.ni3.ag.adminconsole.domain.Group;

public class ChartPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private ACTreeTable tableCharts;
	private ACButton updateButton;
	private ACButton cancelButton;

	private ChangeResetable[] resetableComponents;

	public ChartPanel(){
		initializeComponents();
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		setLayout(elementLayout);

		ACToolBar toolBar = new ACToolBar();

		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();
		add(toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		JScrollPane scrollPane = new JScrollPane();
		tableCharts = new ACTreeTable();
		tableCharts.enableCopyPaste();
		scrollPane.setViewportView(tableCharts);
		add(scrollPane);

		elementLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		elementLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);

		resetableComponents = new ChangeResetable[] { tableCharts };
		TableCellRenderer boolCellRenderer = tableCharts.getDefaultRenderer(Boolean.class);
		tableCharts.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));

	}

	public void setTreeTableModel(ChartPrivilegesTreeTableModel treeModel, Group group){
		ACTableModel tableModel = new ChartPrivilegesTableModel(tableCharts.getTree(), group);
		tableCharts.setModel(treeModel, tableModel);
	}

	public ChartPrivilegesTreeTableModel getTreeModel(){
		return (ChartPrivilegesTreeTableModel) tableCharts.getModel();
	}

	public void addUpdateButtonActionListener(ActionListener actionListener){
		updateButton.addActionListener(actionListener);
	}

	public void addRefreshButtonActionListener(ActionListener actionListener){
		cancelButton.addActionListener(actionListener);
	}

	public void stopCellEditing(){
		if (tableCharts.isEditing())
			tableCharts.getCellEditor().stopCellEditing();
	}

	public int getSelectedRow(){
		if (tableCharts.getSelectedRow() >= 0){
			return tableCharts.convertRowIndexToModel(tableCharts.getSelectedRow());
		}
		return -1;
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return resetableComponents;
	}

}