/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.User;

public interface ModuleUserDAO{
	public ModuleUser getModuleUser(Integer id);

	public List<ModuleUser> getModuleUsers();

	public List<ModuleUser> getByUser(User u);

	public void saveOrUpdate(ModuleUser mu);

	public void saveOrUpdateAll(List<ModuleUser> mus);
}
