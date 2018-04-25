/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.useradmin.UserPanel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class CopyUserActionListener extends ProgressActionListener{

	public CopyUserActionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();
		controller.getView().clearErrors();
		UserPanel userPanel = controller.getView().getUserPanel();
		userPanel.stopCellEditing();

		DatabaseInstance dbInstance = controller.getModel().getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		int row = userPanel.getSelectedRow();
		if (row < 0){
			return;
		}

		User currentUser = userPanel.getTableModel().getSelectedUser(row);
		if (currentUser != null){
			controller.copyUser(currentUser);
		}

	}
}
