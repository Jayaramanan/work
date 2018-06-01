/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.diag;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.diag.DiagnosticsView;
import com.ni3.ag.adminconsole.client.view.diag.TaskButton;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.DiagnosticsModel;
import com.ni3.ag.adminconsole.shared.service.def.DiagnosticsService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class FixButtonActionListener extends ProgressActionListener{

	private static final String NAVI_CACHE_VALIDITY_TASK = "com.ni3.ag.adminconsole.server.service.impl.diag.NavigatorCacheValidityTask";

	public FixButtonActionListener(DiagnosticsController diagnosticsController){
		super(diagnosticsController);
	}

	@Override
	public void performAction(ActionEvent e){
		DiagnosticsController controller = (DiagnosticsController) getController();
		TaskButton b = (TaskButton) e.getSource();
		int index = b.getIndex();
		DiagnosticsModel model = (DiagnosticsModel) controller.getModel();
		DiagnoseTaskResult result = model.getCurrentResults().get(index);
		DiagnosticsService service = ACSpringFactory.getInstance().getDiagnosticsService();
		DiagnosticsView view = controller.getView();
		view.renderErrors(null);
		try{

			if (result.getTaskClass().equals(NAVI_CACHE_VALIDITY_TASK)){
				result = prepareCacheInvalidation(result);
				if (result == null)
					return;
			} else{
				User user = SessionData.getInstance().getUser();
				Integer schemaId = model.getCurrentSchema().getId();
				result.setFixParams(new Object[] { user.getId().toString(), schemaId });
			}
			result = service.makeFix(result);
			view.updateTableModel(result);
			model.getCurrentResults().set(index, result);
			view.disableCurrentButton();
			view.updateTable();
		} catch (ACException e1){
			view.renderErrors(e1.getErrors());
		} catch (ACFixTaskException e2){
			view.renderErrors(wrapSimpleErrors(e2));
		}
	}

	private List<ErrorEntry> wrapSimpleErrors(ACFixTaskException e2){
		List<ErrorEntry> ers = new ArrayList<ErrorEntry>();
		ers.add(new ErrorEntry(TextID.MsgEmpty, new String[] { e2.getExceptionClassName() + "\n" + e2.getMessage() }));
		return ers;
	}

	private DiagnoseTaskResult prepareCacheInvalidation(DiagnoseTaskResult result){
		User user = SessionData.getInstance().getUser();

		DatabaseInstance di = SessionData.getInstance().getCurrentDatabaseInstance();
		String navHost = di.getNavigatorHost();
		if (navHost == null || navHost.isEmpty()){
			navHost = requestNavigatorHost();
		}
		if (navHost == null)
			return null;

		result.setFixParams(new Object[] { user, navHost });
		return result;
	}

	private String requestNavigatorHost(){
		return ACOptionPane.showInputDialog(getController().getView(), Translation.get(TextID.NavigatorHost),
		        "test.office.ni3.net:9090/Ni3Web");
	}
}
