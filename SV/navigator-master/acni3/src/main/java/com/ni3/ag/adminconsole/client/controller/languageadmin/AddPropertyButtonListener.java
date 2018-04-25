/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class AddPropertyButtonListener extends ProgressActionListener{
	public AddPropertyButtonListener(LanguageController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageController controller = (LanguageController) getController();
		controller.getView().stopCellEditing();
		controller.getView().clearErrors();
		controller.addNewProperty();
	}
}
