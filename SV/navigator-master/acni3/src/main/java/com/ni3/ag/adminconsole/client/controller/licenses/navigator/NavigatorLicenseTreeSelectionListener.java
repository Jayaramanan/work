/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.navigator;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.NavigatorLicenseView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.NavigatorLicenseModel;

public class NavigatorLicenseTreeSelectionListener extends SchemaTreeSelectionListener{

	private NavigatorLicenseView view;
	private NavigatorLicenseModel model;
	private NavigatorLicenseController controller;

	public NavigatorLicenseTreeSelectionListener(NavigatorLicenseController controller){
		this.controller = controller;
		this.view = (NavigatorLicenseView) controller.getView();
		this.model = (NavigatorLicenseModel) controller.getModel();
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();

		Object currentObject = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentObject = current;
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof Group){
				currentObject = current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			}
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObject(currentObject);

		if (currentDb != null && model.isInstanceSelected() && !controller.checkInstanceLoaded()){
			return;
		}
		controller.refreshTableModel(true);
	}

}
