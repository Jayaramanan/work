/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.attributes;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.AttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.AttributeEditModel;

public class ObjectTreeSelectionListener extends SchemaTreeSelectionListener{
	private static Logger log = Logger.getLogger(ObjectTreeSelectionListener.class);
	private AttributeEditController controller;

	public ObjectTreeSelectionListener(AttributeEditController attributeEditController){
		controller = attributeEditController;
	}

	public void changeValue(TreeSelectionEvent e){
		AttributeEditView view = controller.getView();
		AttributeEditModel model = controller.getModel();
		view.stopCellEditing();
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();

		ObjectDefinition currentObject = null;
		Schema currentSchema = null;
		DatabaseInstance currentDb = null;
		boolean tableVisible = false;
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
				tableVisible = true;
				ObjectHolder.getInstance().setCurrentObject((ObjectDefinition) currentObject);
			}

			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObjectDefinition(currentObject);
		view.setTableVisible(tableVisible);

		if (currentDb != null && currentObject == null && currentSchema == null && !controller.checkInstanceLoaded()){
			return;
		}

		if (currentObject != null || currentSchema != null){
			controller.updateTable();
		}
	}
}
