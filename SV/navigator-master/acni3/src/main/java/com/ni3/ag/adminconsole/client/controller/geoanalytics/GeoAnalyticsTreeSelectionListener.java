/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.SchemaTreeSelectionListener;
import com.ni3.ag.adminconsole.client.session.ObjectHolder;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.GeoAnalyticsModel;

public class GeoAnalyticsTreeSelectionListener extends SchemaTreeSelectionListener{

	private GeoAnalyticsController controller;

	public GeoAnalyticsTreeSelectionListener(GeoAnalyticsController controller){
		this.controller = controller;
	}

	public void changeValue(TreeSelectionEvent e){
		controller.getView().clearErrors();
		controller.getView().resetEditedFields();
		GeoAnalyticsModel model = (GeoAnalyticsModel) controller.getModel();
		TreePath currentPath = e.getNewLeadSelectionPath();
		if (currentPath == null)
			return;
		Object current = currentPath.getLastPathComponent();
		if (current == null){
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			SessionData.getInstance().setCurrentDatabaseInstance(null);
			model.setCurrentDatabaseInstance(null);
			model.setCurrentSchema(null);
		} else if (current instanceof DatabaseInstance){
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			SessionData.getInstance().setCurrentDatabaseInstance((DatabaseInstance) current);
			model.setCurrentDatabaseInstance((DatabaseInstance) current);
			model.setCurrentSchema(null);
		} else if (current instanceof Schema){
			DatabaseInstance db = (DatabaseInstance) currentPath.getParentPath().getLastPathComponent();
			model.setCurrentDatabaseInstance(db);
			model.setCurrentSchema((Schema) current);
			ObjectHolder.getInstance().setCurrentPath(currentPath.getPath());
			SessionData.getInstance().setCurrentDatabaseInstance(db);
		}

		if (controller.checkInstanceLoaded())
			controller.refreshTableModel();
	}
}
