/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Module;

public interface ModuleDAO{
	public Module getModule(Integer id);

	public List<Module> getModules();

	public void saveOrUpdate(Module m);

	public void saveOrUpdateAll(List<Module> modules);

	public void deleteAll(List<Module> toDelete);

	public List<String> getModuleNames();
}
