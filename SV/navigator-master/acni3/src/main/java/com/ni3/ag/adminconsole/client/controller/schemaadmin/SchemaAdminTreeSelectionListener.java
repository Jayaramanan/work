/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class SchemaAdminTreeSelectionListener extends SchemaTreeSelectionListener{
	private static Logger log = Logger.getLogger(SchemaAdminTreeSelectionListener.class);
	private SchemaAdminController controller;

	public SchemaAdminTreeSelectionListener(SchemaAdminController schemaAdminController){
		this.controller = schemaAdminController;
	}

	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		SchemaAdminModel model = (SchemaAdminModel) controller.getModel();
		Schema currentSchema = null;
		ObjectDefinition currentObject = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof Schema){
				Schema schema = (Schema) current;
				currentSchema = schema;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			} else if (current instanceof ObjectDefinition){
				ObjectDefinition object = (ObjectDefinition) current;
				currentObject = object;
				TreePath ppPath = currentPath.getParentPath().getParentPath();
				currentDb = (DatabaseInstance) ppPath.getLastPathComponent();
				ObjectHolder.getInstance().setCurrentObject(currentObject);
			}

			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject + ", CurrentSchema: " + currentSchema);

		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObjectDefinition(currentObject);
		model.setCurrentSchema(currentSchema);

		if (currentDb != null)
			controller.getView().setNotInitedInstance(!currentDb.isInited());

		if (currentDb != null && currentObject == null && !controller.checkInstanceLoaded()){
			return;
		}

		if (currentObject != null){
			controller.populateNewObjectDefinition(currentObject);
		} else{
			controller.populateNewSchemaDefinition(currentSchema);
		}

		ObjectVisibilityStore visStore = ObjectVisibilityStore.getInstance();
		if (currentDb != null && currentDb.isConnected())
			controller.getView().setConnected(true);
		else
			controller.getView().setConnected(false);

		if (currentDb == null || !visStore.isSchemaVisible() || (currentDb != null && !currentDb.isConnected())){
			controller.getView().setEnabledSchemaButtons(false, false, false);
		} else
			controller.getView().setEnabledSchemaButtons(true, currentSchema != null, currentObject != null);

		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			JTree tree = (JTree) e.getSource();
			if (current == null || current instanceof DatabaseInstance || current.equals(tree.getModel().getRoot())){
				controller.updateInfoView();
				controller.setActiveViewInfoPanel();
			} else
				controller.setActiveViewObjectPanel();
		}
	}
}
