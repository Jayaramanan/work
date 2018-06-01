/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.domain.User;

public class ThickClientPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private ACButton cancelButton;
	private ACToolBar toolBar;
	private ACTable thickClientTable;
	private ACTable sequenceRangeTable;

	private ChangeResetable[] resetableComponents;

	public ThickClientPanel(){
		initializeComponents();
	}

	private JPanel initThickClientTablePanel(){
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		toolBar = new ACToolBar();
		cancelButton = toolBar.makeRefreshButton();
		panel.add(toolBar);
		layout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, panel);

		JScrollPane scrollPane = new JScrollPane();
		thickClientTable = new ACTable();
		thickClientTable.enableCopyPaste();
		scrollPane.setViewportView(thickClientTable);
		panel.add(scrollPane);

		layout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		layout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, panel);
		return panel;
	}

	private JPanel initSeqRangeTablePanel(){
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);

		JScrollPane seqRangeScrollPane = new JScrollPane();
		sequenceRangeTable = new ACTable();
		sequenceRangeTable.enableCopyPaste();
		sequenceRangeTable.enableToolTips();
		seqRangeScrollPane.setViewportView(sequenceRangeTable);
		panel.add(seqRangeScrollPane);

		layout.putConstraint(SpringLayout.WEST, seqRangeScrollPane, 10, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.NORTH, seqRangeScrollPane, 0, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, seqRangeScrollPane, -10, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.EAST, seqRangeScrollPane, -10, SpringLayout.EAST, panel);

		return panel;
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		setLayout(elementLayout);

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplit.setTopComponent(initThickClientTablePanel());
		rightSplit.setBottomComponent(initSeqRangeTablePanel());
		rightSplit.setBorder(BorderFactory.createEmptyBorder());
		rightSplit.setDividerLocation(400);
		elementLayout.putConstraint(SpringLayout.NORTH, rightSplit, 0, SpringLayout.NORTH, this);
		elementLayout.putConstraint(SpringLayout.WEST, rightSplit, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.EAST, rightSplit, 0, SpringLayout.EAST, this);
		elementLayout.putConstraint(SpringLayout.SOUTH, rightSplit, 0, SpringLayout.SOUTH, this);

		add(rightSplit);

		setTableModel(new ThickClientTableModel(null));
		setSeqRangeTableModel(new SeqRangeTableModel());

		resetableComponents = new ChangeResetable[] { thickClientTable };
	}

	public void setSeqRangeTableModel(SeqRangeTableModel model){
		sequenceRangeTable.setModel(model);
		sequenceRangeTable.setRowSorter(new TableRowSorter<SeqRangeTableModel>(model));
	}

	public void setTableModel(ThickClientTableModel model){
		thickClientTable.setModel(model);
		thickClientTable.setRowSorter(new TableRowSorter<ThickClientTableModel>(model));
	}

	public ThickClientTableModel getTableModel(){
		return (ThickClientTableModel) thickClientTable.getModel();
	}

	public void setThickClientTableSelectionListener(ListSelectionListener listener){
		ListSelectionModel selectionModel = thickClientTable.getSelectionModel();
		selectionModel.addListSelectionListener(listener);
	}

	public void addCancelButtonActionListener(ActionListener actionListener){
		cancelButton.addActionListener(actionListener);
	}

	public void setActiveTableRow(User user){
		if (user == null){
			return;
		}
		ThickClientTableModel model = getTableModel();
		int modelIndex = model.indexOf(user);
		if (modelIndex >= 0){
			thickClientTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = thickClientTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = thickClientTable.getCellRect(index, 0, true);
				thickClientTable.scrollRectToVisible(r);
			}
		}

		thickClientTable.requestFocusInWindow();
	}

	public int getSelectedRow(){
		if (thickClientTable.getSelectedRow() >= 0){
			return thickClientTable.convertRowIndexToModel(thickClientTable.getSelectedRow());
		}
		return -1;
	}

	public User getSelectedUser(){
		int rowIndex = getSelectedRow();
		if (rowIndex >= 0){
			return getTableModel().getSelectedUser(rowIndex);
		}
		return null;
	}

	public void setSelectedUser(User selectedUser){
		ThickClientTableModel model = (ThickClientTableModel) thickClientTable.getModel();
		int index = model.indexOf(selectedUser);
		thickClientTable.getSelectionModel().setSelectionInterval(index, index);
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return resetableComponents;
	}

	public void stopCellEditing(){
		if (thickClientTable.isEditing())
			thickClientTable.getCellEditor().stopCellEditing();
	}
}
