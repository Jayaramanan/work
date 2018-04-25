/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;

public class ConnectionTreeSelectionListener extends SchemaTreeSelectionListener{
	private ObjectConnectionController controller;

	private Logger log = Logger.getLogger(ConnectionTreeSelectionListener.class);

	public ConnectionTreeSelectionListener(ObjectConnectionController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		ObjectConnectionModel model = (ObjectConnectionModel) controller.getModel();
		Schema currentSchema = null;
		ObjectDefinition currentObject = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof Schema){
				currentSchema = (Schema) current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			} else if (current instanceof ObjectDefinition){
				ObjectDefinition od = (ObjectDefinition) current;
				currentObject = od;
				TreePath ppPath = currentPath.getParentPath().getParentPath();
				currentDb = (DatabaseInstance) ppPath.getLastPathComponent();
				ObjectHolder.getInstance().setCurrentObject(currentObject);
			}

			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject + ", CurrentSchema: " + currentSchema);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObject(currentObject);
		if (currentDb != null && currentObject == null && !controller.checkInstanceLoaded()){
			return;
		}

		if (currentObject != null){
			controller.setReferenceData();
		}
		controller.refreshTableModel();
	}
}