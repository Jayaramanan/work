/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.reports.ReportsView;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UpdateReportTemplateButtonListener extends ProgressActionListener implements CellEditorListener{

	private ReportsController controller;

	private ACValidationRule nameValidationRule;

	public UpdateReportTemplateButtonListener(ReportsController controller){
		super(controller);
		this.controller = controller;
		nameValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("reportTemplateNameValidationRule");

	}

	@Override
	public void performAction(ActionEvent e){
		if (!save())
			return;
	}

	@Override
	public void editingCanceled(ChangeEvent e){

	}

	@Override
	public void editingStopped(ChangeEvent e){

	}

	public boolean saveFromTree(ReportTemplate rt){
		ReportsModel model = controller.getModel();
		List<ReportTemplate> reportTemps = model.getReports();
		boolean removed = reportTemps.remove(rt);

		ReportTemplate oldRt = model.getCurrentReport();

		model.setCurrentReport(rt);
		model.setNewReportTemplateName(rt.getName());
		boolean ok = save();
		if (removed)
			reportTemps.add(rt);

		model.setCurrentReport(oldRt);
		model.setNewReportTemplateName(null);

		return ok;
	}

	public boolean save(){
		ReportsView view = controller.getView();
		TreePath selectedPath = view.getTreeSelectionPath();
		view.clearErrors();
		ReportsModel model = controller.getModel();
		ReportTemplate rt = model.getCurrentReport();
		if (rt == null){
			return false;
		}

		controller.populateXMLAndPreviewToModel();

		if (model.getNewReportTemplateName() != null && !nameValidationRule.performCheck(model)){
			view.renderErrors(nameValidationRule.getErrorEntries());
			return false;
		}

		controller.saveCurrentReportTemplate();
		if (selectedPath != null){
			TreeModelSupport treeSupport = new TreeModelSupport();
			TreePath found = treeSupport.findPathByNodes(selectedPath.getPath(), view.getTreeModel());
			view.setTreeSelectionPath(found);
		}
		return true;
	}

}
