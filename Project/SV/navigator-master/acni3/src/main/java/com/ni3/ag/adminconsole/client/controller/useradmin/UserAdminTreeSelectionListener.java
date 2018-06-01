/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;

public class UserAdminTreeSelectionListener extends SchemaTreeSelectionListener{
	private UserAdminController controller;

	Logger log = Logger.getLogger(UserAdminTreeSelectionListener.class);

	public UserAdminTreeSelectionListener(UserAdminController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		UserAdminModel model = controller.getModel();
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		Group currentGroup = null;
		DatabaseInstance currentDb = null;
		String currentPanel = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) currentPath.getLastPathComponent();
				currentPanel = UserAdminView.ALL_USERS;
			} else if (current instanceof Group){
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
				currentGroup = (Group) current;
			} else if (current instanceof String){
				TreePath pPath = currentPath.getParentPath();
				currentGroup = (Group) pPath.getLastPathComponent();
				currentDb = (DatabaseInstance) pPath.getParentPath().getLastPathComponent();
				currentPanel = (String) current;
			}
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}
		if (model.getCurrentGroup() != currentGroup){
			controller.getModel().getDeletedUsers().clear();
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);

		model.setCurrentGroup(currentGroup);

		if (currentDb != null && currentGroup == null && !controller.checkInstanceLoaded()){
			return;
		}
		controller.refreshPanelData(currentPanel);

	}
}
