/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.thickclient.vers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserModuleTableModel extends ACTableModel{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(UserModuleTableModel.class);
	private List<User> users;
	private List<Module> allModules;
	private String[] sortedNames;
	private static final Module nullModule;
	static{
		nullModule = new Module();
		nullModule.setId(-1);
		nullModule.setName("");
		nullModule.setVersion("");
	}

	public UserModuleTableModel(){
		addColumn(Translation.get(TextID.User), false, User.class, true);
		sortedNames = new String[Module.NAMES.length];
		System.arraycopy(Module.NAMES, 0, sortedNames, 0, Module.NAMES.length);
		Arrays.sort(sortedNames);
		for (String s : sortedNames){
			addColumn(s, true, ModuleUser.class, false);
		}
		allModules = new ArrayList<Module>();
		users = new ArrayList<User>();
	}

	public UserModuleTableModel(List<User> users, List<Module> allModules){
		this();
		this.users = users;
		this.allModules = allModules;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		return super.isCellEditable(rowIndex, columnIndex);
	}

	@Override
	public int getRowCount(){
		return users.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex){
		User u = users.get(rowIndex);
		switch (columnIndex){
			case 0:
				return u;
			default: {
				int index = columnIndex - 1;
				String name = sortedNames[index];
				return getUserModule(name, u);
			}
		}
	}

	private Object getUserModule(String name, User u){
		for (ModuleUser mu : u.getUserModules()){
			if (mu.getCurrent() != null && mu.getCurrent().getName().equals(name))
				return mu;
			if (mu.getTarget() != null && mu.getTarget().getName().equals(name))
				return mu;
		}

		return null;
	}

	public List<Module> getModulesForColumn(int column){
		String name = sortedNames[column - 1];
		List<Module> modules = new ArrayList<Module>();
		modules.add(nullModule);
		for (Module m : allModules){
			if (m.getName() == null)
				continue;
			if (m.getName().equals(name))
				modules.add(m);
		}
		return modules;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		log.debug("Set value:" + aValue + ", row: " + rowIndex + ", column: " + columnIndex);
		if (aValue instanceof ModuleUser){
			ModuleUser mu = (ModuleUser) aValue;
			if (!isAvailableForColumn(mu.getTarget(), columnIndex))
				return;
			aValue = mu.getTarget();
		}
		User u = users.get(rowIndex);
		Module newModule = (Module) aValue;
		if (newModule != null && !nullModule.equals(newModule)){
			ModuleUser mu = (ModuleUser) getUserModule(newModule.getName(), u);
			if (mu == null){
				mu = new ModuleUser();
				mu.setUser(u);
				if (u.getUserModules() == null)
					u.setUserModules(new ArrayList<ModuleUser>());
				u.getUserModules().add(mu);
			}
			mu.setTarget((Module) aValue);
		} else
			removeModuleUser(u, columnIndex);

		super.setValueAt(aValue, rowIndex, columnIndex);
	}

	void removeModuleUser(User user, int columnIndex){
		List<Module> moduleList = getModulesForColumn(columnIndex);
		List<ModuleUser> toRemove = new ArrayList<ModuleUser>();
		for (Module module : moduleList){
			for (ModuleUser mu : user.getUserModules()){
				if (module.equals(mu.getTarget()) || module.equals(mu.getCurrent())){
					toRemove.add(mu);
				}
			}
		}
		user.getUserModules().removeAll(toRemove);
	}

	boolean isAvailableForColumn(Object aValue, int columnIndex){
		List<Module> moduleList = getModulesForColumn(columnIndex);
		for (Module module : moduleList){
			if (module.equals(aValue)){
				return true;
			}
		}
		return false;
	}

	public User getUser(int index){
		if (index < 0 || index >= getRowCount())
			return null;
		return users.get(index);
	}

	public int indexOf(User u){
		return users.indexOf(u);
	}

	public void setData(List<User> users, List<Module> modules){
		this.users = users;
		this.allModules = modules;
	}

}
