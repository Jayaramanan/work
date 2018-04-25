/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.navigator;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.domain.User;

public class RefreshUserEditionListener extends ProgressActionListener{

	public RefreshUserEditionListener(NavigatorLicenseController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		NavigatorLicenseController controller = (NavigatorLicenseController) getController();
		User user = controller.getView().getSelectedUser();
		controller.loadModel();
		controller.updateTreeModel();
		controller.refreshTableModel(false);
		controller.getView().resetEditedFields();
		if (user != null)
			controller.getView().setActiveTableRow(user);
	}
}
