/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.service.def.LoginService;
import com.ni3.ag.adminconsole.validation.ACException;

public class DisconnectButtonListener extends ProgressActionListener{
	SchemaAdminController controller;

	public DisconnectButtonListener(SchemaAdminController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		SchemaAdminView view = controller.getView();
		TreePath tp = view.getSchemaTreeSelectedPath();
		if (currentDB != null && currentDB.isConnected()){
			controller.disconnect(currentDB);
			logout();
			controller.loadSchemaAdminModel();
			controller.setTreeModel();

			view.setSchemaTreeSelectionPath(tp);
		}
	}

	private void logout(){
		LoginService loginService = ACSpringFactory.getInstance().getLoginService();
		try{
			User u = SessionData.getInstance().getUser();
			loginService.logout(u);
		} catch (ACException ex){
			controller.getView().renderErrors(ex);
		}
	}
}
