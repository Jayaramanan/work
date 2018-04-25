/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GeoAnalyticsView;
import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.GeoAnalyticsModel;

public class DeleteGisTerritoryListener extends ProgressActionListener{

	public DeleteGisTerritoryListener(GeoAnalyticsController controller){
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
		view.stopCellEditing();
		view.clearErrors();
		int row = view.getSelectedRowIndex();
		if (row < 0){
			return;
		}

		GisTerritory territoryToDelete = view.getSelectedTerritory();
		model.setCurrentTerritory(territoryToDelete);

		controller.deleteTerritory(territoryToDelete);
	}
}