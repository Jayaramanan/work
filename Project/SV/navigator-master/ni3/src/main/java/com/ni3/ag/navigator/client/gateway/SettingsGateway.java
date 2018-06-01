package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.shared.domain.UserSetting;
import java.util.List;

public interface SettingsGateway{
	List<UserSetting> getAllSettings();

	void saveUserSetting(String section, String property, String value);
}
