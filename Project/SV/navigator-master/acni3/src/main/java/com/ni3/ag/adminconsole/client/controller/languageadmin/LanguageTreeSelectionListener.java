/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;

public class LanguageTreeSelectionListener extends SchemaTreeSelectionListener{
	private LanguageController controller;

	Logger log = Logger.getLogger(LanguageTreeSelectionListener.class);

	public LanguageTreeSelectionListener(LanguageController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		LanguageModel model = controller.getModel();
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		Language currentLanguage = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) currentPath.getLastPathComponent();
			} else if (current instanceof Language){
				currentLanguage = (Language) current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			}
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
		}

		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentLanguage(currentLanguage);

		if (currentDb != null && currentLanguage == null && !controller.checkInstanceLoaded()){
			return;
		}
		controller.refreshTableData(false);
	}
}
