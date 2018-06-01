package com.ni3.ag.adminconsole.client.controller.appconf.settings;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SettingsModel;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class ImageCacheRefreshButtonListener extends ProgressActionListener{
	private SettingsController controller;

	public ImageCacheRefreshButtonListener(SettingsController settingsController){
		super(settingsController);
		this.controller = settingsController;
	}

	@Override
	public void performAction(ActionEvent e){
		SettingsModel model = controller.getModel();
		if (!model.isCurrent())
			return;
		List<User> currentUsers = getCurrentUsers(model);
		if (currentUsers.isEmpty())
			return;
		for (User u : currentUsers)
			markUser(u);
		SettingsService service = ACSpringFactory.getInstance().getSettingsService();
		service.updateUserSettings(currentUsers);
	}

	private List<User> getCurrentUsers(SettingsModel model){
		List<User> currentUsers = new ArrayList<User>();
		if (model.isCurrentApplication()){
			for (Group g : model.getGroups()){
				currentUsers.addAll(g.getUsers());
			}
		} else if (model.isCurrentGroup()){
			currentUsers.addAll(((Group) model.getCurrentObject()).getUsers());
		} else if (model.isCurrentUser()){
			currentUsers.add((User) model.getCurrentObject());
		}
		return currentUsers;
	}

	private void markUser(User u){
		List<UserSetting> settingsList = u.getSettings();
		if (settingsList == null){
			settingsList = new ArrayList<UserSetting>();
			u.setSettings(settingsList);
		}
		UserSetting us = getRefreshImageCacheSetting(settingsList);
		us.setUser(u);
		us.setValue("true");
	}

	private UserSetting getRefreshImageCacheSetting(List<UserSetting> settingsList){
		for (UserSetting us : settingsList){
			if (us.getProp().equals(Setting.IMAGE_CACHE_REFRESH))
				return us;
		}
		UserSetting us = new UserSetting();
		us.setNew(true);
		us.setSection(UserSetting.APPLET_SECTION);
		us.setProp(Setting.IMAGE_CACHE_REFRESH);
		settingsList.add(us);
		return us;
	}
}
