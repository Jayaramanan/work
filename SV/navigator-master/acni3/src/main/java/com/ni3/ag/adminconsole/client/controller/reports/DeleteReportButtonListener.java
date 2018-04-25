/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.reports.ReportsView;
import com.ni3.ag.adminconsole.domain.ReportTemplate;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.ReportsModel;

public class DeleteReportButtonListener extends ProgressActionListener{

	public DeleteReportButtonListener(AbstractController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ReportsController rc = (ReportsController) getController();
		ReportsView view = rc.getView();
		ReportsModel model = rc.getModel();
		view.clearErrors();

		ReportTemplate report = model.getCurrentReport();
		if (report == null){
			return;
		}
		int result = ACOptionPane.showConfirmDialog(view, Translation.get(TextID.ConfirmDeleteReport), Translation
		        .get(TextID.DeleteReport));
		if (result != ACOptionPane.YES_OPTION)
			return;

		Object[] path = getNewSelection(model.getReports(), report, view.getTreeSelectionPath());

		rc.deleteReportTemplate(report);

		rc.loadData();
		rc.updateTree(true);

		TreeModelSupport support = new TreeModelSupport();
		TreePath found = support.findPathByNodes(path, view.getTreeModel());
		view.setTreeSelectionPath(found);
	}

	private Object[] getNewSelection(List<ReportTemplate> reports, ReportTemplate current, TreePath oldPath){
		Object[] path = oldPath.getPath();
		ReportTemplate diffReport = null;
		for (ReportTemplate rt : reports){
			if (!rt.equals(current)){
				diffReport = rt;
				break;
			}
		}
		if (diffReport != null){
			path[path.length - 1] = diffReport;
		} else{
			path = Arrays.copyOf(path, path.length - 1);
		}
		return path;
	}

}
