/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class CancelButtonActionListener extends ProgressActionListener{

	public CancelButtonActionListener(SchemaAdminController ctx){
		super(ctx);
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		SchemaAdminView view = controller.getView();
		view.getRightPanel().stopEditing();
		ObjectAttribute selectedAttribute = null;
		if (controller.getModel().getCurrentObjectDefinition() != null)
			selectedAttribute = view.getRightPanel().getSelectedAttribute();
		ObjectDefinition selectedObject = view.getRightPanel().getSelectedObject();
		SchemaAdminModel adminModel = controller.getModel();
		if (adminModel.getCurrentObjectDefinition() == null && adminModel.getCurrentSchema() == null){
			return;
		}

		controller.getView().clearErrors();
		if (adminModel.getCurrentObjectDefinition() != null){
			controller.reloadCurrent();
		} else{
			controller.reloadData();
		}
		view.restoreSelection();
		if (selectedAttribute != null)
			view.getRightPanel().setActiveTableRow(selectedAttribute);
		if (selectedObject != null)
			view.getRightPanel().setActiveObjectTableRow(selectedObject);
		view.resetEditedFields();
	}
}
