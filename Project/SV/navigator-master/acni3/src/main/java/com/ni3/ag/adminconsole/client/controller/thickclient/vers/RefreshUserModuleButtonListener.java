/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class RefreshUserModuleButtonListener implements ActionListener{
	private VersioningController controller;

	public RefreshUserModuleButtonListener(VersioningController versioningController){
		controller = versioningController;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		controller.getView().clearErrors();
		User selected = controller.getView().getSelectedUser();
		controller.reloadData();
		controller.getView().restoreSelection();
		if (selected != null)
			controller.getView().setSelectedUser(selected);
	}

}
