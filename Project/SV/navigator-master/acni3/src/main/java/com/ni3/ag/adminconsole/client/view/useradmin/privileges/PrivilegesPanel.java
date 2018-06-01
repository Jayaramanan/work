/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin.privileges;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeModel;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCheckBox;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.client.view.common.treetable.ACTreeTable;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class PrivilegesPanel extends JPanel{

	private static final long serialVersionUID = 7878156607985350612L;

	private ACTreeTable privilegesTreeTable;

	private ACButton updateButton;
	private ACButton cancelButton;

	private ACToolBar toolBar;

	private ACButton grantPrivilegesButton;
	private ACButton expandTreeButton;
	private ACCheckBox configLockedCheckbox;

    private ChangeResetable[] resettableComponents;

	public PrivilegesPanel(){
		initializeComponents();
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		this.setLayout(elementLayout);

		toolBar = new ACToolBar();
		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();
		toolBar.addSeparator();
		grantPrivilegesButton = toolBar.makeGrantPrivilegesButton();
		expandTreeButton = toolBar.makeExpandTreeButton();

		configLockedCheckbox = new ACCheckBox(Translation.get(TextID.ConfigureLockedObjects));
		toolBar.addSeparator();
		toolBar.add(configLockedCheckbox);

		add(toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		privilegesTreeTable = new ACTreeTable();
		privilegesTreeTable.enableCopyPaste();
		privilegesTreeTable.setDefaultRenderer(Boolean.class, new BooleanPrivilegesCellRenderer());
		privilegesTreeTable.setDefaultRenderer(EditingOption.class, new EditingOptionTableCellRenderer());
		privilegesTreeTable.setDefaultEditor(EditingOption.class, new EditingOptionTableCellEditor());

		JScrollPane treeScrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		treeScrollPane.setViewportView(privilegesTreeTable);
		add(treeScrollPane);

		elementLayout.putConstraint(SpringLayout.WEST, treeScrollPane, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, treeScrollPane, 0, SpringLayout.SOUTH, toolBar);
		elementLayout.putConstraint(SpringLayout.SOUTH, treeScrollPane, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, treeScrollPane, -10, SpringLayout.EAST, this);

        resettableComponents = new ChangeResetable[] { privilegesTreeTable, configLockedCheckbox };
	}

	public JTree getTree(){
		return privilegesTreeTable.getTree();
	}

	public void addUpdateButtonActionListener(ActionListener actionListener){
		updateButton.addActionListener(actionListener);
	}

	public void addRefreshButtonActionListener(ActionListener actionListener){
		cancelButton.addActionListener(actionListener);
	}

	public void addPrivilegesButtonActionListener(ActionListener actionListener){
		grantPrivilegesButton.addActionListener(actionListener);
	}

	public void addTreeExpansionListener(ActionListener actionListener){
		expandTreeButton.addActionListener(actionListener);
	}

	public void addConfigLockedCheckboxListener(ItemListener listener){
		configLockedCheckbox.addItemListener(listener);
	}

	public void setGroupPrivilegesTreeModel(GroupPrivilegesTreeTableModel model, Group group, boolean showLockedColumns){
		TableModel tableModel = privilegesTreeTable.getModel();
		if (tableModel == null || !(tableModel instanceof GroupPrivilegesTableModel)
		        || (showLockedColumns != tableModel.getColumnCount() > 5)){
			tableModel = new GroupPrivilegesTableModel(privilegesTreeTable.getTree(), group, model.getSchemas(),
			        showLockedColumns);
		} else{
			((GroupPrivilegesTableModel) tableModel).setData(privilegesTreeTable.getTree(), group, model.getSchemas());
		}

		privilegesTreeTable.setModel(model, (ACTableModel) tableModel);
		if (tableModel instanceof GroupPrivilegesTableModel)
			((GroupPrivilegesTableModel) tableModel).setTreeModelListener();
	}

	public GroupPrivilegesTableModel getTableModel(){
		if (privilegesTreeTable.getModel() instanceof GroupPrivilegesTableModel){
			return (GroupPrivilegesTableModel) privilegesTreeTable.getModel();
		}
		return null;
	}

	public void refreshTables(){
		GroupPrivilegesTableModel tableModel = getTableModel();
		if (tableModel != null)
			tableModel.fireTableDataChanged();
	}

	public int getSelectedRow(){
		if (privilegesTreeTable.getSelectedRow() >= 0){
			return privilegesTreeTable.convertRowIndexToModel(privilegesTreeTable.getSelectedRow());
		}
		return -1;
	}

	public Object getSelectedTreeObject(){
		int selectedRow = getSelectedRow();
		GroupPrivilegesTableModel tableModel = getTableModel();
		if (selectedRow >= 0 && tableModel != null){
			return tableModel.nodeForRow(selectedRow);
		}
		return null;
	}

	public boolean isSelectedTreeObjectEditable(){
		int selectedRow = getSelectedRow();
		GroupPrivilegesTableModel tableModel = getTableModel();
		return tableModel.isCellEditable(selectedRow, 1);
	}

	public GroupPrivilegesTreeTableModel getGroupPrivilegesTreeModel(){
		TreeModel ret = privilegesTreeTable.getTree().getModel();
		if (ret instanceof GroupPrivilegesTreeTableModel)
			return (GroupPrivilegesTreeTableModel) ret;
		return null;
	}

	public void setActiveTableRow(int row, int cellIndex){
		privilegesTreeTable.setSelectedCellIndexes(new int[] { row, row, cellIndex, cellIndex });
		int index = privilegesTreeTable.convertRowIndexToView(row);
		if (index >= 0){
			Rectangle r = privilegesTreeTable.getCellRect(index, 0, true);
			privilegesTreeTable.scrollRectToVisible(r);
		}
	}

	public int getSelectedColumn(){
		if (privilegesTreeTable.getSelectedColumn() >= 0){
			return privilegesTreeTable.convertColumnIndexToModel(privilegesTreeTable.getSelectedColumn());
		}
		return -1;
	}

	public void updateTreeTable(){
		ACTableModel atm = (ACTableModel) privilegesTreeTable.getModel();
		atm.fireTableDataChanged();
		privilegesTreeTable.tableChanged(new TableModelEvent(atm, TableModelEvent.ALL_COLUMNS));

		privilegesTreeTable.getTree().invalidate();
	}

	public boolean isConfigLockedObjects(){
		return configLockedCheckbox.isSelected();
	}

	public void setConfigLockedObject(boolean value){
		this.configLockedCheckbox.setSelected(value);
	}

	public ChangeResetable[] getChangeResettableComponents(){
		return resettableComponents;
	}

	public void stopCellEditing(){
		if (privilegesTreeTable.isEditing())
			privilegesTreeTable.getCellEditor().stopCellEditing();
	}

}