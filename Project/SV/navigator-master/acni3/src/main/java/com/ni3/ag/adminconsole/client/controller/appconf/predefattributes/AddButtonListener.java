/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.predefattributes;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.appconf.PredefinedAttributeEditView;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;

public class AddButtonListener extends ProgressActionListener{

	public AddButtonListener(PredefinedAttributeEditController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		PredefinedAttributeEditController controller = (PredefinedAttributeEditController) getController();
		PredefinedAttributeEditView view = (PredefinedAttributeEditView) controller.getView();
		PredefinedAttributeEditModel model = (PredefinedAttributeEditModel) controller.getModel();

		ObjectAttribute oa = model.getCurrentAttribute();
		if (oa == null)
			return;
		view.stopCellEditing();
		view.clearErrors();

		controller.addPredefinedAttribute();
	}

}
