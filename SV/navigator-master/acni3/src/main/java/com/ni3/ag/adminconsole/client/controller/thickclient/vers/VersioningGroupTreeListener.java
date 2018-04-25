/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class VersioningGroupTreeListener implements TreeSelectionListener{

	private VersioningController controller;

	public VersioningGroupTreeListener(VersioningController controller){
		this.controller = controller;
	}

	@Override
	public void valueChanged(TreeSelectionEvent e){
		controller.getView().stopEditing();
		controller.getView().clearErrors();
		DatabaseInstance currentDb = null;
		Group currentGroup = null;
		TreePath currentPath = e.getNewLeadSelectionPath();
		if (currentPath == null)
			return;
		Object o = currentPath.getLastPathComponent();
		if (o instanceof DatabaseInstance){
			currentDb = (DatabaseInstance) o;
		} else if (o instanceof Group){
			currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			currentGroup = (Group) o;
		}
		ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		controller.getModel().setCurrentGroup(currentGroup);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		controller.getModel().setCurrentDatabaseInstance(currentDb);

		if (currentDb != null && currentGroup == null && !controller.checkInstanceLoaded()){
			return;
		}

		controller.updateModuleTable();
		controller.updateUserModuleTable();
	}

}
