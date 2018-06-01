/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.JComboBox.KeySelectionManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AbstractDocument;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.*;
import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ObjectDefinitionRightPanel extends JPanel{
	private static final long serialVersionUID = -3809400341585659009L;
	private static final String SCHEMA_ADMIN = "schemaAdmin";
	private static final int NAME_COLUMN_INDEX = 0;
	private static final int DATASOURCE_COLUMN_INDEX = 5;
	private ACMandatoryLabel labelObjectName;
	private ACTextField objectName;
	private JTextField creationDate;
	private JTextField createdBy;
	private ACTextField description;
	private ACTextField sort;

	private ACComboBox objectTypeCombo;
	private ACComboBox tableObjectTypeCombo;

	private ACTable attributesTable;
	private ACTable objectsTable;

	private JScrollPane scrollPaneAttribute;
	private JScrollPane scrollPaneObject;
	private ACButton addButton;
	private ACSplitButton updateButton;
	private ACSplitButton deleteButton;
	private ACButton refreshButton;

	private ACComboBox dataTypeCombo;
	private ACComboBox predefinedCombo;

	private JLabel labelAttribute;
	private JLabel lblObjectType;
	private JLabel labelSort;
	private JLabel labelCreationDate;
	private JLabel labelCreatedBy;
	private JLabel labelDescription;

	private ChangeResetable[] changeResetableComponents;
	private DataSourceCellEditor dataSourceCellEditor;

	public ObjectDefinitionRightPanel(){
		super();
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		ACToolBar toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteSplitButton();
		updateButton = toolBar.makeUpdateSplitButton();
		refreshButton = toolBar.makeRefreshButton();
		add(toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		objectName = new ACTextField();
		elementLayout.putConstraint(SpringLayout.NORTH, objectName, 10, SpringLayout.SOUTH, toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, objectName, 100, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.EAST, objectName, 300, SpringLayout.WEST, this);
		this.add(objectName);
		((AbstractDocument) objectName.getDocument()).setDocumentFilter(new ObjectNameDocumentFilter());

		labelObjectName = new ACMandatoryLabel();
		elementLayout.putConstraint(SpringLayout.NORTH, labelObjectName, 0, SpringLayout.NORTH, objectName);
		elementLayout.putConstraint(SpringLayout.EAST, labelObjectName, -5, SpringLayout.WEST, objectName);
		labelObjectName.setHorizontalAlignment(SwingConstants.LEFT);
		labelObjectName.setText(Translation.get(TextID.ObjectName));
		this.add(labelObjectName);

		creationDate = new JTextField();
		this.add(creationDate);
		creationDate.setEditable(false);
		creationDate.setFocusable(false);
		elementLayout.putConstraint(SpringLayout.EAST, creationDate, 600, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.WEST, creationDate, 400, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, creationDate, 0, SpringLayout.NORTH, objectName);

		labelCreationDate = new JLabel();
		elementLayout.putConstraint(SpringLayout.NORTH, labelCreationDate, 0, SpringLayout.NORTH, objectName);
		elementLayout.putConstraint(SpringLayout.EAST, labelCreationDate, -5, SpringLayout.WEST, creationDate);
		labelCreationDate.setHorizontalAlignment(SwingConstants.LEFT);
		labelCreationDate.setText(Translation.get(TextID.CreationDate));
		this.add(labelCreationDate);

		createdBy = new JTextField();
		this.add(createdBy);
		createdBy.setEditable(false);
		createdBy.setFocusable(false);
		elementLayout.putConstraint(SpringLayout.EAST, createdBy, -10, SpringLayout.EAST, this);
		elementLayout.putConstraint(SpringLayout.WEST, createdBy, 700, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, createdBy, 0, SpringLayout.NORTH, objectName);

		labelCreatedBy = new JLabel();
		labelCreatedBy.setHorizontalAlignment(SwingConstants.LEFT);
		labelCreatedBy.setText(Translation.get(TextID.CreatedBy));
		this.add(labelCreatedBy);
		elementLayout.putConstraint(SpringLayout.EAST, labelCreatedBy, -5, SpringLayout.WEST, createdBy);
		elementLayout.putConstraint(SpringLayout.NORTH, labelCreatedBy, 0, SpringLayout.NORTH, objectName);

		description = new ACTextField();
		elementLayout.putConstraint(SpringLayout.NORTH, description, 20, SpringLayout.SOUTH, objectName);
		elementLayout.putConstraint(SpringLayout.WEST, description, 0, SpringLayout.WEST, objectName);
		elementLayout.putConstraint(SpringLayout.EAST, description, -10, SpringLayout.EAST, this);
		this.add(description);

		labelDescription = new JLabel();
		elementLayout.putConstraint(SpringLayout.EAST, labelDescription, -5, SpringLayout.WEST, description);
		elementLayout.putConstraint(SpringLayout.NORTH, labelDescription, 0, SpringLayout.NORTH, description);
		labelDescription.setHorizontalAlignment(SwingConstants.LEFT);
		labelDescription.setText(Translation.get(TextID.Description));
		this.add(labelDescription);

		objectTypeCombo = new ACComboBox();
		ObjectTypeRenderer rnd = new ObjectTypeRenderer();
		objectTypeCombo.setRenderer(rnd);
		objectTypeCombo.setKeySelectionManager(new KeySelectionManager(){

			@Override
			public int selectionForKey(char aKey, ComboBoxModel aModel){
				int i, c;
				int currentSelection = -1;
				Object selectedItem = aModel.getSelectedItem();
				String v;
				String pattern;
				if (selectedItem != null){
					for (i = 0, c = aModel.getSize(); i < c; i++){
						if (selectedItem == aModel.getElementAt(i)){
							currentSelection = i;
							break;
						}
					}
				}

				pattern = ("" + aKey).toLowerCase();
				aKey = pattern.charAt(0);

				for (i = ++currentSelection, c = aModel.getSize(); i < c; i++){
					ObjectType elem = (ObjectType) aModel.getElementAt(i);
					if (elem != null && elem.toString() != null){
						v = elem.getLabel().toLowerCase();
						if (v.length() > 0 && v.charAt(0) == aKey)
							return i;
					}
				}

				for (i = 0; i < currentSelection; i++){
					ObjectType elem = (ObjectType) aModel.getElementAt(i);
					if (elem != null && elem.toString() != null){
						v = elem.getLabel().toLowerCase();
						if (v.length() > 0 && v.charAt(0) == aKey)
							return i;
					}
				}
				return -1;
			}

		});
		elementLayout.putConstraint(SpringLayout.WEST, objectTypeCombo, 0, SpringLayout.WEST, objectName);
		elementLayout.putConstraint(SpringLayout.EAST, objectTypeCombo, 0, SpringLayout.EAST, objectName);
		elementLayout.putConstraint(SpringLayout.NORTH, objectTypeCombo, 20, SpringLayout.SOUTH, description);
		this.add(objectTypeCombo);

		lblObjectType = new JLabel();
		elementLayout.putConstraint(SpringLayout.EAST, lblObjectType, -5, SpringLayout.WEST, objectTypeCombo);
		elementLayout.putConstraint(SpringLayout.NORTH, lblObjectType, 0, SpringLayout.NORTH, objectTypeCombo);
		lblObjectType.setText(Translation.get(TextID.ObjectType));
		lblObjectType.setHorizontalAlignment(SwingConstants.LEFT);
		add(lblObjectType);

		sort = new ACTextField(){
			private static final long serialVersionUID = 1L;

			@Override
			public void setText(String t){
				if (t == null || "".equals(t))
					t = "0";
				super.setText(t);
			}

			@Override
			public String getText(){
				String ret = super.getText();
				if (ret == null || "".equals(ret)){
					ret = "0";
				}
				return ret;
			}
		};
		((AbstractDocument) sort.getDocument()).setDocumentFilter(new NumberDocumentFilter());
		this.add(sort);
		elementLayout.putConstraint(SpringLayout.EAST, sort, 0, SpringLayout.EAST, creationDate);
		elementLayout.putConstraint(SpringLayout.WEST, sort, 0, SpringLayout.WEST, creationDate);
		elementLayout.putConstraint(SpringLayout.NORTH, sort, 0, SpringLayout.NORTH, objectTypeCombo);

		labelSort = new JLabel();
		labelSort.setHorizontalAlignment(SwingConstants.LEFT);
		labelSort.setText(Translation.get(TextID.Sort));
		this.add(labelSort);
		elementLayout.putConstraint(SpringLayout.EAST, labelSort, -5, SpringLayout.WEST, sort);
		elementLayout.putConstraint(SpringLayout.NORTH, labelSort, 0, SpringLayout.NORTH, sort);

		attributesTable = new ACTable();

		dataTypeCombo = new ACComboBox();
		dataTypeCombo.setRenderer(new DataTypeListCellRenderer());
		attributesTable.setDefaultEditor(DataType.class, new ACCellEditor(dataTypeCombo));
		attributesTable.setDefaultRenderer(DataType.class, new DataTypeRenderer());
		TableCellRenderer boolCellRenderer = attributesTable.getDefaultRenderer(Boolean.class);
		attributesTable.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));
		predefinedCombo = new ACComboBox();
		attributesTable.setDefaultEditor(ValueListType.class, new ACCellEditor(predefinedCombo));
		attributesTable.enableCopyPaste();
		attributesTable.enableToolTips();
		attributesTable.getActionMap().put(ACTable.PASTE, new SchemaAdminPasteAction(attributesTable));
		dataSourceCellEditor = new DataSourceCellEditor();

		scrollPaneAttribute = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneObject = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		elementLayout.putConstraint(SpringLayout.EAST, scrollPaneAttribute, -10, SpringLayout.EAST, this);
		elementLayout.putConstraint(SpringLayout.WEST, scrollPaneAttribute, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.SOUTH, scrollPaneAttribute, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.NORTH, scrollPaneAttribute, 40, SpringLayout.SOUTH, objectTypeCombo);
		this.add(scrollPaneAttribute);
		scrollPaneAttribute.setViewportView(attributesTable);

		elementLayout.putConstraint(SpringLayout.EAST, scrollPaneObject, -10, SpringLayout.EAST, this);
		elementLayout.putConstraint(SpringLayout.WEST, scrollPaneObject, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.SOUTH, scrollPaneObject, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.NORTH, scrollPaneObject, 0, SpringLayout.NORTH, objectTypeCombo);
		this.add(scrollPaneObject);

		tableObjectTypeCombo = new ACComboBox();
		tableObjectTypeCombo.setRenderer(new ObjectTypeRenderer());
		objectsTable = new ACTable();
		objectsTable.setDefaultEditor(ObjectType.class, new ACCellEditor(tableObjectTypeCombo));
		objectsTable.setDefaultRenderer(ObjectType.class, new TableObjectTypeRenderer());
		objectsTable.enableCopyPaste();
		objectsTable.enableToolTips();
		scrollPaneObject.setViewportView(objectsTable);

		labelAttribute = new JLabel();
		elementLayout.putConstraint(SpringLayout.SOUTH, labelAttribute, -10, SpringLayout.NORTH, scrollPaneAttribute);
		labelAttribute.setHorizontalAlignment(SwingConstants.LEFT);
		labelAttribute.setText(Translation.get(TextID.Attribute));
		this.add(labelAttribute);
		elementLayout.putConstraint(SpringLayout.WEST, labelAttribute, 10, SpringLayout.WEST, this);

		// ========
		// HIDE TABLE WHEN OPENNING APPLICATION
		setPanelToSchemaInfo(true);

		changeResetableComponents = new ChangeResetable[] { objectName, description, sort, attributesTable, objectTypeCombo,
		        objectsTable };

		objectName.setName(SCHEMA_ADMIN + "_objectName");
		objectTypeCombo.setName(SCHEMA_ADMIN + "_objectType");
		creationDate.setName(SCHEMA_ADMIN + "_creationDate");
		description.setName(SCHEMA_ADMIN + "_description");
		createdBy.setName(SCHEMA_ADMIN + "_createdBy");
		sort.setName(SCHEMA_ADMIN + "_sort");
		attributesTable.setName(SCHEMA_ADMIN + "_attributeTable");
		addButton.setName(SCHEMA_ADMIN + "_addAttribute");
		updateButton.setName(SCHEMA_ADMIN + "_updateAttributes");
		deleteButton.setName(SCHEMA_ADMIN + "_deleteAttribute");
		refreshButton.setName(SCHEMA_ADMIN + "_refreshAttributes");

		fillObjectTypeCombo();
		fillDataTypeCombo();
	}

	public void setPanelToSchemaInfo(boolean set){
		scrollPaneAttribute.setVisible(!set);
		scrollPaneObject.setVisible(set);
		addButton.setVisible(!set);
		deleteButton.setVisible(!set);
		labelAttribute.setVisible(!set);
	}

	public JTextField getObjectName(){
		return objectName;
	}

	public JTextField getCreationDate(){
		return creationDate;
	}

	public JTextField getCreatedBy(){
		return createdBy;
	}

	public JTextField getDescription(){
		return description;
	}

	public JTextField getSort(){
		return sort;
	}

	public ObjectAttributeTableModel getTableModel(){
		if (attributesTable.getModel() instanceof ObjectAttributeTableModel){
			return (ObjectAttributeTableModel) attributesTable.getModel();
		}
		return null;
	}

	public void setTableModel(ObjectAttributeTableModel model){
		attributesTable.setModel(model);
		attributesTable.setRowSorter(new TableRowSorter<ObjectAttributeTableModel>(model));

		JTextField tf = new JTextField();
		((AbstractDocument) tf.getDocument()).setDocumentFilter(new AttributeFilter());
		attributesTable.getColumnModel().getColumn(NAME_COLUMN_INDEX).setCellEditor(new ACTextCellEditor(tf));
		attributesTable.getColumnModel().getColumn(DATASOURCE_COLUMN_INDEX).setCellEditor(dataSourceCellEditor);
		attributesTable.repaint();
	}

	public ACComboBox getObjectTypeCombo(){
		return objectTypeCombo;
	}

	public void setPredefinedReferenceData(List<Integer> predefinedTypes){
		predefinedCombo.removeAllItems();
		if (predefinedCombo == null)
			return;
		for (Integer predef : predefinedTypes){
			predefinedCombo.addItem(ValueListType.valueOf(predef));
		}
	}

	public void fillDataTypeCombo(){
		dataTypeCombo.removeAllItems();
		for (DataType dataType : DataType.values()){
			if (dataType != DataType.BOOL){ // AC-1566 hide bool datatype
				dataTypeCombo.addItem(dataType);
			}
		}
	}

	public void fillObjectTypeCombo(){
		objectTypeCombo.removeAllItems();
		for (ObjectType objectType : ObjectType.values()){
			objectTypeCombo.addItem(objectType);
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
		refreshButton.addActionListener(actionListener);
	}

	public int getSelectedRowIndex(){
		int rowIndex = attributesTable.getSelectedRow();
		if (rowIndex >= 0){
			return attributesTable.convertRowIndexToModel(rowIndex);
		}
		return -1;
	}

	public int[] getSelectedRowIndexes(){
		int[] selectedRows = attributesTable.getSelectedRows();
		int[] modelRows = new int[selectedRows.length];
		for (int row = 0; row < selectedRows.length; row++){
			modelRows[row] = attributesTable.convertRowIndexToModel(selectedRows[row]);
		}
		return modelRows;
	}

	public int getSelectedObjectTableRowIndex(){
		if (objectsTable.getSelectedRow() >= 0){
			return objectsTable.convertRowIndexToModel(objectsTable.getSelectedRow());
		}
		return -1;
	}

	public ObjectAttribute getSelectedAttribute(){
		ObjectAttributeTableModel tableModel = getTableModel();
		return tableModel != null ? tableModel.getSelectedAttribute(getSelectedRowIndex()) : null;
	}

	public void stopEditing(){
		if (attributesTable.isEditing()){
			attributesTable.getCellEditor().stopCellEditing();
		}
		if (objectsTable.isEditing()){
			objectsTable.getCellEditor().stopCellEditing();
		}
	}

	public void setActiveTableRow(ObjectAttribute attribute){
		ObjectAttributeTableModel model = getTableModel();
		int modelIndex = model.indexOf(attribute);
		if (modelIndex >= 0){
			attributesTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = attributesTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = attributesTable.getCellRect(index, 0, true);
				attributesTable.scrollRectToVisible(r);
			}
		}

		attributesTable.requestFocusInWindow();
	}

	public void setActiveObjectTableRow(ObjectDefinition object){
		if (object == null)
			return;
		ObjectTableModel tableModel = getObjectTableModel();
		int modelIndex = tableModel.indexOf(object);
		if (modelIndex >= 0){
			objectsTable.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = objectsTable.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = objectsTable.getCellRect(index, 0, true);
				objectsTable.scrollRectToVisible(r);
			}
		}
		objectsTable.requestFocusInWindow();
	}

	public void setVisibleFields(boolean visible){
		objectTypeCombo.setVisible(visible);
		lblObjectType.setVisible(visible);
		sort.setVisible(visible);
		labelSort.setVisible(visible);
	}

	public void setObjectNameLabelString(String objectNameLabelString){
		labelObjectName.setText(objectNameLabelString);
	}

	public void setDescriptionEnabled(boolean b){
		description.setEnabled(b);
	}

	public ACTable getAttributesTable(){
		return attributesTable;
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return changeResetableComponents;
	}

	public void setObjectTableModel(ObjectTableModel objectTableModel){
		objectsTable.setModel(objectTableModel);
		objectsTable.setRowSorter(new TableRowSorter<ObjectTableModel>(objectTableModel));
	}

	public ObjectTableModel getObjectTableModel(){
		TableModel model = objectsTable.getModel();
		if (model != null && model instanceof ObjectTableModel)
			return (ObjectTableModel) model;
		return null;
	}

	public void setTableObjectTypeReferenceData(List<ObjectType> objectTypes){
		tableObjectTypeCombo.removeAllItems();
		if (objectTypes == null)
			return;
		for (ObjectType objectType : objectTypes)
			tableObjectTypeCombo.addItem(objectType);
	}

	public ObjectDefinition getSelectedObject(){
		ObjectTableModel tableModel = getObjectTableModel();
		return tableModel != null ? tableModel.getSelectedObject(getSelectedObjectTableRowIndex()) : null;
	}

	public void resetLabels(){
		labelObjectName.setText(Translation.get(TextID.ObjectName));
		labelCreationDate.setText(Translation.get(TextID.CreationDate));
		labelCreatedBy.setText(Translation.get(TextID.CreatedBy));
		labelDescription.setText(Translation.get(TextID.Description));
		lblObjectType.setText(Translation.get(TextID.ObjectType));
		labelSort.setText(Translation.get(TextID.Sort));
		labelAttribute.setText(Translation.get(TextID.Attribute));
		addButton.setToolTipText(Translation.get(TextID.Add));
		updateButton.resetLabels(new String[] { Translation.get(TextID.Update), Translation.get(TextID.Update),
		        Translation.get(TextID.UpdateLiveData) });
		deleteButton.resetLabels(new String[] { Translation.get(TextID.Delete), Translation.get(TextID.Delete),
		        Translation.get(TextID.CascadeDelete) });
		refreshButton.setToolTipText(Translation.get(TextID.Refresh));
	}

	public void setDataSources(List<com.ni3.ag.adminconsole.domain.DataSource> dataSources){
		dataSourceCellEditor.setData(dataSources);
	}

	public void setDataSourceCellEditor(){
		attributesTable.getColumnModel().getColumn(DATASOURCE_COLUMN_INDEX).setCellEditor(dataSourceCellEditor);
	}
}
