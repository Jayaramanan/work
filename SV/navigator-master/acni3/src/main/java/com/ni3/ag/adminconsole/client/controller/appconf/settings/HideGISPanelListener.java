/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.table.AbstractTableModel;

import com.ni3.ag.adminconsole.client.view.appconf.SettingsView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;

public class HideGISPanelListener implements ItemListener{

	private SettingsController controller;

	public HideGISPanelListener(SettingsController controller){
		this.controller = controller;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void itemStateChanged(ItemEvent e){
		Object obj = e.getSource();
		SettingsModel model = controller.getModel();
		SettingsView view = controller.getView();
		AbstractTableModel tModel = (AbstractTableModel) view.getSettingsTable().getModel();

		if (!(obj instanceof JCheckBox))
			return;

		JCheckBox cb = (JCheckBox) obj;

		Setting hideGisPanelSetting = null;
		List settings = null;

		if (model.isCurrentGroup()){
			Group group = (Group) model.getCurrentObject();
			settings = group.getGroupSettings();
		} else if (model.isCurrentUser()){
			User user = (User) model.getCurrentObject();
			settings = user.getSettings();
		} else
			settings = model.getApplicationSettings();

		if (settings != null)
			hideGisPanelSetting = getHideGISPanelSetting(settings);

		boolean containsInheritance = controller.areInheritantSettings(settings);

		String value = String.valueOf(cb.isSelected());
		if (hideGisPanelSetting != null)
			hideGisPanelSetting.setValue(value);
		else if (model.isCurrent() && !containsInheritance){
			Setting gs = null;
			if (model.isCurrentUser()){
				User user = (User) model.getCurrentObject();
				gs = new UserSetting(user, Setting.APPLET_SECTION, Setting.HIDE_GIS_PANEL_PROPERTY, value);
				((UserSetting) gs).setNew(true);
			} else if (model.isCurrentGroup()){
				Group group = (Group) model.getCurrentObject();
				gs = new GroupSetting(group, Setting.APPLET_SECTION, Setting.HIDE_GIS_PANEL_PROPERTY, value);
				((GroupSetting) gs).setNew(true);
			}
			if (gs != null)
				settings.add(gs);
		}

		tModel.fireTableDataChanged();
	}

	private Setting getHideGISPanelSetting(List<?> settings){
		Setting hideGisPanelSetting = null;
		for (int i = 0; i < settings.size(); i++){
			Setting us = (Setting) settings.get(i);
			if (us.getProp().equals(Setting.HIDE_GIS_PANEL_PROPERTY)){
				hideGisPanelSetting = us;
				break;
			}
		}
		return hideGisPanelSetting;
	}

}
