/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;

public class RefreshMapJobListener extends ProgressActionListener{

	public RefreshMapJobListener(MapJobController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		MapJobController controller = (MapJobController) getController();
		MapJobView view = controller.getView();
		MapJobModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		MapJob job = view.getSelectedJob();
		TreePath treeSelection = view.getTree().getSelectionPath();

		view.stopCellEditing();
		view.clearErrors();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getTree().setSelectionPath(found);
				view.setActiveTableRow(job);
			}
		}
		view.resetEditedFields();
	}
}