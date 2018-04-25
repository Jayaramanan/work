/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.ac;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.licenses.ac.AdminConsoleLicenseView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.AdminConsoleLicenseModel;

public class ACLicenseTreeSelectionListener extends SchemaTreeSelectionListener{

	private AdminConsoleLicenseView view;
	private AdminConsoleLicenseModel model;
	private AdminConsoleLicenseController controller;

	public ACLicenseTreeSelectionListener(AdminConsoleLicenseController controller){
		this.controller = controller;
		this.view = (AdminConsoleLicenseView) controller.getView();
		this.model = (AdminConsoleLicenseModel) controller.getModel();
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();

		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			}
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);

		if (currentDb != null && !controller.checkInstanceLoaded()){
			return;
		}
		controller.refreshTableModel(true);
	}

}
