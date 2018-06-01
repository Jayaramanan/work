/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;

public class ThickClientTreeSelectionListener extends SchemaTreeSelectionListener{

	private ThickClientController controller;

	public ThickClientTreeSelectionListener(ThickClientController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		ThickClientModel model = (ThickClientModel) controller.getModel();
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;

				ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			}

			SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
			model.setCurrentDatabaseInstance(currentDb);
			if (currentDb != null && !controller.checkInstanceLoaded()){
				return;
			}

			controller.refreshTableModel();
		}
	}
}