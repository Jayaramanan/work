/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.charts;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.CellEditorListener;
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
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class ChartView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = 1L;
	private static final int ATTRIBUTE_COLOR_COLUMN_INDEX = 1;
	private ACTree objectTree;
	private ErrorPanel errorPanel;
	private ACButton addButton;
	private ACButton deleteButton;
	private ACButton updateButton;
	private ACButton refreshButton;
	private ACTable chartTable;
	private ACButton addAttributeButton;
	private ACButton deleteAttributeButton;
	private ACTable attributeTable;
	private ACButton addChartButton;
	private ACButton deleteChartButton;
	private ACComboBox objectDefinitionComboBox;
	private ACComboBox attributeComboBox;
	private ACComboBox displayOperationsComboBox;
	private ACComboBox chartTypeComboBox;
	private ChartPreviewPanel chartPreviewPanel;

	@Override
	public void initializeComponents(){
		SpringLayout mainLayout = new SpringLayout();
		setLayout(mainLayout);
		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		mainLayout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		mainLayout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		add(mainSplit);
		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		JPanel leftPanel = new JPanel();
		SpringLayout leftLayout = new SpringLayout();
		leftPanel.setLayout(leftLayout);
		ACToolBar leftBar = new ACToolBar();
		addChartButton = leftBar.makeAddChartButton();
		deleteChartButton = leftBar.makeDeleteChartButton();
		leftLayout.putConstraint(SpringLayout.WEST, leftBar, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(leftBar);

		JScrollPane treeScroll = new JScrollPane();
		objectTree = new ACTree();

		treeScroll.setViewportView(objectTree);
		leftLayout.putConstraint(SpringLayout.NORTH, treeScroll, 0, SpringLayout.SOUTH, leftBar);
		leftLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);
		leftLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanel.add(treeScroll);
		mainSplit.setLeftComponent(leftPanel);

		JPanel rightPanel = new JPanel();
		SpringLayout rightLayout = new SpringLayout();
		rightPanel.setLayout(rightLayout);
		mainSplit.setRightComponent(rightPanel);

		JSplitPane tablesSplit = new JSplitPane();
		tablesSplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tablesSplit.setDividerLocation((int) (ACMain.getScreenHeight() / 3));
		tablesSplit.setBorder(BorderFactory.createEmptyBorder());
		rightPanel.add(tablesSplit);

		rightLayout.putConstraint(SpringLayout.WEST, tablesSplit, 10, SpringLayout.WEST, rightPanel);
		rightLayout.putConstraint(SpringLayout.NORTH, tablesSplit, 0, SpringLayout.NORTH, rightPanel);
		rightLayout.putConstraint(SpringLayout.SOUTH, tablesSplit, -10, SpringLayout.SOUTH, rightPanel);
		rightLayout.putConstraint(SpringLayout.EAST, tablesSplit, -10, SpringLayout.EAST, rightPanel);
		tablesSplit.setBorder(BorderFactory.createEmptyBorder());

		JPanel topPanel = new JPanel();
		SpringLayout topLayout = new SpringLayout();
		topPanel.setLayout(topLayout);
		ACToolBar topBar = new ACToolBar();
		addButton = topBar.makeAddButton();
		deleteButton = topBar.makeDeleteButton();
		updateButton = topBar.makeUpdateButton();
		refreshButton = topBar.makeRefreshButton();
		topPanel.add(topBar);

		chartTable = new ACTable();
		JScrollPane chartTableScrol = new JScrollPane();
		chartTableScrol.setViewportView(chartTable);
		topLayout.putConstraint(SpringLayout.NORTH, chartTableScrol, 0, SpringLayout.SOUTH, topBar);
		topLayout.putConstraint(SpringLayout.WEST, chartTableScrol, 0, SpringLayout.WEST, topPanel);
		topLayout.putConstraint(SpringLayout.EAST, chartTableScrol, 0, SpringLayout.EAST, topPanel);
		topLayout.putConstraint(SpringLayout.SOUTH, chartTableScrol, 0, SpringLayout.SOUTH, topPanel);
		topPanel.add(chartTableScrol);
		tablesSplit.setTopComponent(topPanel);
		chartTable.enableCopyPaste();
		chartTable.setSelectionModel(new ObjectChartTableSelectionModel());

		chartTable.setDefaultRenderer(ObjectDefinition.class, new ObjectDefinitionCellRenderer());
		chartTable.setDefaultRenderer(ChartDisplayOperation.class, new DisplayOperationCellRenderer());
		chartTable.setDefaultRenderer(ChartType.class, new ChartTypeCellRenderer());

		objectDefinitionComboBox = new ACComboBox();
		objectDefinitionComboBox.setRenderer(new ObjectDefinitionItemRenderer());
		chartTable.setDefaultEditor(ObjectDefinition.class, new ACCellEditor(objectDefinitionComboBox));

		displayOperationsComboBox = new ACComboBox();
		displayOperationsComboBox.setRenderer(new DisplayOperationItemRenderer());
		chartTable.setDefaultEditor(ChartDisplayOperation.class, new ACCellEditor(displayOperationsComboBox));

		chartTypeComboBox = new ACComboBox();
		chartTypeComboBox.setRenderer(new ChartTypeItemRenderer());
		chartTable.setDefaultEditor(ChartType.class, new ACCellEditor(chartTypeComboBox));

		JPanel bottomPanel = new JPanel();
		SpringLayout bottomLayout = new SpringLayout();
		bottomPanel.setLayout(bottomLayout);
		ACToolBar bottomBar = new ACToolBar();
		addAttributeButton = bottomBar.makeAddButton2();
		deleteAttributeButton = bottomBar.makeDeleteButton2();
		bottomPanel.add(bottomBar);

		attributeTable = new ACTable();
		attributeTable.enableCopyPaste();
		JScrollPane attributeTableScrol = new JScrollPane();
		attributeTableScrol.setViewportView(attributeTable);
		bottomLayout.putConstraint(SpringLayout.NORTH, attributeTableScrol, 5, SpringLayout.SOUTH, bottomBar);
		bottomLayout.putConstraint(SpringLayout.WEST, attributeTableScrol, 0, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.EAST, attributeTableScrol, 400, SpringLayout.WEST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.SOUTH, attributeTableScrol, 0, SpringLayout.SOUTH, bottomPanel);
		bottomPanel.add(attributeTableScrol);
		tablesSplit.setBottomComponent(bottomPanel);

		chartPreviewPanel = new ChartPreviewPanel();
		chartPreviewPanel.setBorder(attributeTableScrol.getBorder());
		bottomLayout.putConstraint(SpringLayout.NORTH, chartPreviewPanel, 5, SpringLayout.SOUTH, bottomBar);
		bottomLayout.putConstraint(SpringLayout.WEST, chartPreviewPanel, 10, SpringLayout.EAST, attributeTableScrol);
		bottomLayout.putConstraint(SpringLayout.EAST, chartPreviewPanel, 0, SpringLayout.EAST, bottomPanel);
		bottomLayout.putConstraint(SpringLayout.SOUTH, chartPreviewPanel, 0, SpringLayout.SOUTH, bottomPanel);
		bottomPanel.add(chartPreviewPanel);

		ACTreeCellRenderer renderer = new ACTreeCellRenderer();
		objectTree.setCellRenderer(renderer);
		objectTree.setCellEditor(new ChartTreeCellEditor(objectTree, renderer));
		objectTree.setEditable(true);

		attributeComboBox = new ACComboBox();
		attributeComboBox.setRenderer(new ObjectAttributeItemRenderer());
		attributeTable.setDefaultEditor(ObjectAttribute.class, new ACCellEditor(attributeComboBox));

		Dimension d = errorPanel.getSize();
		d.height = 0;
		errorPanel.setSize(d);

		fillChartTypes();
		fillDisplayOperations();
	}

	public void setObjectDefinitions(List<ObjectDefinition> objects){
		objectDefinitionComboBox.removeAllItems();
		for (ObjectDefinition od : objects){
			objectDefinitionComboBox.addItem(od);
		}
	}

	public void setAttributeComboData(List<ObjectAttribute> attributes){
		attributeComboBox.removeAllItems();
		if (attributes == null){
			return;
		}
		for (ObjectAttribute attr : attributes){
			attributeComboBox.addItem(attr);
		}
	}

	@Override
	public void resetEditedFields(){
		chartTable.resetChanges();
		attributeTable.resetChanges();
		objectDefinitionComboBox.resetChanges();
		chartTypeComboBox.resetChanges();
		displayOperationsComboBox.resetChanges();
	}

	@Override
	public void renderErrors(List<ErrorEntry> errors){
		if (errors == null || errors.isEmpty())
			errorPanel.clearErrorMessage();
		else{
			List<String> msgs = new ArrayList<String>();
			for (int i = 0; i < errors.size(); i++){
				ErrorEntry err = errors.get(i);
				msgs.add(Translation.get(err.getId(), err.getErrors()));
			}
			errorPanel.setErrorMessages(msgs);
		}
	}

	public void clearErrors(){
		errorPanel.clearErrorMessage();
	}

	public JTree getObjectTree(){
		return objectTree;
	}

	public ChartTreeModel getTreeModel(){
		return (ChartTreeModel) objectTree.getModel();
	}

	public ACTable getChartTable(){
		return chartTable;
	}

	public JTable getChartAttributeTable(){
		return attributeTable;
	}

	public void setChartTableModel(ChartTableModel model){
		chartTable.setModel(model);
		chartTable.setRowSorter(new TableRowSorter<ChartTableModel>(model));
		int index = ChartTableModel.FontColor_index;
		TableColumn column = chartTable.getColumnModel().getColumn(index);
		column.setCellRenderer(new ColorTableCellRenderer(true));
		column.setCellEditor(new ColorTableCellEditor(true));
	}

	public ChartTableModel getChartTableModel(){
		return (ChartTableModel) chartTable.getModel();
	}

	public ChartAttributeTableModel getChartAttributeTableModel(){
		return (ChartAttributeTableModel) attributeTable.getModel();
	}

	public void setChartAttributeTableModel(ChartAttributeTableModel model){
		attributeTable.setModel(model);
		attributeTable.setRowSorter(new TableRowSorter<ChartAttributeTableModel>(model));

		TableColumn column = attributeTable.getColumnModel().getColumn(ATTRIBUTE_COLOR_COLUMN_INDEX);
		column.setCellRenderer(new ColorTableCellRenderer());
		column.setCellEditor(new ColorTableCellEditor());
	}

	public void fillDisplayOperations(){
		displayOperationsComboBox.removeAllItems();
		for (ChartDisplayOperation di : ChartDisplayOperation.values()){
			displayOperationsComboBox.addItem(di);
		}
	}

	public void fillChartTypes(){
		chartTypeComboBox.removeAllItems();
		for (ChartType ct : ChartType.values()){
			chartTypeComboBox.addItem(ct);
		}
	}

	public JButton getAddObjectChartButton(){
		return addButton;
	}

	public JButton getDeleteObjectChartButton(){
		return deleteButton;
	}

	public void setChartTableActiveRow(ObjectChart oc){
		ChartTableModel model = (ChartTableModel) chartTable.getModel();
		int modelIndex = model.indexOf(oc);
		if (modelIndex >= 0){
			chartTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = chartTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = chartTable.getCellRect(index, 0, true);
				chartTable.scrollRectToVisible(r);
			}
		}

		chartTable.requestFocusInWindow();
	}

	public ObjectChart getChartTableActiveRow(){
		ChartTableModel tableModel = getChartTableModel();
		int selectedRow = chartTable.getSelectedRow();
		if (selectedRow == -1)
			return null;
		int row = chartTable.convertRowIndexToModel(selectedRow);
		return tableModel != null ? tableModel.getObjectChart(row) : null;
	}

	public JButton getRefreshButton(){
		return refreshButton;
	}

	public JButton getUpdateButton(){
		return updateButton;
	}

	public JButton getAddChartButton(){
		return addChartButton;
	}

	public JButton getDeleteChartButton(){
		return deleteChartButton;
	}

	public void stopTableEditing(){
		if (chartTable.isEditing())
			chartTable.getCellEditor().stopCellEditing();
		if (attributeTable.isEditing())
			attributeTable.getCellEditor().stopCellEditing();
	}

	public JButton getAddChartAttributeButton(){
		return addAttributeButton;
	}

	public JButton getDeleteChartAttributeButton(){
		return deleteAttributeButton;
	}

	public JTable getAttributeTable(){
		return attributeTable;
	}

	public void setAttributeTableActiveRow(ChartAttribute cca, boolean updated){
		ChartAttributeTableModel model = (ChartAttributeTableModel) attributeTable.getModel();
		int modelIndex = updated ? model.indexOfUpdated(cca) : model.indexOf(cca);
		if (modelIndex >= 0){
			int index = attributeTable.convertRowIndexToView(modelIndex);
			attributeTable.getSelectionModel().setSelectionInterval(index, index);
			if (index >= 0){
				Rectangle r = attributeTable.getCellRect(index, 0, true);
				attributeTable.scrollRectToVisible(r);
			}
		}
	}

	public void setAttributeTableActiveRow(ChartAttribute cca){
		setAttributeTableActiveRow(cca, false);
	}

	public ChartAttribute getAttributeTableActiveRow(){
		ChartAttributeTableModel tableModel = getChartAttributeTableModel();
		int selectedRow = attributeTable.getSelectedRow();
		if (selectedRow == -1)
			return null;
		int row = attributeTable.convertRowIndexToModel(selectedRow);
		return tableModel != null ? tableModel.getDynamicAttribute(row) : null;
	}

	public void refreshChartTable(){
		getChartTableModel().fireTableDataChanged();
	}

	public void refreshChartAttributeTable(){
		getChartAttributeTableModel().fireTableDataChanged();
	}

	public ChartPreviewPanel getChartPreview(){
		return chartPreviewPanel;
	}

	public ACComboBox getChartTypeComboBox(){
		return chartTypeComboBox;
	}

	public ObjectChart getSelectedObjectChart(){
		int index = chartTable.getSelectedRow();
		if (index == -1){
			return null;
		}
		index = chartTable.convertRowIndexToModel(index);
		if (index == -1){
			return null;
		} else{
			return getChartTableModel().getObjectChart(index);
		}
	}

	public ChartAttribute getSelectedChartAttribute(){
		int index = attributeTable.getSelectedRow();
		if (index == -1){
			return null;
		}
		index = attributeTable.convertRowIndexToModel(index);
		if (index == -1){
			return null;
		} else{
			return getChartAttributeTableModel().getChartAttribute(index);
		}
	}

	@Override
	public boolean isChanged(){
		stopTableEditing();
		return chartTable.isChanged() || attributeTable.isChanged();
	}

	public boolean isAttributeTableChanged(){
		stopTableEditing();
		return attributeTable.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getTreeModel());
			getObjectTree().setSelectionPath(found);
		}
	}

	public void addChartTreeEditorChartNameListener(CellEditorListener listener){
		objectTree.getCellEditor().addCellEditorListener(listener);
	}

}
