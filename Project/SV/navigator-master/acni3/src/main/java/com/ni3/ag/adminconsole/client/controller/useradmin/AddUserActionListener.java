/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;

public class AddUserActionListener extends ProgressActionListener{

	public AddUserActionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();
		UserAdminModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		controller.getView().getUserPanel().stopCellEditing();
		controller.getView().clearErrors();
		controller.addNewUser();
	}
}
