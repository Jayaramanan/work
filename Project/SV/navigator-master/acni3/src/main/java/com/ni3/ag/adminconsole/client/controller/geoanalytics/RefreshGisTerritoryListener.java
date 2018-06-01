/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GeoAnalyticsView;
import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.GeoAnalyticsModel;

public class RefreshGisTerritoryListener extends ProgressActionListener{

	public RefreshGisTerritoryListener(GeoAnalyticsController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;
		GeoAnalyticsController controller = (GeoAnalyticsController) getController();
		GeoAnalyticsView view = controller.getView();
		GeoAnalyticsModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		GisTerritory territory = view.getSelectedTerritory();
		TreePath treeSelection = view.getTree().getSelectionPath();

		view.stopCellEditing();
		view.clearErrors();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getTree().setSelectionPath(found);
				view.setActiveTableRow(territory);
			}
		}
		view.resetEditedFields();
	}
}