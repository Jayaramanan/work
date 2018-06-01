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
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class LanguageComboListener implements ActionListener{

	private SettingsController controller;
	private boolean enabled = false;

	public LanguageComboListener(SettingsController controller){
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
		Language currentLanguage = (Language) current;
		if (currentLanguage == null || !model.isCurrent()){
			return;
		}

		List<?> settings = model.getCurrentSettings();

		updateLanguageSetting(settings, currentLanguage);

		view.refreshCurrentTable();
	}

	private void updateLanguageSetting(List<?> settings, Language currentLanguage){
		Setting languageSetting = null;
		for (int i = 0; settings != null && i < settings.size(); i++){
			Setting s = (Setting) settings.get(i);
			if (s.getProp().equals(Setting.LANGUAGE_PROPERTY)){
				languageSetting = s;
				break;
			}
		}
		if (languageSetting == null){
			languageSetting = createLanguageSetting();
		}
		languageSetting.setValue(currentLanguage.getId().toString());
	}

	private Setting createLanguageSetting(){
		SettingsModel model = controller.getModel();
		Setting languageSetting = null;
		if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			languageSetting = new UserSetting(user, Setting.APPLET_SECTION, Setting.LANGUAGE_PROPERTY, null);
			user.getSettings().add((UserSetting) languageSetting);
		} else if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			languageSetting = new GroupSetting(group, Setting.APPLET_SECTION, Setting.LANGUAGE_PROPERTY, null);
			group.getGroupSettings().add((GroupSetting) languageSetting);
		} else if (model.isCurrentApplication()){
			languageSetting = new ApplicationSetting(Setting.APPLET_SECTION, Setting.LANGUAGE_PROPERTY, null);
			model.getApplicationSettings().add((ApplicationSetting) languageSetting);
		}
		return languageSetting;
	}

}