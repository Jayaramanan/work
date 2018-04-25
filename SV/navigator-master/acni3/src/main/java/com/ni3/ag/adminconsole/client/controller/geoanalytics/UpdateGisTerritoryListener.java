/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GeoAnalyticsView;
import com.ni3.ag.adminconsole.domain.GisTerritory;

public class UpdateGisTerritoryListener extends ProgressActionListener{

	private GeoAnalyticsController controller;

	public UpdateGisTerritoryListener(GeoAnalyticsController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		GeoAnalyticsView view = controller.getView();
		GisTerritory territory = view.getSelectedTerritory();
		TreePath treeSelection = view.getTree().getSelectionPath();

		view.clearErrors();
		view.stopCellEditing();

		if (!controller.applyTerritories()){
			return;
		}

		view.resetEditedFields();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getTree().setSelectionPath(found);
				view.setActiveTableRow(territory);
			}
		}
	}

}
