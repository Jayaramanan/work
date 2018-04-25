/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;

public class FormatAttributesView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;

	private ACTree schemaTree;
	private ACTable objectAttributeTable;
	private ACButton updateButton;
	private ACButton refreshButton;
	private ErrorPanel errorPanel;

	@Override
	public void initializeComponents(){
		SpringLayout mainLayout = new SpringLayout();
		setLayout(mainLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane mainSplit = new JSplitPane();
		mainLayout.putConstraint(SpringLayout.NORTH, mainSplit, 0, SpringLayout.SOUTH, errorPanel);
		mainLayout.putConstraint(SpringLayout.WEST, mainSplit, 0, SpringLayout.WEST, this);
		mainLayout.putConstraint(SpringLayout.EAST, mainSplit, 0, SpringLayout.EAST, this);
		mainLayout.putConstraint(SpringLayout.SOUTH, mainSplit, 0, SpringLayout.SOUTH, this);
		add(mainSplit);

		schemaTree = new ACTree();
		JScrollPane treeScroll = new JScrollPane();
		schemaTree.setExpandsSelectedPaths(true);
		treeScroll.setViewportView(schemaTree);

		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		mainSplit.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		objectAttributeTable = new ACTable();
		objectAttributeTable.enableCopyPaste();
		objectAttributeTable.enableToolTips();
		JScrollPane objectAttributeTableScroll = new JScrollPane(objectAttributeTable);

		ACToolBar toolBar = new ACToolBar();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();

		JPanel rightPanel = new JPanel();
		mainSplit.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);
		rightPanel.add(toolBar);
		rightPanelLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, toolBar, 0, SpringLayout.NORTH, rightPanel);

		rightPanel.add(objectAttributeTableScroll);
		rightPanelLayout.putConstraint(SpringLayout.WEST, objectAttributeTableScroll, 0, SpringLayout.WEST, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.NORTH, objectAttributeTableScroll, 0, SpringLayout.SOUTH, toolBar);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, objectAttributeTableScroll, -10, SpringLayout.SOUTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, objectAttributeTableScroll, -10, SpringLayout.EAST, rightPanel);

		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		schemaTree.setCellRenderer(new ACTreeCellRenderer());
	}

	public void addUpdateButtonListener(ActionListener listener){
		updateButton.addActionListener(listener);
	}

	public void addRefreshButtonListener(ActionListener listener){
		refreshButton.addActionListener(listener);
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return objectAttributeTable.isChanged();
	}

	@Override
	public void resetEditedFields(){
		objectAttributeTable.resetChanges();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] current = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (current != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(current, schemaTree.getModel());
			schemaTree.setSelectionPath(found);
		}
	}

	public void renderErrors(List<ErrorEntry> errors){
		if (errors == null)
			errorPanel.setErrorMessages(null);
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

	public void setCurrentTreeController(AbstractController controller){
		schemaTree.setCurrentController(controller);
	}

	public void addTreeSelectionListener(TreeSelectionListener tsl){
		schemaTree.addTreeSelectionListener(tsl);
	}

	public void stopCellEditing(){
		if (objectAttributeTable.isEditing()){
			objectAttributeTable.getCellEditor().stopCellEditing();
		}
	}

	public ListSelectionModel getAttributeTableSelectionModel(){
		return objectAttributeTable.getSelectionModel();
	}

	public TreeModel getSchemaTreeModel(){
		return schemaTree.getModel();
	}

	public void setTreeSelectionPath(TreePath found){
		schemaTree.setSelectionPath(found);
	}

	public void setTableModel(FormatAttributeTableModel tableModel){
		objectAttributeTable.setModel(tableModel);
		objectAttributeTable.setRowSorter(new TableRowSorter<FormatAttributeTableModel>(tableModel));
	}

	public FormatAttributeTableModel getTableModel(){
		return (FormatAttributeTableModel) objectAttributeTable.getModel();
	}

	public void refreshTable(){
		((FormatAttributeTableModel) objectAttributeTable.getModel()).fireTableDataChanged();
	}

	public void setTreeModel(TreeModel treeModel){
		schemaTree.setModel(treeModel);
	}

	public TreePath getTreeSelectionPath(){
		return schemaTree.getSelectionPath();
	}

	public void setActiveTableRow(ObjectAttribute attribute){
		if (attribute == null && getTableModel() != null){
			return;
		}
		FormatAttributeTableModel model = getTableModel();
		int modelIndex = model.indexOf(attribute);
		if (modelIndex >= 0){
			objectAttributeTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = objectAttributeTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = objectAttributeTable.getCellRect(index, 0, true);
				objectAttributeTable.scrollRectToVisible(r);
			}
		}

		objectAttributeTable.requestFocusInWindow();
	}

	public ObjectAttribute getSelectedAttribute(){
		FormatAttributeTableModel model = getTableModel();
		if (model != null){
			return model.getSelectedAttribute(getSelectedRowIndex());
		}
		return null;
	}

	public int getSelectedRowIndex(){
		int row = objectAttributeTable.getSelectedRow();
		if (row != -1){
			row = objectAttributeTable.convertRowIndexToModel(row);
		}
		return row;
	}

	public void addListSelectionListener(ListSelectionListener listener){
		objectAttributeTable.getSelectionModel().addListSelectionListener(listener);
	}

}
