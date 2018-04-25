/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.useractivity.UserActivityView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserActivityModel;

public class UserActivityTreeSelectionListener extends SchemaTreeSelectionListener{

	private UserActivityController controller;

	public UserActivityTreeSelectionListener(UserActivityController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		UserActivityView view = controller.getView();
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		if (currentPath == null)
			return;
		controller.clearAll();
		Object current = currentPath.getLastPathComponent();
		UserActivityModel model = (UserActivityModel) controller.getModel();
		DatabaseInstance currentDb = null;
		if (current instanceof DatabaseInstance){
			currentDb = (DatabaseInstance) current;
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		controller.reloadCurrentInstanceData();
		controller.refreshFilterCombo();
	}
}