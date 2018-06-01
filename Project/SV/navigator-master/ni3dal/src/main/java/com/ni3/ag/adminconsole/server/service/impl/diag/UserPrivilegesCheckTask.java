/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.List;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectGroupDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class UserPrivilegesCheckTask implements DiagnosticTask{

	private static final String MY_DESCRIPTION = "Checking user privileges";
	private static final String TOOLTIP_USER_NOT_IN_GROUP = "User is not assigned to any group: ";
	private static final String TOOLTIP_USER_DOSE_NOT_HAVE_OBJECT_PRIVILEGES = "User does not have persmissions to any object: ";
	private static final String TOOLTIP_USER_DOSE_NOT_HAVE_ATTRIBUTE_PRIVILEGES = "User does not have persmissions to any attribute: ";
	private static final String ACTION_ASSIGN_TO_GROUP = "Go to Users tab and set group for the user: ";
	private static final String ACTION_ADD_PERMISSIONS_TO_OBJECT = "Go to Users tab and set 'Can read' to at least one object for the group '%' ";
	private static final String ACTION_ADD_PERMISSIONS_TO_ATTRIBUTE = "Go to Users tab and set 'Can read' to at least one attribute for the group '%'";

	private UserDAO userDAO;
	private ObjectGroupDAO objectGroupDAO;
	private AttributeGroupDAO attributeGroupDAO;

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public ObjectGroupDAO getObjectGroupDAO(){
		return objectGroupDAO;
	}

	public void setObjectGroupDAO(ObjectGroupDAO objectGroupDAO){
		this.objectGroupDAO = objectGroupDAO;
	}

	public AttributeGroupDAO getAttributeGroupDAO(){
		return attributeGroupDAO;
	}

	public void setAttributeGroupDAO(AttributeGroupDAO attributeGroupDAO){
		this.attributeGroupDAO = attributeGroupDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<User> users = userDAO.getUsers();
		for (User u : users){
			DiagnoseTaskResult dtr = checkUserPrivileges(u);
			if (dtr != null)
				return dtr;
		}
		return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	private DiagnoseTaskResult checkUserPrivileges(User u){
		String user = u.getFirstName() + " " + u.getLastName();
		if (u.getGroups() == null || u.getGroups().isEmpty()){
			return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Warning,
			        TOOLTIP_USER_NOT_IN_GROUP + user, ACTION_ASSIGN_TO_GROUP.replace("%", user));
		}
		final Group group = u.getGroups().get(0);
		List<ObjectGroup> ougs = objectGroupDAO.getByGroup(group);
		List<AttributeGroup> ags = attributeGroupDAO.getAttributeGroupsByGroup(group);
		boolean isValid = false;
		for (ObjectGroup oug : ougs){
			if (oug.isCanCreate() || oug.isCanDelete() || oug.isCanRead() || oug.isCanUpdate()){
				isValid = true;
				break;
			}
		}
		if (!isValid){
			return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Error,
			        TOOLTIP_USER_DOSE_NOT_HAVE_OBJECT_PRIVILEGES + user, ACTION_ADD_PERMISSIONS_TO_OBJECT.replace("%", group
			                .getName()));
		}
		for (AttributeGroup ag : ags){
			if (ag.isCanRead())
				return null;
		}
		return new DiagnoseTaskResult(getClass().getName(), MY_DESCRIPTION, false, DiagnoseTaskStatus.Error,
		        TOOLTIP_USER_DOSE_NOT_HAVE_ATTRIBUTE_PRIVILEGES + user, ACTION_ADD_PERMISSIONS_TO_ATTRIBUTE.replace("%",
		                group.getName()));
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		throw new ACFixTaskException("ACFixTaskException", "Non fixable");
	}

	@Override
	public String getTaskDescription(){
		return MY_DESCRIPTION;
	}
}
