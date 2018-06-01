/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserSelectionTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;

	private JTree tree;
	private Set<User> selectedUsers;

	public UserSelectionTableModel(JTree tree, Set<User> selectedUsers){
		this.tree = tree;
		this.selectedUsers = selectedUsers;
		addColumn(get(TextID.Selected), false, Boolean.class, false);
		addColumn(get(TextID.Users), false, TreeModel.class, false);

		tree.addTreeExpansionListener(new TreeExpansionListener(){
			public void treeExpanded(TreeExpansionEvent event){
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event){
				fireTableDataChanged();
			}
		});
	}

	@Override
	public int getRowCount(){
		return tree.getRowCount();
	}

	protected Object nodeForRow(int row){
		TreePath treePath = tree.getPathForRow(row);
		return treePath.getLastPathComponent();
	}

	@Override
	public Object getValueAt(int row, int column){
		Object node = nodeForRow(row);
		switch (column){
			case 0:
				return isSelected(node);
			case 1:
				return node;
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int row, int column){
		return isCellEditable(nodeForRow(row), column);
	}

	boolean isCellEditable(Object node, int column){
		if (column == 1){
			return true;
		} else if (column == 0){
			if (isSelected(node))
				return true;
			if (node instanceof Group)
				return !selectedDifferentGroup((Group) node);
			if (node instanceof User){
				User user = (User) node;
				Group group = user.getGroups().get(0);
				return !selectedDifferentGroup(group);
			}
		}
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int row, int column){
		Object node = nodeForRow(row);
		switch (column){
			case 0:
				setSelected(node, (Boolean) aValue);
				fireTableRowsUpdated(0, getRowCount());
				break;
			default:
				break;
		}
	}

	boolean isSelected(Object node){
		if (node instanceof Group){
			Group group = (Group) node;
			List<User> users = group.getUsers();
			return users != null && !users.isEmpty() && selectedUsers.containsAll(users);
		} else if (node instanceof User){
			User user = (User) node;
			return selectedUsers.contains(user);
		}
		return false;
	}

	void setSelected(Object node, boolean selected){
		if (node instanceof Group){
			Group group = (Group) node;
			List<User> users = group.getUsers();
			if (selected)
				selectedUsers.addAll(users);
			else
				selectedUsers.removeAll(users);
		} else if (node instanceof User){
			User user = (User) node;
			if (selected)
				selectedUsers.add(user);
			else
				selectedUsers.remove(user);
		}
	}

	boolean selectedDifferentGroup(Group group){
		for (User user : selectedUsers){
			if (!group.equals(user.getGroups().get(0)))
				return true;
		}
		return false;
	}

	void setSelectedUsers(Set<User> selectedUsers){
		this.selectedUsers = selectedUsers;
	}

	Set<User> getSelectedUsers(){
		return selectedUsers;
	}

}
