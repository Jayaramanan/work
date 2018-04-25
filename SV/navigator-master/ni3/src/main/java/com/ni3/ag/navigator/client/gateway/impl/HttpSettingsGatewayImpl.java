package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.SettingsGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpSettingsGatewayImpl extends AbstractGatewayImpl implements SettingsGateway{
	@Override
	public List<UserSetting> getAllSettings(){
		NRequest.Settings request = NRequest.Settings.newBuilder().setAction(NRequest.Settings.Action.GET_ALL_SETTINGS)
				.build();
		try{
			ByteString payload = sendRequest(ServletName.SettingsServlet, request);
			NResponse.Settings protoSettings = NResponse.Settings.parseFrom(payload);
			List<NResponse.Setting> loadedSettings = protoSettings.getSettingsList();
			List<UserSetting> settings = new ArrayList<UserSetting>();
			for (NResponse.Setting ss : loadedSettings){
				UserSetting us = new UserSetting();
				us.setId(ss.getUserId());
				us.setSection(ss.getSection());
				us.setProperty(ss.getProperty());
				us.setValue(ss.getValue());
				settings.add(us);
			}
			return settings;
		} catch (IOException ex){
			showErrorAndThrow("Error get settings from server", ex);
			return null;
		}
	}

	@Override
	public void saveUserSetting(String section, String property, String value){
		NRequest.Settings.Builder request = NRequest.Settings.newBuilder();
		request.setAction(NRequest.Settings.Action.SAVE_USER_SETTING);
		request.setSection(section);
		request.setProperty(property);
		request.setValue(value);
		try{
			sendRequest(ServletName.SettingsServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error get settings from server", ex);
		}
	}
}
