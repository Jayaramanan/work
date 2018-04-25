/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.client.view.appconf.TabSwitchAction;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class TabSwitchActionComboListener implements ActionListener{
	private SettingsController controller;
	private boolean enabled = false;

	public TabSwitchActionComboListener(SettingsController settingsController){
		controller = settingsController;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		if (!enabled){
			return;
		}
		SettingsView view = controller.getView();
		SettingsModel model = controller.getModel();
		view.clearErrors();
		view.stopCellEditing();

		Object current = ((ACComboBox) e.getSource()).getSelectedItem();
		TabSwitchAction currentAction = (TabSwitchAction) current;
		if (currentAction == null || !model.isCurrent()){
			return;
		}

		List<?> settings = model.getCurrentSettings();

		updateSetting(settings, currentAction);

		view.refreshCurrentTable();
	}

	private void updateSetting(List<?> settings, TabSwitchAction action){
		Setting tsSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(Setting.TAB_SWITCH_ACTION_PROPERTY)){
				tsSetting = s;
				break;
			}
		}
		if (tsSetting == null){
			tsSetting = createSetting();
		}
		tsSetting.setValue(action.getValue());
	}

	private Setting createSetting(){
		SettingsModel model = controller.getModel();
		Setting tsSetting = null;
		if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			tsSetting = new UserSetting(user, Setting.APPLET_SECTION, Setting.TAB_SWITCH_ACTION_PROPERTY, null);
			user.getSettings().add((UserSetting) tsSetting);
		} else if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			tsSetting = new GroupSetting(group, Setting.APPLET_SECTION, Setting.TAB_SWITCH_ACTION_PROPERTY, null);
			group.getGroupSettings().add((GroupSetting) tsSetting);
		} else if (model.isCurrentApplication()){
			tsSetting = new ApplicationSetting(Setting.APPLET_SECTION, Setting.TAB_SWITCH_ACTION_PROPERTY, null);
			model.getApplicationSettings().add((ApplicationSetting) tsSetting);
		}
		return tsSetting;
	}

}
