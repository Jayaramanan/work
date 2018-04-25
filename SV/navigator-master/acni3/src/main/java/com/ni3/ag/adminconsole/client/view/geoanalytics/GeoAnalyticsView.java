/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.geoanalytics;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class GeoAnalyticsView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private ACTree tree;
	private ACTable tableTerritory;
	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton refreshButton;
	private ACButton populateButton;
	private ACButton addCisButton;
	private ACButton deleteCisButton;
	private ACComboBox territoryCombo;

	private ErrorPanel errorPanel;

	private GeoAnalyticsView(){
	}

	public void initializeComponents(){
		SpringLayout mainLayout = new SpringLayout();
		this.setLayout(mainLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		tree = new ACTree();
		tree.setCellRenderer(new ACTreeCellRenderer());
		tree.setExpandsSelectedPaths(true);
		JScrollPane treeScroll = new JScrollPane(tree);

		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		splitPane.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		JPanel rightPanel = new JPanel();
		splitPane.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		ACToolBar toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);

		JScrollPane territoryScrollPane = new JScrollPane();
		tableTerritory = new ACTable();
		tableTerritory.enableCopyPaste();
		territoryScrollPane.setViewportView(tableTerritory);
		rightPanel.add(territoryScrollPane);

		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		mainLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		mainLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		rightPanelLayout.putConstraint(SpringLayout.WEST, territoryScrollPane, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, territoryScrollPane, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, territoryScrollPane, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, territoryScrollPane, -10, SpringLayout.EAST, rightPanel);

		territoryCombo = new ACComboBox();
		territoryCombo.setRenderer(new GisTerritoryListCellRenderer());
		tableTerritory.setDefaultEditor(GisTerritory.class, new GisTerritoryCellEditor(territoryCombo));
		tableTerritory.setDefaultRenderer(GisTerritory.class, new GisTerritoryTableCellRenderer());
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		tree.addTreeSelectionListener(tsl);
	}

	public ACTree getTree(){
		return tree;
	}

	public GeoAnalyticsTreeModel getTreeModel(){
		return (GeoAnalyticsTreeModel) tree.getModel();
	}

	public void setTreeModel(GeoAnalyticsTreeModel model){
		tree.setModel(model);
	}

	public void setTerritoryComboData(List<GisTerritory> territories){
		territoryCombo.removeAllItems();
		if (territories == null){
			return;
		}
		for (GisTerritory territory : territories){
			territoryCombo.addItem(territory);
		}
	}

	public void setTableModel(GisTerritoryTableModel model){
		tableTerritory.setModel(model);
		tableTerritory.setRowSorter(new TableRowSorter<GisTerritoryTableModel>(model));
	}

	public GisTerritoryTableModel getTableModel(){
		return (GisTerritoryTableModel) tableTerritory.getModel();
	}

	public void addUpdateButtonActionListener(ActionListener actionListener){
		updateButton.addActionListener(actionListener);
	}

	public void addAddButtonActionListener(ActionListener actionListener){
		addButton.addActionListener(actionListener);
	}

	public void addDeleteButtonActionListener(ActionListener actionListener){
		deleteButton.addActionListener(actionListener);
	}

	public void addRefreshButtonActionListener(ActionListener actionListener){
		refreshButton.addActionListener(actionListener);
	}

	public void addPopulateButtonActionListener(ActionListener actionListener){
		populateButton.addActionListener(actionListener);
	}

	public void addAddCisButtonActionListener(ActionListener actionListener){
		addCisButton.addActionListener(actionListener);
	}

	public void addDeleteCisButtonActionListener(ActionListener actionListener){
		deleteCisButton.addActionListener(actionListener);
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public int getSelectedRowIndex(){
		if (tableTerritory.getSelectedRow() >= 0){
			return tableTerritory.convertRowIndexToModel(tableTerritory.getSelectedRow());
		}
		return -1;
	}

	public GisTerritory getSelectedTerritory(){
		return getTableModel().getSelectedTerritory(getSelectedRowIndex());
	}

	public void setActiveTableRow(GisTerritory territory){
		GisTerritoryTableModel model = getTableModel();
		int modelIndex = model.indexOf(territory);
		if (modelIndex >= 0){
			tableTerritory.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = tableTerritory.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = tableTerritory.getCellRect(index, 0, true);
				tableTerritory.scrollRectToVisible(r);
			}
		}

		tableTerritory.requestFocusInWindow();
	}

	public void stopCellEditing(){
		if (tableTerritory.isEditing())
			tableTerritory.getCellEditor().stopCellEditing();
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		List<String> msgs = new ArrayList<String>();
		for (int i = 0; i < errors.size(); i++){
			ErrorEntry err = errors.get(i);
			msgs.add(Translation.get(err.getId(), err.getErrors()));
		}
		errorPanel.setErrorMessages(msgs);
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	@Override
	public void resetEditedFields(){
		tableTerritory.resetChanges();
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return tableTerritory.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			tree.setSelectionPath(found);
		}
	}

}
