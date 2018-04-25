/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ThickClientTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;
	private List<User> users = new ArrayList<User>();

	public ThickClientTableModel(){
		addColumn(Translation.get(TextID.FirstName), false, String.class, false);
		addColumn(Translation.get(TextID.LastName), false, String.class, false);
		addColumn(Translation.get(TextID.UserName), false, String.class, false);
	};

	public ThickClientTableModel(List<User> users){
		this();
		this.users = users;
	}

	public void setData(List<User> users){
		super.resetChanges();
		this.users = users;
	}

	public int getRowCount(){
		if (users == null)
			return 0;
		return users.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		User user = users.get(rowIndex);
		switch (columnIndex){
			case 0:
				return user.getFirstName();
			case 1:
				return user.getLastName();
			case 2:
				return user.getUserName();
			default:
				return null;
		}
	}

	public int indexOf(User user){
		return users.indexOf(user);
	}

	public User getSelectedUser(int rowIndex){
		if (rowIndex >= 0 && rowIndex < users.size()){
			return users.get(rowIndex);
		}
		return null;
	}

}
