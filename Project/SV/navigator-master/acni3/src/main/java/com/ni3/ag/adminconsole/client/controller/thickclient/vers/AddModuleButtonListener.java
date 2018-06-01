/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;

public class AddModuleButtonListener implements ActionListener{

	private VersioningController controller;

	public AddModuleButtonListener(VersioningController versioningController){
		controller = versioningController;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		VersioningModel model = controller.getModel();
		controller.getView().clearErrors();
		if (model.getCurrentDatabaseInstance() == null)
			return;
		if (controller.getView().isUserModuleTableChanged()){
			if (!controller.canSwitch(true))
				return;
			controller.getView().restoreSelection();
		}
		List<Module> modules = model.getModules();
		modules.add(new Module());
		controller.getView().updateModuleTable();
		controller.getView().setSelectedModule(modules.size() - 1);
	}

}
