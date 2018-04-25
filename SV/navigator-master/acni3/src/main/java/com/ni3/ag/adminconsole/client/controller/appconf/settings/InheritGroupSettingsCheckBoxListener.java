/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
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

public class InheritGroupSettingsCheckBoxListener implements ItemListener{

	private SettingsController controller;

	private boolean isCurrentUser = false;
	private boolean isCurrentGroup = false;
	private Object currentObject;
	private boolean enabled = false;

	InheritGroupSettingsCheckBoxListener(boolean isCurrentGroup, boolean isCurrentUser, Object currentObject){
		this.isCurrentGroup = isCurrentGroup;
		this.isCurrentUser = isCurrentUser;
		this.currentObject = currentObject;
	}

	public InheritGroupSettingsCheckBoxListener(SettingsController controller){
		this.controller = controller;
	}

	public void setEnabled(boolean enabled){
		this.enabled = enabled;
	}

	protected Setting getInheritanceSetting(List<? extends Setting> settings){
		Setting inheritanceSetting = null;
		for (int i = 0; i < settings.size(); i++){
			Setting us = (Setting) settings.get(i);
			if (us.getProp().equals(Setting.INHERITS_GROUP_SETTINGS_PROPERTY)){
				inheritanceSetting = us;
				break;
			}
		}
		return inheritanceSetting;
	}

	protected Setting createInheritanceSetting(String value){
		Setting gs = null;
		if (isCurrentUser){
			User user = (User) currentObject;
			gs = new UserSetting(user, Setting.APPLET_SECTION, Setting.INHERITS_GROUP_SETTINGS_PROPERTY, value);
			((UserSetting) gs).setNew(true);
		} else if (isCurrentGroup){
			Group group = (Group) currentObject;
			gs = new GroupSetting(group, Setting.APPLET_SECTION, Setting.INHERITS_GROUP_SETTINGS_PROPERTY, value);
			((GroupSetting) gs).setNew(true);
		}
		return gs;
	}

	/**
	 * For application level checkbox should not be available
	 */
	@SuppressWarnings( { "unchecked" })
	@Override
	public void itemStateChanged(ItemEvent e){
		if (!enabled){
			return;
		}
		Object obj = e.getSource();
		SettingsView view = controller.getView();
		AbstractTableModel tModel = (AbstractTableModel) view.getSettingsTable().getModel();
		SettingsModel model = (SettingsModel) controller.getModel();
		isCurrentGroup = model.isCurrentGroup();
		isCurrentUser = model.isCurrentUser();
		currentObject = model.getCurrentObject();

		if (!isCurrentUser && !isCurrentGroup)
			return;
		if (!(obj instanceof JCheckBox))
			return;

		JCheckBox cb = (JCheckBox) obj;

		List settings = null;
		if (isCurrentGroup){
			Group group = (Group) currentObject;
			settings = group.getGroupSettings();
		} else if (isCurrentUser){
			User user = (User) currentObject;
			settings = user.getSettings();
		}

		Setting inheritanceSetting = getInheritanceSetting(settings);

		String value = String.valueOf(cb.isSelected());
		if (inheritanceSetting != null)
			inheritanceSetting.setValue(value);
		else{
			Setting gs = createInheritanceSetting(value);
			if (gs != null)
				settings.add(gs);
		}

		if (!cb.isSelected()){
			List<Setting> inheritantSettings = controller.getInheritantSettings();
			leaveFixedSettings(settings, inheritantSettings);
		} else{
			// delete props
			List<Setting> toRemove = new ArrayList<Setting>();
			int minIndex = Integer.MAX_VALUE;
			int maxIndex = Integer.MIN_VALUE;
			for (String node : Setting.SETTINGS_MENU_TREE_NODES){
				for (int i = 0; i < settings.size(); i++){
					Setting us = (Setting) settings.get(i);
					if (us.getProp().startsWith(node) || !controller.isFixed(us)){
						if (i < minIndex)
							minIndex = i;
						if (i > maxIndex)
							maxIndex = i;
						toRemove.add(us);

					}
				}
			}
			if (minIndex < Integer.MAX_VALUE && maxIndex > Integer.MIN_VALUE)
				tModel.fireTableRowsDeleted(minIndex, maxIndex);
			settings.removeAll(toRemove);
		}
		populateDataToCombo();
		tModel.fireTableDataChanged();
		view.setTopPanelEnabled(!cb.isSelected());
	}

	protected void leaveFixedSettings(List<Setting> settings, List<Setting> inheritantSettings){
		for (Setting is : inheritantSettings){
			for (String node : Setting.SETTINGS_MENU_TREE_NODES){
				if (is.getProp().startsWith(node)){
					if (!settings.contains(is))
						settings.add(is);
					break;
				}
			}
			if (is.getProp().equals(Setting.SCHEME_PROPERTY) || is.getProp().equals(Setting.LANGUAGE_PROPERTY)
			        || is.getProp().equals(Setting.TAB_SWITCH_ACTION_PROPERTY)
			        || is.getProp().equals(Setting.OBJECT_UPDATE_RIGHTS_PROPERTY)
			        || is.getProp().equals(Setting.OBJECT_DELETE_RIGHTS_PROPERTY)
			        || is.getProp().equals(Setting.HIDE_GIS_PANEL_PROPERTY)
			        || is.getProp().equals(Setting.HELP_DOCUMENT_URL_PROPERTY)){
				if (!settings.contains(is)){
					settings.add(is);
				}
			}
		}
	}

	private void populateDataToCombo(){
		controller.setListenersEnabled(false);
		controller.populateDataToCombo();
		controller.setListenersEnabled(true);
	}

}
