/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.util.List;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ModuleTableModel extends ACTableModel{

	public static final int NAME_COLUMN_INDEX = 0;
	public static final int PATH_COLUMN_INDEX = 1;
	public static final int VERSION_COLUMN_INDEX = 2;
	public static final int PARAMS_COLUMN_INDEX = 5;

	private static final long serialVersionUID = 1L;

	private List<Module> modules;
	private List<Group> groups;

	public ModuleTableModel(List<Module> modules, List<Group> groups){
		this.modules = modules;
		this.groups = groups;
		addColumn(Translation.get(TextID.ModuleName), true, String.class, true);
		addColumn(Translation.get(TextID.FileName), true, String.class, true);
		addColumn(Translation.get(TextID.Version), true, String.class, true);
		addColumn(Translation.get(TextID.Hash), true, String.class, false);
		addColumn(Translation.get(TextID.ArchivePassword), true, String.class, false);
		addColumn(Translation.get(TextID.Parameters), true, String.class, false);
	}

	@Override
	public int getRowCount(){
		return modules.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		Module m = modules.get(rowIndex);
		switch (columnIndex){
			case 0:
				return m.getName();
			case 1:
				return m.getPath();
			case 2:
				return m.getVersion();
			case 3:
				return m.getHash();
			case 4:
				return m.getArchivePassword();
			case 5:
				return m.getParams();
		}
		return null;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		Module m = modules.get(rowIndex);
		switch (columnIndex){
			case 0:
				m.setName((String) aValue);
				break;
			case 1:
				m.setPath((String) aValue);
				break;
			case 2:
				m.setVersion((String) aValue);
				break;
			case 3:
				m.setHash((String) aValue);
				break;
			case 4:
				m.setArchivePassword((String) aValue);
				break;
			case 5:
				m.setParams((String) aValue);
		}
	}

	public Module getModule(int index){
		if (index < 0 || index > getRowCount())
			return null;
		return modules.get(index);
	}

	public int indexOf(Module m){
		if (modules == null)
			return -1;
		return modules.indexOf(m);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		if (columnIndex == PARAMS_COLUMN_INDEX)
			return true;
		Module m = modules.get(rowIndex);
		return !isModuleUsed(m);
	}

	private boolean isModuleUsed(Module m){
		for (Group group : groups){
			for (User user : group.getUsers()){
				for (ModuleUser module : user.getUserModules()){
					if (m.equals(module.getCurrent()) || m.equals(module.getTarget())){
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String getToolTip(int row, int column){
		if (isCellEditable(row, column)){
			return null;
		}

		return Translation.get(TextID.ReadonlyModuleAssignedToUser);
	}

	public void setModules(List<Module> modules){
		this.modules = modules;
	}
}
