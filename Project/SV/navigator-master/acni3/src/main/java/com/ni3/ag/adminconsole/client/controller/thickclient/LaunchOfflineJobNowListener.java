/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;
import com.ni3.ag.adminconsole.shared.service.def.ThickClientJobService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class LaunchOfflineJobNowListener extends ProgressActionListener{

	private ACValidationRule updateJobValidationRule;

	public LaunchOfflineJobNowListener(ThickClientController controller){
		super(controller);
		this.updateJobValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "OfflineClientExportJobValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		ThickClientController controller = (ThickClientController) getController();
		ThickClientView view = controller.getView();
		ThickClientModel model = controller.getModel();

		OfflineJob currentJob = view.getSelectedJob();
		if (currentJob == null || currentJob.getId() == null || !controller.testModulesPath())
			return;
		TreePath treeSelection = view.getTree().getSelectionPath();
		view.clearErrors();

		if (!updateJobValidationRule.performCheck(model)){
			controller.getView().renderErrors(updateJobValidationRule.getErrorEntries());
			return;
		}

		ThickClientJobService service = ACSpringFactory.getInstance().getThickClientService();
		List<ErrorEntry> errors = null;
		try{
			service.processJob(currentJob, false);
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
