/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;


import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.LoginController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ConnectButtonListener extends ProgressActionListener{

	private final static Logger log = Logger.getLogger(ConnectButtonListener.class);

	public ConnectButtonListener(SchemaAdminController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		SchemaAdminView view = controller.getView();
		SessionData sData = SessionData.getInstance();
		TreePath tp = view.getSchemaTreeSelectedPath();
		DatabaseInstance currentDB = sData.getCurrentDatabaseInstance();
		log.debug("currentDB: " + currentDB);
		if (currentDB != null)
			log.debug(", connected: " + currentDB.isConnected());
		if (currentDB != null && !currentDB.isConnected()){
			LoginController loginController = (LoginController) ACSpringFactory.getInstance().getBean("loginController");
			loginController.run();
			if (!loginController.isSuccess()){
				return;
			}
			sData.setDatabaseInstanceConnected(currentDB, true);
			if (!MainPanel2.isDefaultUserLanguageInited())
				MainPanel2.initDefaultUserLanguage();
			controller.loadSchemaAdminModel();
			controller.setTreeModel();

			view.setSchemaTreeSelectionPath(tp);
		}
	}

}
