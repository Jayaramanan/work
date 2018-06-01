/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteModuleButtonListener implements ActionListener{

	private VersioningController controller;
	private ACValidationRule inUseValidationRule;

	public DeleteModuleButtonListener(VersioningController versioningController){
		controller = versioningController;
		inUseValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("ModuleInUseValidationRule");
	}

	@Override
	public void actionPerformed(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		controller.getView().clearErrors();
		if (controller.getModel().getCurrentDatabaseInstance() == null)
			return;
		int index = controller.getView().getModuleTableSelectionIndex();
		if (index == -1)
			return;
		if (controller.getView().isUserModuleTableChanged()){
			if (!controller.canSwitch(true))
				return;
			controller.getView().restoreSelection();
		}
		List<Module> deleted = controller.getModel().getDeletedModules();
		List<Module> current = controller.getModel().getModules();
		controller.getModel().setModudleToDelete(current.get(index));
		if (!inUseValidationRule.performCheck(controller.getModel())){
			controller.getView().renderErrors(inUseValidationRule.getErrorEntries());
			return;
		}
		deleted.add(current.get(index));
		int next = getNextModule(current, index);
		current.remove(index);
		controller.getView().deleteRowsFromModuleTable(index, index);
		controller.getView().updateModuleTable();
		if (next != -1)
			controller.getView().setSelectedModule(next);
	}

	private int getNextModule(List<Module> current, int index){
		if (current.size() == 1)
			return -1;
		if (index == current.size() - 1)
			return index - 1;
		else
			return index;
	}

}
