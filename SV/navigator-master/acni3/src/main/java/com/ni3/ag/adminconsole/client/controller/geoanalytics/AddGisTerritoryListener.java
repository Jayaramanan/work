/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class AddGisTerritoryListener extends ProgressActionListener{

	public AddGisTerritoryListener(GeoAnalyticsController controller){
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
		controller.getView().clearErrors();
		controller.addNewTerritory();
	}
}
