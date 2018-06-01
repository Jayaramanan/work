/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.connection;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.ColorTableCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ColorTableCellRenderer;
import com.ni3.ag.adminconsole.domain.LineStyle;
import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class ObjectConnectionView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;
	private static final int COLOR_COLUMN_INDEX = 4;
	private JScrollPane treeScroll;
	private ACTree schemaTree;
	private JScrollPane scrollPaneConnection;
	private ACTable tableConnection;

	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton cancelButton;
	private ACButton generateConnectionTypesButton;

	private ACToolBar toolBar;

	private ACComboBox objectCombo;
	private ACComboBox connTypeCombo;
	private ACComboBox lineStyleCombo;
	private ACComboBox lineWeightCombo;

	private ErrorPanel errorPanel;

	private ObjectConnectionView(){
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		schemaTree = new ACTree();
		schemaTree.setCellRenderer(new ACTreeCellRenderer());
		schemaTree.setExpandsSelectedPaths(true);
		treeScroll = new JScrollPane(schemaTree);

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

		toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();
		generateConnectionTypesButton = toolBar.makeGenerateConnectionTypesButton();
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);

		scrollPaneConnection = new JScrollPane();
		tableConnection = new ACTable();
		tableConnection.enableCopyPaste();
		scrollPaneConnection.setViewportView(tableConnection);
		rightPanel.add(scrollPaneConnection);
		rightPanelLayout.putConstraint(SpringLayout.WEST, scrollPaneConnection, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, scrollPaneConnection, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, scrollPaneConnection, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, scrollPaneConnection, -10, SpringLayout.EAST, rightPanel);

		errorPanel = new ErrorPanel();
		this.add(errorPanel);

		this.add(splitPane);

		elementLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		elementLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);

		objectCombo = new ACComboBox();
		objectCombo.setRenderer(new ObjectDefinitionListCellRenderer());
		tableConnection.setDefaultEditor(ObjectDefinition.class, new ACCellEditor(objectCombo));
		tableConnection.setDefaultRenderer(ObjectDefinition.class, new ObjectDefinitionRenderer());

		connTypeCombo = new ACComboBox();
		connTypeCombo.setRenderer(new ConnectionTypeListCellRenderer());
		tableConnection.setDefaultEditor(PredefinedAttribute.class, new ACCellEditor(connTypeCombo));
		tableConnection.setDefaultRenderer(PredefinedAttribute.class, new ConnectionTypeRenderer());

		lineStyleCombo = new ACComboBox();
		lineStyleCombo.setRenderer(new LineStyleListCellRenderer());
		tableConnection.setDefaultEditor(LineStyle.class, new ACCellEditor(lineStyleCombo));
		tableConnection.setDefaultRenderer(LineStyle.class, new LineStyleRenderer());

		lineWeightCombo = new ACComboBox();
		lineWeightCombo.setRenderer(new LineWeightListCellRenderer());
		tableConnection.setDefaultEditor(LineWeight.class, new ACCellEditor(lineWeightCombo));
		tableConnection.setDefaultRenderer(LineWeight.class, new LineWeightRenderer());

		fillLineStyleCombo();
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		schemaTree.addTreeSelectionListener(tsl);
	}

	public void setTreeModel(ObjectConnectionTreeModel model){
		schemaTree.setModel(model);
	}

	public JTree getSchemaTree(){
		return schemaTree;
	}

	public ObjectConnectionTreeModel getTreeModel(){
		return (ObjectConnectionTreeModel) schemaTree.getModel();
	}

	public void setTableModel(ObjectConnectionTableModel model){
		tableConnection.setModel(model);
		tableConnection.setRowSorter(new TableRowSorter<ObjectConnectionTableModel>(model));

		TableColumn column = tableConnection.getColumnModel().getColumn(COLOR_COLUMN_INDEX);
		column.setCellRenderer(new ColorTableCellRenderer());
		column.setCellEditor(new ColorTableCellEditor());
	}

	public ObjectConnectionTableModel getTableModel(){
		return (ObjectConnectionTableModel) tableConnection.getModel();
	}

	public void setObjectDefinitionReferenceData(List<ObjectDefinition> objDefinitions){
		objectCombo.removeAllItems();
		for (ObjectDefinition objDef : objDefinitions){
			objectCombo.addItem(objDef);
		}
	}

	public void setConnectionTypeReferenceData(List<PredefinedAttribute> connTypes){
		connTypeCombo.removeAllItems();
		for (PredefinedAttribute connType : connTypes){
			connTypeCombo.addItem(connType);
		}
	}

	public void fillLineStyleCombo(){
		lineStyleCombo.removeAllItems();
		for (LineStyle lineStyle : LineStyle.values()){
			lineStyleCombo.addItem(lineStyle);
		}
	}

	public void setLineWeightReferenceData(List<LineWeight> lineWeights){
		lineWeightCombo.removeAllItems();
		for (LineWeight lineWeight : lineWeights){
			lineWeightCombo.addItem(lineWeight);
		}
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

	public void addCancelButtonActionListener(ActionListener actionListener){
		cancelButton.addActionListener(actionListener);
	}

	public void addGenerateConnectionTypesActionListener(ActionListener actionListener){
		generateConnectionTypesButton.addActionListener(actionListener);
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public int getSelectedRowIndex(){
		if (tableConnection.getSelectedRow() >= 0){
			return tableConnection.convertRowIndexToModel(tableConnection.getSelectedRow());
		}
		return -1;
	}

	public ObjectConnection getSelectedConnection(){
		return getTableModel().getSelectedConnection(getSelectedRowIndex());
	}

	public void setActiveTableRow(ObjectConnection connection){
		ObjectConnectionTableModel model = getTableModel();
		int modelIndex = model.indexOf(connection);
		if (modelIndex >= 0){
			tableConnection.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = tableConnection.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = tableConnection.getCellRect(index, 0, true);
				tableConnection.scrollRectToVisible(r);
			}
		}

		tableConnection.requestFocusInWindow();
	}

	public void stopCellEditing(){
		if (tableConnection.isEditing())
			tableConnection.getCellEditor().stopCellEditing();
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
		tableConnection.resetChanges();
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return tableConnection.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		ObjectDefinition currentObject = holder.getCurrentObject();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (currentPath != null){
			TreePath found = getTreeModel().findPathByNodes(currentPath, getTreeModel());
			if (currentObject != null && !currentObject.equals(found.getLastPathComponent()) && currentPath.length > 3){
				found = getTreeModel().findPathByNodes(Arrays.copyOf(currentPath, 3), getTreeModel());
			}
			getSchemaTree().setSelectionPath(found);
		}
	}

	public ObjectDefinition getSelectedEdge(){
		ObjectDefinition od = ObjectHolder.getInstance().getCurrentObject();
		if (od.isEdge())
			return od;
		return null;
	}
}
