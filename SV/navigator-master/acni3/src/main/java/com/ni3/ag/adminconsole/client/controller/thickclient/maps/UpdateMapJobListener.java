/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.domain.MapJob;

public class UpdateMapJobListener extends ProgressActionListener{

	private MapJobController controller;

	public UpdateMapJobListener(MapJobController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		MapJobView view = controller.getView();
		MapJob currentJob = view.getSelectedJob();
		TreePath treeSelection = view.getTree().getSelectionPath();

		if (!controller.applyMapJobs()){
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
