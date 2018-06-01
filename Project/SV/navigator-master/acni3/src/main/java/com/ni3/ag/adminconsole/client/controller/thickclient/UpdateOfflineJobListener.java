/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class UpdateOfflineJobListener extends ProgressActionListener{

	private ThickClientController controller;

	public UpdateOfflineJobListener(ThickClientController controller){
		super(controller);
		this.controller = controller;

	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		if (db == null || !db.isConnected())
			return;
		ThickClientView view = controller.getView();
		OfflineJob currentJob = view.getSelectedJob();
		TreePath treeSelection = view.getTree().getSelectionPath();

		if (!controller.applyOfflineJobs()){
			return;
		}

		view.resetEditedFields();

		controller.reloadData();

		if (treeSelection != null){
			TreePath found = view.getTreeModel().findPathByNodes(treeSelection.getPath(), view.getTreeModel());
			if (found != null){
				view.getTree().setSelectionPath(found);
				view.setActiveTableRow(currentJob);
			}
		}
	}

}
