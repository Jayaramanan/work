/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;

class SchemaTreeSelectionListener extends com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener{
	private ChartController controller;
	private Logger log = Logger.getLogger(SchemaTreeSelectionListener.class);

	public SchemaTreeSelectionListener(ChartController aThis){
		controller = aThis;
	}

	@Override
	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		TreePath currentPath = e.getNewLeadSelectionPath();
		ChartModel model = controller.getModel();
		Object currentObject = null;
		DatabaseInstance currentDb = null;
		if (currentPath != null){
			Object current = currentPath.getLastPathComponent();
			if (current instanceof DatabaseInstance){
				currentDb = (DatabaseInstance) current;
			} else if (current instanceof Schema){
				currentObject = current;
				currentDb = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			} else if (current instanceof Chart){
				currentObject = current;
				TreePath ppPath = currentPath.getParentPath().getParentPath();
				currentDb = (DatabaseInstance) ppPath.getLastPathComponent();
			}

			ObjectHolder oh = ObjectHolder.getInstance();
			oh.setCurrentPath(currentPath.getPath());
		}

		log.debug("CurrentDb: " + currentDb + ", CurrentObject: " + currentObject);
		SessionData.getInstance().setCurrentDatabaseInstance(currentDb);
		model.setCurrentDatabaseInstance(currentDb);
		model.setCurrentObject(currentObject);
		model.setCurrentObjectChart(null);
		if (currentDb != null && currentObject == null && !controller.checkInstanceLoaded()){
			return;
		}

		controller.updateChartTable();
	}
}
