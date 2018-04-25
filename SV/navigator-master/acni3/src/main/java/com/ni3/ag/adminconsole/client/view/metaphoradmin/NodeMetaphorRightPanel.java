/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.metaphoradmin;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.*;
import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class NodeMetaphorRightPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	private JScrollPane scrollPane;
	private ACTable tableMetaphor;
	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton cancelButton;

	private ACButton addSetButton;
	private ACButton deleteSetButton;
	private ACButton copySetButton;
	private ACButton addIconButton;
	private ACButton deleteIconButton;

	private ACToolBar toolBar;

	private ACComboBox iconCombo;

	private JLabel metaphorSetLabel;
	private JComboBox metaphorSetCombo;

	private ChangeResetable[] changeResetableComponents;

	public NodeMetaphorRightPanel(){
		initializeComponents();
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		toolBar = new ACToolBar();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();

		toolBar.addSeparator();

		copySetButton = toolBar.makeCopyMetaphorSetButton();
		addSetButton = toolBar.makeAddMetaphorSetButton();
		deleteSetButton = toolBar.makeDeleteMetaphorSetButton();

		toolBar.addSeparator();

		addIconButton = toolBar.makeAddIconButton();
		deleteIconButton = toolBar.makeDeleteIconButton();

		toolBar.addSeparator();

		metaphorSetCombo = new JComboBox();
		metaphorSetLabel = new JLabel(Translation.get(TextID.MetaphorSet));
		metaphorSetLabel.setLabelFor(metaphorSetCombo);

		toolBar.add(metaphorSetLabel);
		toolBar.add(metaphorSetCombo);
		metaphorSetCombo.setMaximumSize(new Dimension(250, 20));
		metaphorSetCombo.setPreferredSize(new Dimension(250, 20));
		metaphorSetLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		add(toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		scrollPane = new JScrollPane();
		tableMetaphor = new ACTable();
		tableMetaphor.enableCopyPaste();

		scrollPane.setViewportView(tableMetaphor);
		this.add(scrollPane);

		elementLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		elementLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);

		iconCombo = new ACComboBox();
		iconCombo.setRenderer(new IconListCellRenderer());
		iconCombo.setFocusable(false);
		tableMetaphor.setDefaultEditor(Icon.class, new ACCellEditor(iconCombo));
		tableMetaphor.setDefaultRenderer(Icon.class, new IconTableCellRenderer());

		changeResetableComponents = new ChangeResetable[]{tableMetaphor};
	}

	public void setTableModel(NodeMetaphorTableModel model){
		tableMetaphor.setModel(model);
		tableMetaphor.setRowSorter(new TableRowSorter<NodeMetaphorTableModel>(model));
	}

	public NodeMetaphorTableModel getTableModel(){
		return (NodeMetaphorTableModel) tableMetaphor.getModel();
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

	public void addMetaphorSetComboListener(ActionListener actionListener){
		metaphorSetCombo.addActionListener(actionListener);
	}

	public void addAddSetButtonActionListener(ActionListener actionListener){
		addSetButton.addActionListener(actionListener);
	}

	public void addDeleteSetButtonActionListener(ActionListener actionListener){
		deleteSetButton.addActionListener(actionListener);
	}

	public void addCopySetButtonActionListener(ActionListener actionListener){
		copySetButton.addActionListener(actionListener);
	}

	public void addAddIconButtonActionListener(ActionListener actionListener){
		addIconButton.addActionListener(actionListener);
	}

	public void addDeleteIconButtonActionListener(ActionListener actionListener){
		deleteIconButton.addActionListener(actionListener);
	}

	public void setIconReferenceData(List<Icon> iconList){
		iconCombo.removeAllItems();
		if (iconList == null){
			return;
		}
		Collections.sort(iconList, new Comparator<Icon>(){
			@Override
			public int compare(Icon o1, Icon o2){
				if (o1.getIconName() == null || o2.getIconName() == null)
					return 0;
				return o1.getIconName().compareTo(o2.getIconName());
			}
		});
		for (Icon icon : iconList){
			iconCombo.addItem(icon);
		}
	}

	public void setMetaphorSetReferenceData(List<String> metaphorSets){
		metaphorSetCombo.removeAllItems();
		metaphorSetCombo.addItem(null);
		for (String set : metaphorSets){
			metaphorSetCombo.addItem(set);
		}
	}

	public JComboBox getMetaphorSetCombo(){
		return metaphorSetCombo;
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public void stopCellEditing(){
		if (tableMetaphor.isEditing())
			tableMetaphor.getCellEditor().stopCellEditing();
	}

	public int getSelectedRowIndex(){
		if (tableMetaphor.getSelectedRow() >= 0){
			return tableMetaphor.convertRowIndexToModel(tableMetaphor.getSelectedRow());
		}
		return -1;
	}

	public Metaphor getSelectedNodeMetaphor(){
		return getTableModel().getSelectedRowData(getSelectedRowIndex());
	}

	public void setActiveTableRow(Metaphor nodeMetaphor){
		NodeMetaphorTableModel model = getTableModel();
		int modelIndex = model.indexOf(nodeMetaphor);
		if (modelIndex >= 0){
			int index = tableMetaphor.convertRowIndexToView(modelIndex);

			if (index >= 0){
				tableMetaphor.getSelectionModel().setSelectionInterval(index, index);
				Rectangle r = tableMetaphor.getCellRect(index, 0, true);
				tableMetaphor.scrollRectToVisible(r);
			}
		}

		tableMetaphor.requestFocusInWindow();
	}

	public void setCellRenderersAndEditors(List<ObjectAttribute> attributes){
		if (attributes == null || attributes.isEmpty()){
			return;
		}
		for (int i = 0; i < attributes.size(); i++){
			List<PredefinedAttribute> columnValues = attributes.get(i).getPredefinedAttributes();
			ACComboBox combo = new ACComboBox();
			combo.addItem(null);
			for (PredefinedAttribute value : columnValues){
				combo.addItem(value);
			}
			combo.setRenderer(new PredefinedAttributeListCellRenderer());
			TableColumn column = tableMetaphor.getColumnModel().getColumn(i + NodeMetaphorTableModel.FIRST_DYNAMIC_COLUMN);
			column.setCellRenderer(new PredefinedAttributeRenderer());
			column.setCellEditor(new ACCellEditor(combo));
		}
	}

	public void setTableColumnModel(TableColumnModel columnModel){
		tableMetaphor.setColumnModel(columnModel);
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return changeResetableComponents;
	}

	public void resizeTableColumns(){
		tableMetaphor.resizeColumns();
	}

}