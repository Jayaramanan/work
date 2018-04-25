/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.client.view.common.ACComboBox;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class AccessComboListener implements ActionListener{

	private SettingsController controller;
	private boolean enabled = false;

	public AccessComboListener(SettingsController controller){
		this.controller = controller;
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
		if (current == null || !model.isCurrent()){
			return;
		}

		String prop = null;
		String value = null;
		if (current instanceof UpdateAccessType){
			UpdateAccessType access = (UpdateAccessType) current;
			value = access.getValue();
			prop = Setting.OBJECT_UPDATE_RIGHTS_PROPERTY;
		} else if (current instanceof DeleteAccessType){
			DeleteAccessType access = (DeleteAccessType) current;
			value = access.getValue();
			prop = Setting.OBJECT_DELETE_RIGHTS_PROPERTY;
		}

		List<?> settings = model.getCurrentSettings();

		updateAccessSetting(settings, prop, value);

		view.refreshCurrentTable();
	}

	private void updateAccessSetting(List<?> settings, String prop, String value){
		Setting accessSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(prop)){
				accessSetting = s;
				break;
			}
		}
		if (accessSetting == null){
			accessSetting = createAccessSetting(prop);
		}
		accessSetting.setValue(value);
	}

	private Setting createAccessSetting(String prop){
		SettingsModel model = controller.getModel();
		Setting accessSetting = null;
		if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			accessSetting = new UserSetting(user, Setting.APPLET_SECTION, prop, null);
			user.getSettings().add((UserSetting) accessSetting);
		} else if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			accessSetting = new GroupSetting(group, Setting.APPLET_SECTION, prop, null);
			group.getGroupSettings().add((GroupSetting) accessSetting);
		} else if (model.isCurrentApplication()){
			accessSetting = new ApplicationSetting(Setting.APPLET_SECTION, prop, null);
			model.getApplicationSettings().add((ApplicationSetting) accessSetting);
		}
		return accessSetting;
	}
}