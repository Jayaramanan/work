package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserSettingsDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.domain.UserSetting;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.Settings;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class SettingsServlet extends Ni3Servlet{
	private static final long serialVersionUID = -4499956441213472119L;

	private NRequest.Settings request;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		InputStream is = getInputStream(request);
		this.request = null;
		NRequest.Settings protoRequest = NRequest.Settings.parseFrom(is);
		NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		switch (protoRequest.getAction()){
			case GET_ALL_SETTINGS:
				handleGetSettings(responseBuilder);
				break;
			case SAVE_USER_SETTING:
				handleSaveUserSetting(protoRequest, responseBuilder);
				break;
		}
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		sendResponse(request, response, responseBuilder);
	}

	private void handleSaveUserSetting(Settings protoRequest, Builder responseBuilder){
		this.request = protoRequest;
		final UserSettingsDAO dao = NSpringFactory.getInstance().getUserSettingsDao();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		int userId = storage.getCurrentUser().getId();
		UserSetting currentSetting = dao.get(userId, protoRequest.getProperty());
		if (currentSetting == null){
			currentSetting = new UserSetting();
			currentSetting.setId(userId);
			currentSetting.setProperty(protoRequest.getProperty());
			currentSetting.setSection(protoRequest.getSection());
		}

		currentSetting.setValue(protoRequest.getValue());
		dao.save(currentSetting);
	}

	private void handleGetSettings(NResponse.Envelope.Builder responseBuilder){
		UserSettingsDAO settingsDAO = NSpringFactory.getInstance().getUserSettingsDao();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		int userId = storage.getCurrentUser().getId();
		List<UserSetting> settings = settingsDAO.getSettingsForUser(userId);
		NResponse.Settings.Builder settingsBuilder = NResponse.Settings.newBuilder();
		for (UserSetting us : settings){
			if (us.getValue() == null)
				continue;
			settingsBuilder.addSettings(NResponse.Setting.newBuilder().setUserId(userId).setSection(us.getSection())
					.setProperty(us.getProperty()).setValue(us.getValue()));
		}
		responseBuilder.setPayload(settingsBuilder.build().toByteString());
	}

	@Override
	protected DeltaHeader getTransactionDeltaForRequest(){
		DeltaHeader result = DeltaHeader.DO_NOTHING;
		if (request != null){
			ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
			User creator = storage.getCurrentUser();
			Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();

			final String propertyName = request.getProperty();
			params.put(DeltaParamIdentifier.UpdateSettingsPropertyName, new DeltaParam(
					DeltaParamIdentifier.UpdateSettingsPropertyName, propertyName));

			result = new DeltaHeader(DeltaType.SETTING_UPDATE, creator, params);
		}

		return result;
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}
}
