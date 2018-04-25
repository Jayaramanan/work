/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.AddDatasourceService;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseSettingsService;
import com.ni3.ag.adminconsole.validation.ACException;

public class DeleteDatasourceButtonListener extends ProgressActionListener{

	private SchemaAdminController controller;

	public DeleteDatasourceButtonListener(AbstractController controller){
		super(controller);
		this.controller = (SchemaAdminController) controller;
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();
		DatabaseInstance dbi = model.getCurrentDatabaseInstance();
		if (dbi == null)
			return;

		int choice = ACOptionPane.showConfirmDialog(view, Translation.get(TextID.ConfirmRemoveDatasourceFromTree), null);
		if (choice != ACOptionPane.YES_OPTION)
			return;

		AddDatasourceService service = ACSpringFactory.getInstance().getAddDatasourceService();
		try{
			if (dbi.isInited())
				service.deleteDataSource(dbi.getDatabaseInstanceId());
			initDatabaseInstances();
			controller.reloadData();
			controller.updateInfoView();
		} catch (ACException e1){
			view.renderErrors(e1.getErrors());
		}

	}

	private void initDatabaseInstances(){
		SessionData sData = SessionData.getInstance();
		ThreadLocalStorage tlStorage = ThreadLocalStorage.getInstance();
		DatabaseInstance instance = sData.getCurrentDatabaseInstance();

		DatabaseSettingsService dbService = ACSpringFactory.getInstance().getDatabaseSettingsService();
		List<DatabaseInstance> dbInstances = dbService.getDatabaseInstanceNames();
		sData.setDatabaseInstances(dbInstances);

		if (instance != null && instance.isConnected()){
			for (DatabaseInstance inst : dbInstances)
				if (instance.equals(inst)){
					sData.setDatabaseInstanceConnected(inst, true);
					sData.setCurrentDatabaseInstance(inst);
					sData.setDbName(inst.getDatabaseInstanceId());
					tlStorage.setCurrentDatabaseInstanceId(inst.getDatabaseInstanceId());
				}
		}
		controller.reloadData();
		controller.updateInfoView();
		controller.getView().restoreSelection();
	}
}
