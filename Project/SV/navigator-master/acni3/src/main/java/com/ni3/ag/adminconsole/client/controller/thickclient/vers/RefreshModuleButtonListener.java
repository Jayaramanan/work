/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;

public class RefreshModuleButtonListener implements ActionListener{

	private VersioningController controller;

	public RefreshModuleButtonListener(VersioningController versioningController){
		controller = versioningController;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		VersioningView view = controller.getView();
		view.stopEditing();
		view.clearErrors();
		VersioningModel model = controller.getModel();
		if (model.getCurrentDatabaseInstance() == null)
			return;

		model.clearDeleted();
		controller.reloadModules();
		controller.reloadPaths();
		controller.updateModuleTable();
	}

}
