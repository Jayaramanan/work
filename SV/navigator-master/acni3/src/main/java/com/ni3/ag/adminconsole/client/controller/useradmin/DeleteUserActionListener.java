/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.UserPanel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.rules.UserDeltaValidationRule;
import com.ni3.ag.adminconsole.validation.rules.UserObjectRefValidationRule;
import com.ni3.ag.adminconsole.validation.rules.UserOfflineJobsRefValidationRule;

public class DeleteUserActionListener extends ProgressActionListener{

	public DeleteUserActionListener(UserAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		UserAdminController controller = (UserAdminController) getController();

		UserAdminView view = controller.getView();
		UserPanel userPanel = view.getUserPanel();
		userPanel.stopCellEditing();
		view.clearErrors();

		int row = userPanel.getSelectedRow();
		if (row < 0){
			return;
		}

		User userToDelete = userPanel.getTableModel().getSelectedUser(row);
		UserObjectRefValidationRule rule = (UserObjectRefValidationRule) ACSpringFactory.getInstance().getBean(
		        "userObjectRefValidationRule");
		UserDeltaValidationRule dRule = (UserDeltaValidationRule) ACSpringFactory.getInstance().getBean(
		        "userDeltaValidationRule");
		UserOfflineJobsRefValidationRule offRule = (UserOfflineJobsRefValidationRule) ACSpringFactory.getInstance().getBean(
		        "userOfflineJobsRefValidationRule");
		UserAdminModel model = controller.getModel();
		model.setUserToDelete(userToDelete);
		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}
		if (!dRule.performCheck(model)){
			view.renderErrors(dRule.getErrorEntries());
			return;
		}
		if (!offRule.performCheck(model)){
			view.renderErrors(offRule.getErrorEntries());
			return;
		}
		controller.deleteUser(userToDelete);
	}
}
