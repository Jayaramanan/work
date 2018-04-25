/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.languageadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.languageadmin.LanguageView;
import com.ni3.ag.adminconsole.domain.UserLanguageProperty;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.impl.LanguageModel;

public class RefreshPropertyButtonListener extends ProgressActionListener{

	public RefreshPropertyButtonListener(LanguageController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		LanguageController controller = (LanguageController) getController();
		LanguageView view = controller.getView();
		LanguageModel model = controller.getModel();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}

		view.stopCellEditing();
		view.clearErrors();

		UserLanguageProperty property = view.getSelectedLanguageProperty();

		controller.reloadData();

		if (property != null)
			view.setSelectedLanguageProperty(property);
		view.resetEditedFields();
	}
}
