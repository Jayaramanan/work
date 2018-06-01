/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class AddButtonActionListener extends ProgressActionListener{

	public AddButtonActionListener(SettingsController settingsController){
		super(settingsController);
	}

	@Override
	public void performAction(ActionEvent e){
		SettingsController controller = (SettingsController) getController();
		SettingsModel model = (SettingsModel) controller.getModel();
		if (!model.isCurrent())
			return;
		((SettingsView) controller.getView()).stopCellEditing();
		controller.addSetting();
	}

}
