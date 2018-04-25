/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;
import com.ni3.ag.adminconsole.validation.ACException;

public class LaunchNowButtonListener extends ProgressActionListener{

	private MapJobController controller;

	public LaunchNowButtonListener(MapJobController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		MapJobView view = controller.getView();
		MapJob currentJob = view.getSelectedJob();
		if (currentJob == null || !MapJobStatus.Scheduled.getValue().equals(currentJob.getStatus()) || currentJob.getId() == null){
			return;
		}
		TreePath treeSelection = view.getTree().getSelectionPath();

		if (!controller.checkMandatoryRule()){
			controller.renderMandatoryRuleErrors();
			return;
		}

		MapJobService service = ACSpringFactory.getInstance().getMapJobService();
		List<ErrorEntry> errors = null;
		try{
			service.processJob(currentJob);
		} catch (ACException e1){
			errors = e1.getErrors();
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

		if (errors != null){
			view.renderErrors(errors);
		}
	}

}
