/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses.navigator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.ChecksumEncoder;
import com.ni3.ag.adminconsole.shared.service.impl.CustomChecksumEncoder;

public class UserEditionTableModel extends ACTableModel{

	private static final long serialVersionUID = 1L;
	private static ChecksumEncoder encoder = new CustomChecksumEncoder();
	private Map<Integer, ModuleDescription> moduleMap = new HashMap<Integer, ModuleDescription>();
	private List<User> users;
	private List<User> allUsers;

	private int baseColumnIndex = -1, dataCaptureColumnIndex = -1, chartsColumnIndex = -1, mapsColumnIndex = -1,
	        geoAnalyticsColumnIndex = -1, remoteClientColumnIndex = -1, reportsColumnIndex = -1;

	public UserEditionTableModel(List<User> users, List<User> allUsers, List<ModuleDescription> mDescrs){
		setData(users, allUsers, mDescrs, true);
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
		} else if (columnIndex == geoAnalyticsColumnIndex){
			return baseColumnIndex > 0 && (Boolean) getValueAt(rowIndex, baseColumnIndex) && mapsColumnIndex > 0
			        && (Boolean) getValueAt(rowIndex, mapsColumnIndex);
		} else if (columnIndex == dataCaptureColumnIndex || columnIndex == chartsColumnIndex
		        || columnIndex == mapsColumnIndex || columnIndex == remoteClientColumnIndex
		        || columnIndex == reportsColumnIndex){
			return baseColumnIndex > 0 && (Boolean) getValueAt(rowIndex, baseColumnIndex);
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
		NavigatorModule module = moduleMap.get(columnIndex) != null ? moduleMap.get(columnIndex).getModule() : null;
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
		ModuleDescription mDescr = moduleMap.get(columnIndex);
		NavigatorModule module = mDescr != null ? mDescr.getModule() : null;
		String edition = module.getValue().toString();
		if (!aValue){
			for (UserEdition ue : uEditions){
				if (ue.getEdition().equals(edition)){
					uEditions.remove(ue);
					break;
				}
			}
			if (columnIndex == mapsColumnIndex){
				setAccess(user, geoAnalyticsColumnIndex, false);
			} else if (columnIndex == baseColumnIndex){
				setAccess(user, dataCaptureColumnIndex, false);
				setAccess(user, chartsColumnIndex, false);
				setAccess(user, mapsColumnIndex, false);
				setAccess(user, remoteClientColumnIndex, false);
				setAccess(user, reportsColumnIndex, false);
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

	private void fillColumnIndexes(List<ModuleDescription> mDescrs){
		for (int i = 0; i < mDescrs.size(); i++){
			int colIndex = i + 1;
			ModuleDescription moduleDescription = mDescrs.get(i);
			NavigatorModule module = moduleDescription.getModule();
			moduleMap.put(colIndex, moduleDescription);
			switch (module){
				case BaseModule:
					baseColumnIndex = colIndex;
					break;
				case DataCaptureModule:
					dataCaptureColumnIndex = colIndex;
					break;
				case ChartsModule:
					chartsColumnIndex = colIndex;
					break;
				case MapsModule:
					mapsColumnIndex = colIndex;
					break;
				case GeoAnalyticsModule:
					geoAnalyticsColumnIndex = colIndex;
					break;
				case RemoteClientModule:
					remoteClientColumnIndex = colIndex;
					break;
				case ReportsModule:
					reportsColumnIndex = colIndex;
					break;
				default:
					break;
			}
		}
	}

	public void updateUsedLicenseCount(){
		for (int col = 1; col < getColumnCount(); col++){
			ModuleDescription md = moduleMap.get(col);
			int count = getUsedLicenseCount(md.getModule());
			md.setUsedUserCount(count);
		}
	}

	private int getUsedLicenseCount(NavigatorModule module){
		int result = 0;
		for (User user : allUsers){
			for (UserEdition ue : user.getUserEditions()){
				if (ue.getEdition().equals(module.getValue().toString())){
					result++;
					break;
				}
			}
		}
		return result;
	}

	public ModuleDescription getModuleDescription(int columnIndex){
		return moduleMap.get(columnIndex);
	}

	public boolean isCellMarkedForExpiry(int row, int col){
		String colName = getColumnName(col);
		List<UserEdition> userEditions = users.get(row).getUserEditions();
		if (userEditions == null)
			return false;
		String moduleName = "";
		for (NavigatorModule module : NavigatorModule.values()){
			TextID id = TextID.valueOf(module.toString());
			String moduleTranslation = Translation.get(id);
			if (moduleTranslation.equals(colName)){
				moduleName = module.getValue();
				break;
			}
		}
		for (UserEdition ue : userEditions){
			if (ue.getEdition().equals(moduleName))
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

	public void markCellForExpiry(int row, int col){
		String colName = getColumnName(col);
		List<UserEdition> userEditions = users.get(row).getUserEditions();
		if (userEditions == null)
			return;
		String moduleName = "";
		for (NavigatorModule module : NavigatorModule.values()){
			TextID id = TextID.valueOf(module.toString());
			String moduleTranslation = Translation.get(id);
			if (moduleTranslation.equals(colName)){
				moduleName = module.getValue();
				break;
			}
		}
		for (UserEdition ue : userEditions){
			if (ue.getEdition().equals(moduleName))
				ue.setIsExpiring(!ue.isExpiring());
		}
	}

	public void markRowForExpiry(int row){
		for (int i = 1; i < super.getColumnCount() + 1; i++)
			markCellForExpiry(row, i);
	}

	public String getColumnModule(int column){
		String moduleName = "";
		String colName = getColumnName(column);
		for (NavigatorModule module : NavigatorModule.values()){
			TextID id = TextID.valueOf(module.toString());
			String moduleTranslation = Translation.get(id);
			if (moduleTranslation.equals(colName)){
				moduleName = module.getValue();
				break;
			}
		}
		return moduleName;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column) || column == 0){
			return null;
		}

		if (baseColumnIndex <= 0 || !(Boolean) getValueAt(row, baseColumnIndex))
			return Translation.get(TextID.ReadonlyBaseModuleDisabled);
		else if (column == geoAnalyticsColumnIndex){
			return Translation.get(TextID.ReadonlyMapsModuleDisabled);
		}
		return null;
	}

	public void setData(List<User> users, List<User> allUsers, List<ModuleDescription> mDescrs, boolean updateColumns){
		this.users = users;
		this.allUsers = allUsers;
		if (updateColumns){
			List<String> colNames = new ArrayList<String>();
			for (int i = 0; i < getColumnCount(); i++)
				colNames.add(getColumnName(i));
			removeColumns(colNames);
			fillColumnIndexes(mDescrs);
			addColumn(Translation.get(TextID.User), false, User.class, false);
			for (int i = 0; i < mDescrs.size(); i++){
				addColumn(mDescrs.get(i).getColumnName(), true, Boolean.class, false);
			}
		}
	}
}
