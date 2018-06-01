/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;

public class DeletePropertyButtonListener extends ProgressActionListener{

	public DeletePropertyButtonListener(LanguageController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageController controller = (LanguageController) getController();

		LanguageView view = controller.getView();
		view.stopCellEditing();
		view.clearErrors();

		int row = view.getSelectedRowModelIndex();
		if (row < 0){
			return;
		}
		controller.deleteProperty(row);
	}
}
