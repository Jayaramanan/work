/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.reports.ReportsTreeModel;
import com.ni3.ag.adminconsole.client.view.reports.ReportsView;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AddReportTemplateButtonListener extends ProgressActionListener{

	private ReportsController controller;
	private ACValidationRule reportTemplateNameValidationRule;

	public AddReportTemplateButtonListener(ReportsController controller){
		super(controller);
		this.controller = controller;
		reportTemplateNameValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "reportTemplateNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		ReportsView view = controller.getView();
		ReportsModel model = controller.getModel();
		view.clearErrors();

		final DatabaseInstance db = model.getCurrentDatabaseInstance();
		if (db == null || !db.isConnected()){
			return;
		}

		addNewReportTemplate();

	}

	private boolean addNewReportTemplate(){
		ReportsView view = controller.getView();
		ReportsModel model = controller.getModel();

		String message = Translation.get(TextID.MsgEnterNameOfNewReportTemplate);
		String reportName = ACOptionPane.showInputDialog(view, message, Translation.get(TextID.NewReportTemplate));
		reportName = StringValidator.validate(reportName);
		if (reportName == null || reportName.length() == 0){
			return false;
		}
		model.setNewReportTemplateName(reportName);

		if (!reportTemplateNameValidationRule.performCheck(model)){
			view.renderErrors(reportTemplateNameValidationRule.getErrorEntries());
			model.setNewReportTemplateName(null);
			return false;
		}

		TreePath currentPath = view.getTreeSelectionPath();

		ReportTemplate reportTemplate = new ReportTemplate();
		reportTemplate.setName(reportName);
		reportTemplate = controller.addReportTemplate(reportTemplate);

		controller.loadData();
		controller.updateTree(false);
		restoreTreeSelection(currentPath, reportTemplate);

		model.setNewReportTemplateName(null);

		return true;

	}

	private void restoreTreeSelection(TreePath selectedPath, ReportTemplate reportTemplate){
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], reportTemplate };

		ReportsTreeModel treeModel = controller.getView().getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		if (found != null){
			controller.getView().setTreeSelectionPath(found);
		}
	}

}
