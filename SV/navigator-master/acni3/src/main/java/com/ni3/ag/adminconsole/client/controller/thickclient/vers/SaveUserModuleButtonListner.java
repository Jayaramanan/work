/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;

public class SaveUserModuleButtonListner extends ProgressActionListener{

	public SaveUserModuleButtonListner(AbstractController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		VersioningController controller = (VersioningController) getController();
		controller.getView().clearErrors();
		User selected = controller.getView().getSelectedUser();
		if (!save())
			return;

		controller.reloadData();
		controller.getView().restoreSelection();
		if (selected != null)
			controller.getView().setSelectedUser(selected);
	}

	public boolean save(){
		VersioningController controller = (VersioningController) getController();
		controller.getView().stopEditing();
		List<User> users;
		Group g = controller.getModel().getCurrentGroup();
		if (g == null)
			users = controller.getAllUsers();
		else
			users = g.getUsers();
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		service.updateUserModules(users);
		return true;
	}

}
