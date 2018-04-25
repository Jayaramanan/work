/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.common.ACTextField;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.ImageLoader;
import com.ni3.ag.adminconsole.domain.Group;

public class UserSelectionEditor extends AbstractCellEditor implements TableCellEditor{
	private static final Logger log = Logger.getLogger(UserSelectionEditor.class);
	private static final long serialVersionUID = 1L;

	private JButton dlgButton;
	private ACTextField userField;
	private JPanel panel;
	private List<Group> groups;

	public UserSelectionEditor(){
		userField = new ACTextField();
		userField.setBorder(BorderFactory.createEmptyBorder());
		dlgButton = new JButton(ImageLoader.loadIcon("/images/Group16.png"));
		dlgButton.setMaximumSize(new Dimension(20, 20));
		dlgButton.setPreferredSize(new Dimension(20, 20));
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(userField, BorderLayout.CENTER);
		panel.add(dlgButton, BorderLayout.EAST);
		dlgButton.addActionListener(new UserSelectionDialogButtonListener());
	}

	public void setGroups(List<Group> groups){
		this.groups = groups;
	}

	@Override
	public Object getCellEditorValue(){
		return userField.getText();
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
		userField.setText((String) value);
		return panel;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent){
		if (anEvent instanceof MouseEvent){
			return ((MouseEvent) anEvent).getClickCount() >= 2;
		}
		return true;
	}

	private class UserSelectionDialogButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			String userIds = userField.getText();
			log.debug("Old user string: " + userIds);
			UserSelectionDialog userDialog = new UserSelectionDialog(groups, userIds);
			userDialog.setVisible(true);
			if (userDialog.isOkPressed()){
				String newUserIds = userDialog.getSelectedUsers();
				log.debug("New user string: " + newUserIds);
				userField.setText(newUserIds);
			}
			fireEditingStopped();
		}
	}
}
