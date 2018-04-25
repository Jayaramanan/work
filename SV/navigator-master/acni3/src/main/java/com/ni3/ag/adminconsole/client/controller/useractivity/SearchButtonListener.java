/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useractivity;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.UserActivityModel;

public class SearchButtonListener extends ProgressActionListener{

	public SearchButtonListener(UserActivityController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserActivityController controller = (UserActivityController) getController();
		UserActivityModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		controller.launchSearch();
	}
}
