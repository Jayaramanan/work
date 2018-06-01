/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.metaphoradmin;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.NodeMetaphorModel;

public class NodeMetaphorTreeSelectionListener extends SchemaTreeSelectionListener{
	private NodeMetaphorController controller;

	Logger log = Logger.getLogger(NodeMetaphorTreeSelectionListener.class);

	public NodeMetaphorTreeSelectionListener(NodeMetaphorController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		NodeMetaphorModel model = (NodeMetaphorModel) controller.getModel();
		ObjectDefinition currentObject = null;
		Schema currentSchema = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof Schema){
				currentSchema = (Schema) current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			} else if (current instanceof ObjectDefinition){
				currentObject = (ObjectDefinition) current;
				TreePath ppPath = currentPath.getParentPath().getParentPath();
				currentDb = (DatabaseInstance) ppPath.getLastPathComponent();
			}

			ObjectHolder oh = ObjectHolder.getInstance();
			oh.setCurrentObject(currentObject);
			oh.setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObjectDefinition(currentObject);
		if (currentDb != null && currentObject == null && currentSchema == null && !controller.checkInstanceLoaded()){
			return;
		}

		controller.refreshTableModel(true);
	}

}
