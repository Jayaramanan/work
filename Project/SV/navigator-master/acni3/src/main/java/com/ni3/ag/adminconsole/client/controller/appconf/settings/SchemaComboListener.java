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
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class SchemaComboListener implements ActionListener{

	private SettingsController controller;
	private boolean enabled = false;

	public SchemaComboListener(SettingsController controller){
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
		Schema currentSchema = (Schema) current;
		if (currentSchema == null || !model.isCurrent()){
			return;
		}

		List<?> settings = model.getCurrentSettings();

		updateSchemaSetting(settings, currentSchema);

		view.refreshCurrentTable();
	}

	private void updateSchemaSetting(List<?> settings, Schema currentSchema){
		Setting schemaSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(Setting.SCHEME_PROPERTY)){
				schemaSetting = s;
				break;
			}
		}
		if (schemaSetting == null){
			schemaSetting = createSchemaSetting();
		}
		schemaSetting.setValue(currentSchema.getId().toString());
	}

	private Setting createSchemaSetting(){
		SettingsModel model = controller.getModel();
		Setting schemaSetting = null;
		if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			schemaSetting = new UserSetting(user, Setting.APPLET_SECTION, Setting.SCHEME_PROPERTY, null);
			user.getSettings().add((UserSetting) schemaSetting);
		} else if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			schemaSetting = new GroupSetting(group, Setting.APPLET_SECTION, Setting.SCHEME_PROPERTY, null);
			group.getGroupSettings().add((GroupSetting) schemaSetting);
		} else if (model.isCurrentApplication()){
			schemaSetting = new ApplicationSetting(Setting.APPLET_SECTION, Setting.SCHEME_PROPERTY, null);
			model.getApplicationSettings().add((ApplicationSetting) schemaSetting);
		}
		return schemaSetting;
	}
}