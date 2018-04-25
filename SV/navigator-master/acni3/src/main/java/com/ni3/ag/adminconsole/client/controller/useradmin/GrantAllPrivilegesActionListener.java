/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.common.ACRootNode;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.privileges.PrivilegesPanel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;

public class GrantAllPrivilegesActionListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(GrantAllPrivilegesActionListener.class);

	public GrantAllPrivilegesActionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();
		UserAdminView view = controller.getView();
		view.getUserPanel().stopCellEditing();
		view.clearErrors();

		UserAdminModel model = controller.getModel();
		Group group = model.getCurrentGroup();
		if (group == null){
			return;
		}

		PrivilegesPanel privilegesPanel = view.getPrivilegesPanel();
		Object selectedObject = privilegesPanel.getSelectedTreeObject();
		if (selectedObject == null)
			return;
		GroupPrivilegesUpdater privilegesUpdater = new GroupPrivilegesUpdater(model.getSchemas());
		selectedObject = privilegesUpdater.convertSelectedObjectToModel(selectedObject);

		log.debug("Granting all privileges for group " + group + ", object - " + selectedObject);

		if (selectedObject instanceof ACRootNode){
			for (Schema schema : model.getSchemas()){
				privilegesUpdater.setCanReadSchema(schema, group, true, true);
			}
		} else if (selectedObject instanceof Schema){
			privilegesUpdater.setCanReadSchema((Schema) selectedObject, group, true, true);
		} else if (selectedObject instanceof ObjectDefinition && privilegesPanel.isSelectedTreeObjectEditable()){
			privilegesUpdater.setCanReadObject((ObjectDefinition) selectedObject, group, true, true);
		} else if (selectedObject instanceof ObjectAttribute && privilegesPanel.isSelectedTreeObjectEditable()){
			ObjectAttribute attribute = (ObjectAttribute) selectedObject;
			boolean canUpdate = isCanUpdateObject(attribute, group);
			privilegesUpdater.setCanReadAttribute(attribute, group, true, true, canUpdate);
		} else if (selectedObject instanceof PredefinedAttribute && privilegesPanel.isSelectedTreeObjectEditable()){
			privilegesUpdater.setCanReadPredefined((PredefinedAttribute) selectedObject, group, true);
		} else{
			return;
		}

		privilegesPanel.refreshTables();
	}

	private boolean isCanUpdateObject(ObjectAttribute attribute, Group group){
		ObjectDefinition object = attribute.getObjectDefinition();
		boolean canUpdate = false;
		for (ObjectGroup og : object.getObjectGroups()){
			if (og.getGroup().equals(group)){
				canUpdate = og.isCanUpdate();
				break;
			}
		}
		return canUpdate;
	}

}
