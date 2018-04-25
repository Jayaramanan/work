/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.appconf;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.ErrorPanel;
import com.ni3.ag.adminconsole.client.view.ErrorRenderer;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACAutoCompleteTextArea;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACNumberCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.service.def.CalculateFormulaService;

public class PredefinedAttributeEditView extends JPanel implements AbstractView, ErrorRenderer{

	private static final long serialVersionUID = 1L;

	private ACTree schemaTree;
	private ACTable objectAttributeTable;
	private ACTable predefinedAttributeTable;
	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton refreshButton;
	private ACButton gradientButton;
	private ACButton recalculateButton;
	private ACToolBar toolBar;
	private ErrorPanel errorPanel;
	private ACAutoCompleteTextArea formulaArea;
	private JScrollPane predefinedAttrTableScroll;
	private JScrollPane formulaScrollPane;
	private HaloColorTableCellEditor haloEditor;
	private JSplitPane bottomSplit;

	private PredefinedAttributeEditView(){
	}

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
		objectAttributeTable.setSelectionModel(new AttributeTableSelectionModel());
		JScrollPane objectAttributeTableScroll = new JScrollPane();
		objectAttributeTableScroll.getViewport().add(objectAttributeTable);

		JPanel rightPanel = new JPanel();
		mainSplit.setRightComponent(rightPanel);
		SpringLayout rightPanelLayout = new SpringLayout();
		rightPanel.setLayout(rightPanelLayout);

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightSplit.setTopComponent(objectAttributeTableScroll);
		rightPanel.add(rightSplit);

		rightPanelLayout.putConstraint(SpringLayout.NORTH, rightSplit, 10, SpringLayout.NORTH, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.WEST, rightSplit, 10, SpringLayout.WEST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.EAST, rightSplit, -10, SpringLayout.EAST, rightPanel);
		rightPanelLayout.putConstraint(SpringLayout.SOUTH, rightSplit, -10, SpringLayout.SOUTH, rightPanel);

		JPanel rightBottomPanel = initPredefinedPanel();

