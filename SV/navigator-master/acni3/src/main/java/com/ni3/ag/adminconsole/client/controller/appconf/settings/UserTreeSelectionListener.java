/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class UserTreeSelectionListener extends SchemaTreeSelectionListener{
	private SettingsController controller;

	public UserTreeSelectionListener(SettingsController settingsController){
		controller = settingsController;
	}

	public void changeValue(TreeSelectionEvent e){
		SettingsModel model = controller.getModel();
		SettingsView view = controller.getView();
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		Object currentObject = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) currentPath.getLastPathComponent();
			} else if (current instanceof Group){
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			} else if (current instanceof User){
				TreePath ppPath = currentPath.getParentPath().getParentPath();
				currentDb = (DatabaseInstance) ppPath.getLastPathComponent();
			} else{
				current = null;
			}
			currentObject = current;
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);

		model.setCurrentObject(currentObject);

		if (currentDb != null && currentObject instanceof DatabaseInstance && !controller.checkInstanceLoaded()){
			return;
		}
		controller.updateData();
	}

}
