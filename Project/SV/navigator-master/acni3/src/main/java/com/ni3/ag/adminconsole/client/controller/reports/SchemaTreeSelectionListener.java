/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;

public class SchemaTreeSelectionListener extends com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener{
	private ReportsController controller;
	private Logger log = Logger.getLogger(SchemaTreeSelectionListener.class);

	public SchemaTreeSelectionListener(ReportsController aThis){
		controller = aThis;
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		ReportsModel model = controller.getModel();
		ReportTemplate currentReport = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof ReportTemplate){
				currentReport = (ReportTemplate) current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			}

			ObjectHolder oh = ObjectHolder.getInstance();
			oh.setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentReport: " + currentReport);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentReport(currentReport);
		if (currentDb != null && currentReport == null && !controller.checkInstanceLoaded()){
			return;
		}

		controller.populateXMLAndPreviewToView();
	}
}