		rightSplit.setBottomComponent(rightBottomPanel);
		rightSplit.setBorder(BorderFactory.createEmptyBorder());
		mainSplit.setDividerLocation((int) (ACMain.getScreenWidth() / 5));
		rightSplit.setDividerLocation(400);
		schemaTree.setCellRenderer(new ACTreeCellRenderer());
	}

	private JPanel initPredefinedPanel(){
		JPanel rightBottomPanel = new JPanel();
		SpringLayout rightBottomLayout = new SpringLayout();
		rightBottomPanel.setLayout(rightBottomLayout);
		toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		refreshButton = toolBar.makeRefreshButton();
		gradientButton = toolBar.makeGradientButton();
		recalculateButton = toolBar.makeRecalculateButton();
		predefinedAttributeTable = new ACTable();
		predefinedAttributeTable.enableCopyPaste();
		predefinedAttributeTable.enableToolTips();
		predefinedAttrTableScroll = new JScrollPane(predefinedAttributeTable);

		formulaArea = new ACAutoCompleteTextArea("@");
		formulaScrollPane = new JScrollPane(formulaArea);

		bottomSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		bottomSplit.setTopComponent(predefinedAttrTableScroll);
		bottomSplit.setBottomComponent(formulaScrollPane);
		bottomSplit.setBorder(BorderFactory.createEmptyBorder());

		rightBottomPanel.add(toolBar);
		rightBottomPanel.add(bottomSplit);

		rightBottomLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, rightBottomPanel);

		rightBottomLayout.putConstraint(SpringLayout.NORTH, bottomSplit, 0, SpringLayout.SOUTH, toolBar);
		rightBottomLayout.putConstraint(SpringLayout.WEST, bottomSplit, 0, SpringLayout.WEST, rightBottomPanel);
		rightBottomLayout.putConstraint(SpringLayout.EAST, bottomSplit, 0, SpringLayout.EAST, rightBottomPanel);
		rightBottomLayout.putConstraint(SpringLayout.SOUTH, bottomSplit, 0, SpringLayout.SOUTH, rightBottomPanel);

		return rightBottomPanel;
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

	public JTree getSchemaTree(){
		return schemaTree;
	}

	public PredefinedAttributeTableModel getPredefinedAttributeTableModel(){
		TableModel model = predefinedAttributeTable.getModel();
		return (model instanceof PredefinedAttributeTableModel) ? (PredefinedAttributeTableModel) model : null;
	}

	public JButton getAddButton(){
		return addButton;
	}

	public JButton getUpdateButton(){
		return updateButton;
	}

	public JButton getDeleteButton(){
		return deleteButton;
	}

	public JButton getRefreshButton(){
		return refreshButton;
	}

	public void setActiveTableRow(ObjectAttribute attribute){

		if (attribute == null){
			objectAttributeTable.clearSelection();
			return;
		}
		AttributeTableModel model = (AttributeTableModel) objectAttributeTable.getModel();

		int modelIndex = model.getIndexOf(attribute);
		if (modelIndex >= 0){
			objectAttributeTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });
		}
	}

	public void setActiveTableRow(PredefinedAttribute attribute){

		if (attribute == null){
			predefinedAttributeTable.clearSelection();
			return;
		}
		PredefinedAttributeTableModel model = (PredefinedAttributeTableModel) predefinedAttributeTable.getModel();

		int modelIndex = model.indexOf(attribute);
		if (modelIndex >= 0){
			predefinedAttributeTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = predefinedAttributeTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = predefinedAttributeTable.getCellRect(index, 0, true);
				predefinedAttributeTable.scrollRectToVisible(r);
			}
		}

		predefinedAttributeTable.requestFocusInWindow();
	}

	public int getSelectedAttributeModelIndex(){
		if (objectAttributeTable.getSelectedRow() >= 0
		        && objectAttributeTable.getSelectedRow() < objectAttributeTable.getRowCount()){
			return objectAttributeTable.convertRowIndexToModel(objectAttributeTable.getSelectedRow());
		}
		return -1;
	}

	public ObjectAttribute getSelectedAttribute(){
		int index = getSelectedAttributeModelIndex();
		return ((AttributeTableModel) objectAttributeTable.getModel()).getAttribute(index);
	}

	public int getSelectedPredefinedAttributeModelIndex(){
		if (predefinedAttributeTable.getSelectedRow() >= 0){
			return predefinedAttributeTable.convertRowIndexToModel(predefinedAttributeTable.getSelectedRow());
		}
		return -1;
	}

	public void stopCellEditing(){
		if (predefinedAttributeTable.isEditing()){
			predefinedAttributeTable.getCellEditor().stopCellEditing();
		}
	}

	public List<int[]> getSelectedCellIndexes(){
		List<int[]> ret = new ArrayList<int[]>();
		ret.add(objectAttributeTable.getSelectedCellIndexes());
		ret.add(predefinedAttributeTable.getSelectedCellIndexes());
		return ret;
	}

	@Override
	public void resetEditedFields(){
		predefinedAttributeTable.resetChanges();
		formulaArea.resetChanges();
	}

	public void setPredefinedAttributeTableModel(PredefinedAttributeTableModel tableModel){
		predefinedAttributeTable.setModel(tableModel);
		predefinedAttributeTable.setRowSorter(new TableRowSorter<PredefinedAttributeTableModel>(tableModel));

		TableColumn haloColumn = predefinedAttributeTable.getColumnModel().getColumn(
		        PredefinedAttributeTableModel.ATTRIBUTE_COLOR_COLUMN_INDEX);
		haloColumn.setCellRenderer(new HaloColorTableCellRenderer());
		haloEditor = new HaloColorTableCellEditor();
		haloColumn.setCellEditor(haloEditor);
		TableColumn parentColumn = predefinedAttributeTable.getColumnModel().getColumn(
		        PredefinedAttributeTableModel.ATTRIBUTE_PARENT_COLOR_INDEX);
		parentColumn.setCellEditor(new ACNumberCellEditor());
	}

	@Override
	public boolean isChanged(){
		stopCellEditing();
		return predefinedAttributeTable.isChanged() || formulaArea.isChanged();
	}

	@Override
	public void restoreSelection(){
		ObjectHolder holder = ObjectHolder.getInstance();
		Object[] current = holder.getMaxPath(new Class<?>[] { Schema.class, ObjectDefinition.class });
		if (current != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(current, getSchemaTree().getModel());
			getSchemaTree().setSelectionPath(found);
		}
	}

	public ListSelectionModel getObjectAttributeListSelectionModel(){
		return objectAttributeTable.getSelectionModel();
	}

	public void addObjectAttributeTableListener(ListSelectionListener listener){
		getObjectAttributeListSelectionModel().addListSelectionListener(listener);
	}

	public void addGradientButtonListener(ActionListener l){
		gradientButton.addActionListener(l);
	}
	
	public void addRecalculateButtonListener(ActionListener l){
		recalculateButton.addActionListener(l);
	}

	public void setFormula(Formula formula){
		if (formula != null)
			formulaArea.setText(formula.getFormula());
		else
			formulaArea.setText("");
	}

	public void setVisibility(boolean isFormula, boolean isPredefined){
		boolean adjustSplitPane = predefinedAttrTableScroll.isVisible() != isPredefined
		        || formulaScrollPane.isVisible() != isFormula;
		predefinedAttrTableScroll.setVisible(isPredefined);
		addButton.setVisible(isPredefined);
		deleteButton.setVisible(isPredefined);
		gradientButton.setVisible(isPredefined);
		recalculateButton.setVisible(isFormula);
		formulaScrollPane.setVisible(isFormula);
		if (adjustSplitPane){
			double location = (isFormula && isPredefined) ? 0.5 : (isPredefined ? 1.0 : 0.0);
			bottomSplit.setDividerLocation(location);
		}
	}

	public String getFormulaText(){
		return formulaArea.getText();
	}

	public void setAutoCompleteItems(List<ObjectAttribute> objectAttributes){
		formulaArea.setAutoCompleteItems(objectAttributes);
	}

	public AttributeTableModel getAttributeTableModel(){
		return (AttributeTableModel) objectAttributeTable.getModel();
	}

	public void setAttributeTableModel(AttributeTableModel attributeTableModel){
		objectAttributeTable.setModel(attributeTableModel);
		objectAttributeTable.setRowSorter(new TableRowSorter<AttributeTableModel>(attributeTableModel));
	}

	public void refreshAttributeTable(){
		getAttributeTableModel().fireTableDataChanged();
		objectAttributeTable.tableChanged(new TableModelEvent(getAttributeTableModel(), TableModelEvent.ALL_COLUMNS));
		objectAttributeTable.invalidate();
	}

	public void refreshPredefinedTable(){
		getPredefinedAttributeTableModel().fireTableDataChanged();
		predefinedAttributeTable.invalidate();
	}

	public int getPredefinedAttributeModelIndex(int idx){
		return predefinedAttributeTable.convertRowIndexToModel(idx);
	}

	public PredefinedAttribute getSelectedPredefinedAttribute(){
		int index = getSelectedPredefinedAttributeModelIndex();
		return ((PredefinedAttributeTableModel) predefinedAttributeTable.getModel()).getPredefinedAttribute(index);
	}

	public void addHaloCellEditorListener(CellEditorListener listener){
		haloEditor.addCellEditorListener(listener);
		TableColumn column = predefinedAttributeTable.getColumnModel().getColumn(
		        PredefinedAttributeTableModel.ATTRIBUTE_PARENT_COLOR_INDEX);
		column.getCellEditor().addCellEditorListener(listener);
	}

	public ListSelectionModel getPredefinedAttributeListSelectionModel(){
		return predefinedAttributeTable.getSelectionModel();
	}

}
