/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;

public class RefreshOfflineJobListener extends ProgressActionListener{

	public RefreshOfflineJobListener(ThickClientController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ThickClientController controller = (ThickClientController) getController();
		ThickClientView view = controller.getView();
		ThickClientModel model = controller.getModel();

		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		OfflineJob job = view.getSelectedJob();
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