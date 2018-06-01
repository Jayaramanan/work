package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.shared.domain.UserSetting;

public interface UserSettingsDAO{

	UserSetting get(Integer userid, String property);

	void save(UserSetting settings);

	void delete(UserSetting settings);

	List<UserSetting> getSettingsForUser(int userId);

	UserSetting getSettingForUser(int userId, String section, String property);
}
