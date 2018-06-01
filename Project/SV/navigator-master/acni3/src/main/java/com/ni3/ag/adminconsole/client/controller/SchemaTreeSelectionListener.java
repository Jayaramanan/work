/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;


import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public abstract class SchemaTreeSelectionListener implements TreeSelectionListener{

	@Override
	public void valueChanged(TreeSelectionEvent e){
		TreePath currentPath = e.getNewLeadSelectionPath();
		if (currentPath != null){
			Object[] currentObjects = currentPath.getPath();
			for (int i = 1; i < currentObjects.length; i++)
				if (currentObjects[i] instanceof DatabaseInstance)
					if (processDBInstance((DatabaseInstance) currentObjects[i]))
						return;
		}
		changeValue(e);
	}

	private boolean processDBInstance(DatabaseInstance currentDb){
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);

		if (MainPanel2.isDisabledApp(currentDb)){
			return false;
		}

		boolean currentVisible = MainPanel2.adjustTabVisibilities();
		if (!currentVisible){
			MainPanel2.forwardToSchemaAdmin(false);
			MainPanel2.isDisabledApp(currentDb);
			return true;
		}
		return false;
	}

	public abstract void changeValue(TreeSelectionEvent e);
}
