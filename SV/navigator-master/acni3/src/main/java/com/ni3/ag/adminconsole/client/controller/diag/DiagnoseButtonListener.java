/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.diag;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.diag.DiagnosticsView;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.DiagnosticsModel;
import com.ni3.ag.adminconsole.shared.service.def.DiagnosticsService;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class DiagnoseButtonListener extends ProgressActionListener{

	public DiagnoseButtonListener(DiagnosticsController diagnosticsController){
		super(diagnosticsController);
	}

	@Override
	public void performAction(ActionEvent e){
		DiagnosticsController controller = (DiagnosticsController) getController();
		final DiagnosticsView view = controller.getView();
		view.renderErrors(null);
		controller.clearTaskResults();
		DiagnosticsModel model = (DiagnosticsModel) controller.getModel();
		if (model.getCurrentSchema() == null)
			return;
		final Schema sch = model.getCurrentSchema();
		DatabaseInstance instance = model.getCurrentDatabaseInstance();
		SessionData.getInstance().setCurrentDatabaseInstance(instance);
		final DiagnosticsService service = ACSpringFactory.getInstance().getDiagnosticsService();
		final List<DiagnoseTaskResult> oldResults = model.getCurrentResults();

		for (DiagnoseTaskResult oldResult : oldResults){
			final DiagnoseTaskResult newResult = service.makeDiagnostic(oldResult, sch);
			view.updateTableModel(newResult);
			view.updateTableForce();
		}

	}

}
