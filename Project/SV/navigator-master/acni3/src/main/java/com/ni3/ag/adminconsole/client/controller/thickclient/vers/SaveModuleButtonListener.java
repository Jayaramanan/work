/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class SaveModuleButtonListener extends ProgressActionListener{
	private ACValidationRule rules[];
	private VersioningController controller;

	public SaveModuleButtonListener(AbstractController controller){
		super(controller);
		this.controller = (VersioningController) controller;
		rules = new ACValidationRule[] {
		        (ACValidationRule) ACSpringFactory.getInstance().getBean("ModuleMandatoryFieldsRule"),
		        (ACValidationRule) ACSpringFactory.getInstance().getBean("UniqModuleNameValidationRule") };
	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;

		controller.getView().clearErrors();
		controller.getView().stopEditing();
		if (!save())
			return;
		controller.reloadData();
		controller.getView().restoreSelection();
	}

	public boolean save(){
		for (ACValidationRule rule : rules){
			if (!rule.performCheck(controller.getModel())){
				controller.getView().renderErrors(rule.getErrorEntries());
				return false;
			}
		}
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		service.updateModules(controller.getModel().getModules(), controller.getModel().getDeletedModules());
		controller.getModel().clearDeleted();
		return true;
	}

}
