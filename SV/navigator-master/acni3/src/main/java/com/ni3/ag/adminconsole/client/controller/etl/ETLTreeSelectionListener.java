/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.etl;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ETLModel;

public class ETLTreeSelectionListener extends SchemaTreeSelectionListener{

	private ETLController controller;

	public ETLTreeSelectionListener(ETLController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		ETLModel model = controller.getModel();
		TreePath currentPath = e.getNewLeadSelectionPath();
		if (currentPath == null)
			return;
		Object current = currentPath.getLastPathComponent();
		if (current == null){
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			SessionData.getInstance().setCurrentDatabaseInstance(null);
		} else if (current instanceof DatabaseInstance){
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			SessionData.getInstance().setCurrentDatabaseInstance((DatabaseInstance) current);
			model.setCurrentDatabaseInstance((DatabaseInstance) current);
		}
	}
}