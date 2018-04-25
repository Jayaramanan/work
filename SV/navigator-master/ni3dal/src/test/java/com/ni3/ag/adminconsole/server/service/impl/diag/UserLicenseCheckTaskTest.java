/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserEdition;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserLicenseCheckTaskTest extends TestCase{
	private List<User> users;
	private UserLicenseCheckTask task;

	@Override
	protected void setUp() throws Exception{
		users = new ArrayList<User>();
		for (int i = 0; i < 3; i++){
			User user = new User();
			user.setUserName("user" + (i + 1));
			user.setUserEditions(new ArrayList<UserEdition>());
			user.setActive(true);
			users.add(user);
		}
		task = new UserLicenseCheckTask();
	}

	public void testGetTaskResultNoAccessToAll(){
		DiagnoseTaskResult result = task.getTaskResult(users);
		assertEquals(DiagnoseTaskStatus.Warning, result.getStatus());
		assertEquals("No licenses (base module) assigned to user(s): user1, user2, user3", result.getErrorDescription());
	}

	public void testGetTaskResultNoAccessNoActive(){
		for (int i = 0; i < 3; i++){
			users.get(i).setActive(false);
		}
		DiagnoseTaskResult result = task.getTaskResult(users);
		assertEquals(DiagnoseTaskStatus.Ok, result.getStatus());
		assertNull(result.getErrorDescription());
	}

	public void testGetTaskResultNoAccessToOne(){
		for (int i = 0; i < 2; i++){
			users.get(i).getUserEditions().add(new UserEdition(users.get(i), NavigatorModule.BaseModule.getValue(), "abc"));
		}
		users.get(2).getUserEditions()
		        .add(new UserEdition(users.get(2), NavigatorModule.DataCaptureModule.getValue(), "abc"));

		DiagnoseTaskResult result = task.getTaskResult(users);
		assertEquals(DiagnoseTaskStatus.Warning, result.getStatus());
		assertEquals("No licenses (base module) assigned to user(s): user3", result.getErrorDescription());
	}

	public void testGetTaskResultAllAccessible(){
		for (int i = 0; i < 3; i++){
			users.get(i).getUserEditions().add(new UserEdition(users.get(i), NavigatorModule.BaseModule.getValue(), "abc"));
		}
		DiagnoseTaskResult result = task.getTaskResult(users);
		assertEquals(DiagnoseTaskStatus.Ok, result.getStatus());
		assertNull(result.getErrorDescription());
	}
}
