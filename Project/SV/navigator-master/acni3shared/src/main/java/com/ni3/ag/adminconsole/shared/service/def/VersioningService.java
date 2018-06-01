/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.def;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.validation.ACException;

public interface VersioningService{
	List<Group> getGroups();

	List<Module> getModules();

	void updateModules(List<Module> toUpdate, List<Module> toDelete);

	void updateUserModules(List<User> users);

	List<String> getFileNames();

	String uploadZipModule(byte[] bytes, String name) throws ACException;

	void sendStarterModuleToUser(User targetUser, boolean useSSO) throws ACException;

	boolean testModulesPath();

}
