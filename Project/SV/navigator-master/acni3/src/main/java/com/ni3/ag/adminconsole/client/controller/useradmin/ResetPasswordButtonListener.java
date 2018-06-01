/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.UserPanel;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ResetUserPasswordValidationRule;

public class ResetPasswordButtonListener extends ProgressActionListener{

	public ResetPasswordButtonListener(AbstractController controller){
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

		User userToReset = userPanel.getTableModel().getSelectedUser(row);
		UserAdminModel model = controller.getModel();
		model.setUserToReset(userToReset);
		ResetUserPasswordValidationRule rule = (ResetUserPasswordValidationRule) ACSpringFactory.getInstance().getBean(
		        "ResetUserPasswordValidationRule");
		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return;
		}
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		try{
			userToReset = service.resetPassword(userToReset);
		} catch (ACException ex){
			controller.getView().renderErrors(ex.getErrors());
			return;
		}
		controller.reloadData();
		controller.getView().restoreSelection();
	}

}
