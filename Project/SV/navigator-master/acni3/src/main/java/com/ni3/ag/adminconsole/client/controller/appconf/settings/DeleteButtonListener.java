/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class DeleteButtonListener extends ProgressActionListener{
	private ACValidationRule deleteRule;

	public DeleteButtonListener(SettingsController settingsController){
		super(settingsController);
		deleteRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("deleteSettingsValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		SettingsController controller = (SettingsController) getController();
		SettingsModel model = (SettingsModel) controller.getModel();
		SettingsView view = (SettingsView) controller.getView();

		view.clearErrors();

		if (!model.isCurrent())
			return;

		List<Setting> settings = new ArrayList<Setting>();
		if (model.isCurrentApplication())
			settings.addAll(model.getApplicationSettings());
		else if (model.isCurrentGroup())
			settings.addAll(((Group) model.getCurrentObject()).getGroupSettings());
		else if (model.isCurrentUser())
			settings.addAll(((User) model.getCurrentObject()).getSettings());

		// backup
		List<Setting> backup = model.getDeletableSettings();

		model.setDeletableSettings(new ArrayList<Setting>());

		int[] selected = view.getSettingsTable().getSelectedRows();
		if (selected.length == 0)
			return;
		for (int i = 0; i < selected.length; i++){
			selected[i] = view.getSettingsTable().convertRowIndexToModel(selected[i]);
			model.addDeletableSetting(settings.get(selected[i]));
		}

		boolean ok = deleteRule.performCheck(model);

		// restore backup
		model.setDeletableSettings(backup);

		if (!ok){
			view.renderErrors(deleteRule.getErrorEntries());
		} else{
			controller.deleteSettings();
		}
	}

}
