/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.UserEMailValidationRule;

public class SendButtonListener extends ProgressActionListener{
	private static final Logger log = Logger.getLogger(SendButtonListener.class);
	private VersioningController controller;
	private boolean useSSO;

	public SendButtonListener(VersioningController versioningController, boolean sso){
		super(versioningController);
		this.controller = versioningController;
		useSSO = sso;
	}

	@Override
	public void performAction(ActionEvent e){
		VersioningModel model = controller.getModel();
		DatabaseInstance dbi = model.getCurrentDatabaseInstance();
		if (dbi == null)
			return;
		if (!dbi.isConnected())
			return;
		VersioningView view = controller.getView();
		int index = view.getUserModuleTableSelectionIndex();
		if (index == -1)
			return;

		User targetUser = view.getSelectedUser();
		UserEMailValidationRule rule = (UserEMailValidationRule) ACSpringFactory.getInstance().getBean(
		        "UserEMailValidationRule");
		model.setUserToSend(targetUser);
		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		try{
			service.sendStarterModuleToUser(targetUser, useSSO);
			view.clearErrors();
		} catch (ACException ex){
			log.error("Failed to send email to client", ex);
			view.renderErrors(ex.getErrors());
		}
	}
}
