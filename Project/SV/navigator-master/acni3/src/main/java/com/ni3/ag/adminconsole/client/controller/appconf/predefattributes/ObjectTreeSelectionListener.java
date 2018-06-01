/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class ObjectTreeSelectionListener extends SchemaTreeSelectionListener{
	private PredefinedAttributeEditController controller;
	private Logger log = Logger.getLogger(ObjectTreeSelectionListener.class);
	private boolean enabled;

	public ObjectTreeSelectionListener(PredefinedAttributeEditController ctrl){
		controller = ctrl;
		enable();
	}

	public void changeValue(TreeSelectionEvent e){
		if (!enabled)
			return;

		PredefinedAttributeEditView view = controller.getView();
		PredefinedAttributeEditModel model = controller.getModel();
		view.stopCellEditing();
		view.clearErrors();
		view.resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();

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
			view.setVisibility(false, true);

			ObjectHolder oh = ObjectHolder.getInstance();
			oh.setCurrentObject(currentObject);
			oh.setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		if (currentDb != null && currentObject == null && currentSchema == null && !controller.checkInstanceLoaded()){
			return;
		}
		model.setCurrentObject(currentObject);

		controller.reloadAttributeTableModel();
	}

	public void disable(){
		enabled = false;

	}

	public void enable(){
		enabled = true;

	}
}
