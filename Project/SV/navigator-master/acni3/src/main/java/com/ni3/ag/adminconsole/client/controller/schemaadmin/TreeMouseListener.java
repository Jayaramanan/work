/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.LoginController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;


public class TreeMouseListener implements MouseListener{

	private SchemaAdminController controller;

	TreeMouseListener(SchemaAdminController controller){
		this.controller = controller;
	}

	@Override
	public void mouseClicked(MouseEvent e){
	}

	@Override
	public void mouseEntered(MouseEvent e){
	}

	@Override
	public void mouseExited(MouseEvent e){
	}

	@Override
	public void mousePressed(MouseEvent e){
		if (e.getClickCount() != 2){
			return;
		}
		SchemaAdminView view = controller.getView();
		TreePath tp = view.getSchemaTreeSelectedPath();
		if (tp != null && tp.getLastPathComponent() instanceof DatabaseInstance){
			DatabaseInstance db = (DatabaseInstance) tp.getLastPathComponent();
			if (db.isInited() && !db.isConnected()){
				LoginController loginController = (LoginController) ACSpringFactory.getInstance().getBean("loginController");
				loginController.run();
				if (!loginController.isSuccess()){
					return;
				}
				SessionData.getInstance().setDatabaseInstanceConnected(db, true);
				if (!MainPanel2.isDefaultUserLanguageInited())
					MainPanel2.initDefaultUserLanguage();

				controller.loadSchemaAdminModel();
				controller.setTreeModel();

				view.setSchemaTreeSelectionPath(tp);
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent e){
	}

}
