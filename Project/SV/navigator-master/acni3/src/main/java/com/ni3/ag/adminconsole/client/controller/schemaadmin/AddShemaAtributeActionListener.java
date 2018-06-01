/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class AddShemaAtributeActionListener extends ProgressActionListener{

	public AddShemaAtributeActionListener(SchemaAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		SchemaAdminModel adminModel = controller.getModel();
		ObjectDefinition currentObjectDefinition = adminModel.getCurrentObjectDefinition();
		if (currentObjectDefinition == null){
			return;
		}
		controller.getView().getRightPanel().stopEditing();
		controller.getView().clearErrors();
		controller.addNewAtribute();
	}
}
