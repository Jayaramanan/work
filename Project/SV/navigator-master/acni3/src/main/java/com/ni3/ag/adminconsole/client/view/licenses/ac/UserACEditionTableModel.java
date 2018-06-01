/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.ac;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.ACModuleDescription;
import com.ni3.ag.adminconsole.license.AdminConsoleModule;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.CustomChecksumEncoder;

public class UserACEditionTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;
	private static ChecksumEncoder encoder = new CustomChecksumEncoder();
	private List<User> users;
	private List<ACModuleDescription> mDescriptions;

	public UserACEditionTableModel(List<User> users, List<ACModuleDescription> mDescriptions){
		setData(users, mDescriptions, true);
		updateUsedLicenseCount();
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
				return user.getUserName();
			default:
				return hasAccess(user, columnIndex);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex == 0){
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		super.setValueAt(aValue, rowIndex, columnIndex);
		User user = users.get(rowIndex);
		switch (columnIndex){
			case 0:
				break;
			default:
				setAccess(user, columnIndex, (Boolean) aValue);
				updateUsedLicenseCount();
				fireTableRowsUpdated(rowIndex, rowIndex);
		}
	}

	public int indexOf(User user){
		return this.users.indexOf(user);
	}

	public User getSelected(int rowIndex){
		if (rowIndex >= 0 && rowIndex < users.size()){
			return users.get(rowIndex);
		}
		return null;
	}

	boolean hasAccess(User user, int columnIndex){
		List<UserEdition> uEditions = user.getUserEditions();
		AdminConsoleModule module = mDescriptions.get(columnIndex - 1).getModule();
		for (UserEdition ue : uEditions){
			if (ue.getEdition().equals(module.getValue().toString())){
				return true;
			}
		}
		return false;
	}

	void setAccess(User user, int columnIndex, boolean aValue){
		if (columnIndex <= 0){
			return;
		}
		List<UserEdition> uEditions = user.getUserEditions();
		AdminConsoleModule module = mDescriptions.get(columnIndex - 1).getModule();
		String edition = module.getValue();
		if (!aValue){
			for (UserEdition ue : uEditions){
				if (ue.getEdition().equals(edition)){
					uEditions.remove(ue);
					break;
				}
			}
		} else{
			boolean found = false;
			for (UserEdition ue : uEditions){
				if (ue.getEdition().equals(edition)){
					found = true;
					break;
				}
			}
			if (!found){
				String checksum = encoder.encode(user.getId(), edition);
				uEditions.add(new UserEdition(user, edition, checksum));
			}
		}
	}

	public void updateUsedLicenseCount(){
		for (int col = 1; col < getColumnCount(); col++){
			AdminConsoleModule module = mDescriptions.get(col - 1).getModule();
			int count = getUsedLicenseCount(module);
			mDescriptions.get(col - 1).setUsedUserCount(count);
		}
	}

	int getUsedLicenseCount(AdminConsoleModule module){
		int result = 0;
		for (User user : users){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(module.getValue())){
					result++;
					break;
				}
			}
		}
		return result;
	}

	public boolean isCellMarkedForExpiry(int row, int col){
		List<UserEdition> userEditions = users.get(row).getUserEditions();
		if (userEditions == null)
			return false;
		AdminConsoleModule module = mDescriptions.get(col - 1).getModule();
		for (UserEdition ue : userEditions){
			if (ue.getEdition().equals(module.getValue()))
				return ue.isExpiring();
		}
		return false;
	}

	public int getCurrentMarkedCellCount(String moduleName){
		int ret = 0;
		if (users == null)
			return ret;
		for (User user : users){
			List<UserEdition> userEditions = user.getUserEditions();
			for (UserEdition ue : userEditions){
				if (ue.getEdition().equals(moduleName)){
					if (ue.isExpiring())
						ret++;
					break;
				}
			}
		}
		return ret;
	}

	public void setCellMarkedForExpiry(int row, int col, boolean value){
		List<UserEdition> userEditions = users.get(row).getUserEditions();
		if (userEditions == null)
			return;
		AdminConsoleModule module = mDescriptions.get(col - 1).getModule();
		for (UserEdition ue : userEditions){
			if (ue.getEdition().equals(module.getValue())){
				ue.setIsExpiring(value);
				break;
			}
		}
	}

	public void setRowMarkedForExpiry(int row, boolean value){
		for (int i = 1; i < super.getColumnCount() + 1; i++)
			setCellMarkedForExpiry(row, i, value);
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column) || column == 0){
			return null;
		}

		return null;
	}

	public ACModuleDescription getModuleDescription(int column){
		if (column > 0){
			return mDescriptions.get(column - 1);
		}
		return null;
	}

	public void setData(List<User> users, List<ACModuleDescription> moduleDescriptions, boolean updateColumns){
		this.users = users;
		this.mDescriptions = moduleDescriptions;
		List<String> colNames = new ArrayList<String>();
		for (int i = 0; i < getColumnCount(); i++)
			colNames.add(getColumnName(i));
		removeColumns(colNames);

		addColumn(Translation.get(TextID.User), false, User.class, false);

		for (int i = 0; i < mDescriptions.size(); i++){
			String m = mDescriptions.get(i).getModule().toString();
			String columnName = Translation.get(TextID.valueOf(m));
			mDescriptions.get(i).setColumnName(columnName);

			addColumn(columnName, true, Boolean.class, false);
		}

	}
}
