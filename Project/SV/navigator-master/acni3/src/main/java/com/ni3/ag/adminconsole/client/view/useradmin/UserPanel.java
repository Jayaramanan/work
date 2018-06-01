/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACCellEditor;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.client.view.common.ACTable;
import com.ni3.ag.adminconsole.client.view.common.ACToolBar;
import com.ni3.ag.adminconsole.client.view.common.BooleanCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.ChangeResetable;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;

public class UserPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	public static final String ALL_USER_MODE = "AllUsers";
	public static final String GROUP_USER_MODE = "GroupUsers";

	private String currentMode = GROUP_USER_MODE;
	private JScrollPane scrollPane;
	private ACTable tableUser;

	private ChangeResetable[] resetableComponents;

	private ACButton addButton;
	private ACButton updateButton;
	private ACButton deleteButton;
	private ACButton cancelButton;
	private ACButton copyButton;
	private ACButton resetPasswordButton;
	private ACComboBox groupCombo;
	private ACToolBar toolBar;

	public UserPanel(){
		initializeComponents();
	}

	public JTable getTableUser(){
		return tableUser;
	}

	public void initializeComponents(){
		SpringLayout elementLayout = new SpringLayout();
		setLayout(elementLayout);

		toolBar = new ACToolBar();
		copyButton = toolBar.makeCopyButton();
		addButton = toolBar.makeAddButton();
		deleteButton = toolBar.makeDeleteButton();
		updateButton = toolBar.makeUpdateButton();
		cancelButton = toolBar.makeRefreshButton();
		resetPasswordButton = toolBar.makeResetPasswordButton();
		add(toolBar);
		elementLayout.putConstraint(SpringLayout.WEST, toolBar, 10, SpringLayout.WEST, this);

		scrollPane = new JScrollPane();
		tableUser = new ACTable();
		tableUser.enableCopyPaste();
		scrollPane.setViewportView(tableUser);
		add(scrollPane);

		elementLayout.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, this);
		elementLayout.putConstraint(SpringLayout.NORTH, scrollPane, 0, SpringLayout.SOUTH, toolBar);
		elementLayout.putConstraint(SpringLayout.SOUTH, scrollPane, -10, SpringLayout.SOUTH, this);
		elementLayout.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, this);

		groupCombo = new ACComboBox();
		groupCombo.setRenderer(new GroupListCellRenderer());
		tableUser.setDefaultEditor(Group.class, new ACCellEditor(groupCombo));
		tableUser.setDefaultRenderer(Group.class, new GroupTableCellRenderer());
		TableCellRenderer boolCellRenderer = tableUser.getDefaultRenderer(Boolean.class);
		tableUser.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));

		resetableComponents = new ChangeResetable[] { tableUser };
		deleteButton.setVisible(false);
	}

	public void setTableModel(final UserTableModel model){
		tableUser.setModel(model);
		tableUser.setRowSorter(new TableRowSorter<UserTableModel>(model));
	}

	public void setTableModelData(List<User> users){
		UserTableModel tableModel = getTableModel();
		tableModel.setData(users);
	}

	public UserTableModel getTableModel(){
		return (UserTableModel) tableUser.getModel();
	}

	public void setGroupReferenceData(List<Group> groupList){
		groupCombo.removeAllItems();
		if (groupList == null)
			return;
		for (Group group : groupList){
			groupCombo.addItem(group);
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

	public void addRefreshButtonActionListener(ActionListener actionListener){
		cancelButton.addActionListener(actionListener);
	}

	public void addCopyButtonActionListener(ActionListener actionListener){
		copyButton.addActionListener(actionListener);
	}

	public void refreshTable(){
		getTableModel().fireTableDataChanged();
	}

	public void stopCellEditing(){
		if (tableUser.isEditing())
			tableUser.getCellEditor().stopCellEditing();
	}

	public int getSelectedRow(){
		if (tableUser.getSelectedRow() >= 0){
			return tableUser.convertRowIndexToModel(tableUser.getSelectedRow());
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

	public void setActiveTableRow(User user){
		if (user == null){
			return;
		}
		UserTableModel model = getTableModel();
		int modelIndex = model.indexOf(user);
		if (modelIndex >= 0){
			tableUser.setSelectedCellIndexes(new int[] { modelIndex, modelIndex, 0, 0 });

			int index = tableUser.convertRowIndexToView(modelIndex);
			if (index >= 0){
				Rectangle r = tableUser.getCellRect(index, 0, true);
				tableUser.scrollRectToVisible(r);
			}
		}

		tableUser.requestFocusInWindow();
	}

	public void setCurrentMode(String mode){
		currentMode = mode;
	}

	public boolean isAllUserMode(){
		return currentMode.equals(ALL_USER_MODE);
	}

	public ChangeResetable[] getChangeResetableComponents(){
		return resetableComponents;
	}

	public void addResetPasswordButtonListener(ActionListener l){
		resetPasswordButton.addActionListener(l);
	}

}
