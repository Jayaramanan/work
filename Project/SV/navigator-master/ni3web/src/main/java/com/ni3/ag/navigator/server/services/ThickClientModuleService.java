/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.navigator.shared.domain.User;

public interface ThickClientModuleService{

	Module getModule(String module, User user, String modulesPath);

	void processGetCurrentVersions(HttpServletResponse response, User user) throws IOException;

	void processCommitModule(User user, String module, String version, HttpServletResponse response) throws IOException;

}
