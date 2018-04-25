/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
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
import com.ni3.ag.adminconsole.client.view.common.ACCheckBox;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACSplitButton;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.BooleanCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.InMatrixType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class AttributeEditView extends JPanel implements AbstractView, ErrorRenderer{
	private static final long serialVersionUID = 1L;

	private static final int TABLE_MINIMUM_WIDTH = 920;

	private ACTree schemaTree;
	private ErrorPanel errorPanel;
	private ACTable attributeTable;
	private ACButton refreshButton;
	private ACSplitButton updateButton;
	private ACToolBar toolBar;
	private JScrollPane scrollPane;
	private ACComboBox inMatrixCombo;
	private ACCheckBox advancedViewCheckbox;

	private AttributeEditView(){
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		errorPanel = new ErrorPanel();
		add(errorPanel);

		JSplitPane splitPane = new JSplitPane();
		elementLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, errorPanel);
		elementLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);
		add(splitPane);

		schemaTree = new ACTree();
		schemaTree.setExpandsSelectedPaths(true);
		JScrollPane treeScroll = new JScrollPane();
		treeScroll.setViewportView(schemaTree);
		JPanel leftPanel = new JPanel();
		SpringLayout leftPanelLayout = new SpringLayout();
		leftPanel.setLayout(leftPanelLayout);

		leftPanelLayout.putConstraint(SpringLayout.WEST, treeScroll, 10, SpringLayout.WEST, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.NORTH, treeScroll, 10, SpringLayout.NORTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.SOUTH, treeScroll, -10, SpringLayout.SOUTH, leftPanel);
		leftPanelLayout.putConstraint(SpringLayout.EAST, treeScroll, -10, SpringLayout.EAST, leftPanel);

		splitPane.setLeftComponent(leftPanel);
		leftPanel.add(treeScroll);

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		SpringLayout panelElementLayout = new SpringLayout();
		panel.setLayout(panelElementLayout);

		toolBar = new ACToolBar();
		updateButton = toolBar.makeAttributeUpdateSplitButton();
		refreshButton = toolBar.makeRefreshButton();

		advancedViewCheckbox = new ACCheckBox(Translation.get(TextID.AdvancedView), true);
		toolBar.addSeparator();
		toolBar.add(advancedViewCheckbox);

		panel.add(toolBar);
		panelElementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, panel);

		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		attributeTable = new ACTable();
		scrollPane.addComponentListener(new ScrollPaneResizeListener());
		attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		attributeTable.enableCopyPaste();
		attributeTable.enableToolTips();
		scrollPane.getViewport().add(attributeTable);
		panelElementLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		panelElementLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, panel);
		panelElementLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, panel);
		panelElementLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, panel);
		panel.add(scrollPane);

		splitPane.setDividerLocation((int) (ACMain.getScreenWidth() / 5));

		schemaTree.setCellRenderer(new ACTreeCellRenderer());
		Dimension d = errorPanel.getSize();
		d.height = 0;
		errorPanel.setSize(d);

		inMatrixCombo = new ACComboBox();
		inMatrixCombo.setRenderer(new InMatrixListCellRenderer());
		attributeTable.setDefaultEditor(InMatrixType.class, new ACCellEditor(inMatrixCombo));
		attributeTable.setDefaultRenderer(InMatrixType.class, new InMatrixTableCellRenderer());
		for (InMatrixType type : InMatrixType.values()){
			inMatrixCombo.addItem(type);
		}

		TableCellRenderer boolRenderer = attributeTable.getDefaultRenderer(Boolean.class);
		attributeTable.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolRenderer));

		setAttributeEditTableModel(new AttributeEditTableModel(null));
		setRenderers();
		setColumnWidths();
	}

	public JTree getObjectTree(){
		return schemaTree;
	}

	public JTable getAttributeTable(){
		return attributeTable;
	}

	public AttributeEditTableModel getTableModel(){
		if (attributeTable.getModel() instanceof AttributeEditTableModel){
			return (AttributeEditTableModel) attributeTable.getModel();
		}
		return null;
	}

	public void setAttributeEditTableModel(AttributeEditTableModel model){
		attributeTable.setModel(model);
		attributeTable.setRowSorter(new TableRowSorter<AttributeEditTableModel>(model));
	}

	public void stopCellEditing(){
		if (attributeTable.isEditing())
			attributeTable.getCellEditor().stopCellEditing();
	}

	public void refreshTable(){
		((AttributeEditTableModel) attributeTable.getModel()).fireTableDataChanged();
	}

	public void addCancelButtonListener(ActionListener cancelButtonListener){
		refreshButton.addActionListener(cancelButtonListener);
	}

	public void addUpdateButtonListener(ActionListener updateButtonListener){
		updateButton.addActionListener(updateButtonListener);
	}

	public void addAdvancedViewCheckboxListener(ItemListener listener){
		advancedViewCheckbox.addItemListener(listener);
	}

	public void renderErrors(ErrorContainer c){
		if (c == null)
			errorPanel.setErrorMessages(null);
		else
			renderErrors(c.getErrors());
	}

	@Override
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

	public int getSelectedRowIndex(){
		int row = attributeTable.getSelectedRow();
		if (row != -1){
			row = attributeTable.convertRowIndexToModel(row);
		}
		return row;
	}

	public ObjectAttribute getSelectedAttribute(){
		AttributeEditTableModel model = getTableModel();
		if (model != null){
			return model.getSelectedAttribute(getSelectedRowIndex());
		}
		return null;
	}

	public void setActiveTableRow(ObjectAttribute attribute){
		if (attribute == null && getTableModel() != null){
			return;
		}
		AttributeEditTableModel model = getTableModel();
		int modelIndex = model.indexOf(attribute);
		if (modelIndex >= 0){
			attributeTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = attributeTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = attributeTable.getCellRect(index, 0, true);
				attributeTable.scrollRectToVisible(r);
			}
		}

		attributeTable.requestFocusInWindow();
	}

	@Override
	public void resetEditedFields(){
		attributeTable.resetChanges();
		advancedViewCheckbox.resetChanges();
	}

	public void setTableVisible(boolean b){
		scrollPane.setVisible(b);
	}

	public void setRenderers(){
		AttributeEditTableModel model = getTableModel();
		TableColumnModel colModel = attributeTable.getColumnModel();
		for (int i = 1; i < model.getColumnCount(); i++)
			colModel.getColumn(i).setHeaderRenderer(new VerticalTableHeaderRenderer());

	}

	public void setColumnWidths(){
		Hashtable<Integer, Integer> widthConstraints = new Hashtable<Integer, Integer>();
		widthConstraints.put(1, 20);
		for (int i = 3; i <= 17; i++){
			widthConstraints.put(i, 20);
		}
		widthConstraints.put(AttributeEditTableModel.INMATRIX_COLUMN_INDEX, 40);
		widthConstraints.put(AttributeEditTableModel.AGGREGABLE_COLUMN_INDEX, 20);
		widthConstraints.put(AttributeEditTableModel.MULTIVALUE_COLUMN_INDEX, 20);

		TableColumnModel colModel = attributeTable.getColumnModel();
		Enumeration<Integer> keys = widthConstraints.keys();
		for (; keys.hasMoreElements();){
			Integer index = keys.nextElement();
			colModel.getColumn(index).setPreferredWidth(widthConstraints.get(index));
		}
		colModel.getColumn(0).setMinWidth(150);
	}

	public boolean isChanged(){
		stopCellEditing();
		return attributeTable.isChanged() || advancedViewCheckbox.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] currentPath = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (currentPath != null){
			TreePath found = new TreeModelSupport().findPathByNodes(currentPath, getObjectTree().getModel());
			getObjectTree().setSelectionPath(found);
		}
	}

	private class ScrollPaneResizeListener implements ComponentListener{

		@Override
		public void componentHidden(ComponentEvent e){

		}

		@Override
		public void componentMoved(ComponentEvent e){

		}

		@Override
		public void componentResized(ComponentEvent e){
			int w = scrollPane.getWidth();
			if (w > TABLE_MINIMUM_WIDTH)
				attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			else
				attributeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		@Override
		public void componentShown(ComponentEvent e){

		}

	}

	public int getSelectedAttributeModelIndex(){
		if (attributeTable.getSelectedRow() >= 0){
			return attributeTable.convertRowIndexToModel(attributeTable.getSelectedRow());
		}
		return -1;
	}

	public boolean isPhysicalDataTypeChanged(){
		AttributeEditTableModel aeTModel = (AttributeEditTableModel) attributeTable.getModel();
		return aeTModel.isPhysicalDataTypeChanged();
	}

	public void setAdvancedViewCheckboxState(boolean advancedView){
		advancedViewCheckbox.setSelected(advancedView);
	}

}
