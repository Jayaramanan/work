/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACButton;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.BooleanCellRenderer;
import com.ni3.ag.adminconsole.client.view.common.Mnemonic;
import com.ni3.ag.adminconsole.client.view.common.treetable.ACTreeTable;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserSelectionDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 3864844263486827445L;
	private ACButton okButton;
	private ACButton cancelButton;

	private boolean okPressed = false;
	private UserSelectionTableModel tableModel;
	private String userNames;
	private List<Group> groups;

	public UserSelectionDialog(List<Group> groups, String userNames){
		this.groups = groups;
		this.userNames = userNames;
		setTitle(Translation.get(TextID.Users));
		initComponents();
		setLocation((int) (ACMain.getScreenWidth() / 2) - getWidth() / 2, (int) (ACMain.getScreenHeight() / 2) - getHeight() / 2);
		setIconImage(new ImageIcon(getClass().getResource("/images/Ni3.png")).getImage());
	}

	private void initComponents(){
		setModal(true);
		setSize(new Dimension(400, 400));

		JPanel mainPanel = new JPanel();
		SpringLayout layout = new SpringLayout();
		mainPanel.setLayout(layout);
		getContentPane().add(mainPanel);

		ACTreeTable treeTable = new ACTreeTable();
		tableModel = new UserSelectionTableModel(treeTable.getTree(), userNamesToSet(userNames));
		UserSelectionTreeTableModel treeModel = new UserSelectionTreeTableModel(groups);
		treeTable.setModel(treeModel, tableModel);
		treeTable.getTree().setCellRenderer(new ACTreeCellRenderer());

		JScrollPane sp = new JScrollPane();
		sp.setViewportView(treeTable);

		mainPanel.add(sp);
		layout.putConstraint(SpringLayout.NORTH, sp, 0, SpringLayout.NORTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, sp, 0, SpringLayout.EAST, mainPanel);
		layout.putConstraint(SpringLayout.WEST, sp, 0, SpringLayout.WEST, mainPanel);
		layout.putConstraint(SpringLayout.SOUTH, sp, -40, SpringLayout.SOUTH, mainPanel);

		okButton = new ACButton(Mnemonic.AltO, TextID.Ok);
		okButton.setSize(70, 23);
		okButton.setPreferredSize(new Dimension(70, 23));
		cancelButton = new ACButton(Mnemonic.AltC, TextID.Cancel);
		cancelButton.setSize(70, 23);
		cancelButton.setPreferredSize(new Dimension(70, 23));

		layout.putConstraint(SpringLayout.NORTH, cancelButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -10, SpringLayout.EAST, mainPanel);
		mainPanel.add(cancelButton);
		cancelButton.addActionListener(this);

		layout.putConstraint(SpringLayout.NORTH, okButton, -33, SpringLayout.SOUTH, mainPanel);
		layout.putConstraint(SpringLayout.EAST, okButton, -10, SpringLayout.WEST, cancelButton);
		mainPanel.add(okButton);
		okButton.addActionListener(this);

		TableColumn column = treeTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(30);
		column.setMinWidth(30);
		column.setMaxWidth(30);

		TableCellRenderer boolCellRenderer = treeTable.getDefaultRenderer(Boolean.class);
		treeTable.setDefaultRenderer(Boolean.class, new BooleanCellRenderer(boolCellRenderer));
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (e.getSource() == okButton){
			okPressed = true;
		} else{
			okPressed = false;
		}
		setVisible(false);
	}

	public boolean isOkPressed(){
		return okPressed;
	}

	public String getSelectedUsers(){
		if (!okPressed){
			return null;
		}
		Set<User> selectedUsers = tableModel.getSelectedUsers();
		String names = userSetToString(selectedUsers);
		return names;
	}

	private Set<User> userNamesToSet(String userIds){
		if (userIds == null || userIds.isEmpty())
			return new HashSet<User>();
		Set<User> users = new HashSet<User>();
		String[] names = userIds.split(",");
		for (String name : names){
			for (Group group : groups){
				for (User user : group.getUsers()){
					if (user.getUserName().equals(name)){
						users.add(user);
						break;
					}
				}
			}
		}
		return users;
	}

	private String userSetToString(Set<User> users){
		if (users == null || users.isEmpty())
			return null;
		String result = "";
		boolean first = true;
		for (User user : users){
			if (first)
				first = false;
			else
				result += ",";

			result += user.getUserName();
		}

		return result;
	}

}
