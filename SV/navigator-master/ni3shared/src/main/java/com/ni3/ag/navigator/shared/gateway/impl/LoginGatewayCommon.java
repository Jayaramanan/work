package com.ni3.ag.navigator.shared.gateway.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.gateway.AbstractGateway;
import com.ni3.ag.navigator.shared.gateway.LoginGateway;
import com.ni3.ag.navigator.shared.login.LoginResult;
import com.ni3.ag.navigator.shared.login.LoginStatus;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class LoginGatewayCommon extends AbstractGateway implements LoginGateway{
	private static final Logger log = Logger.getLogger(LoginGatewayCommon.class);

	@Override
	public LoginResult loginWithUserNamePassword(String url, String userName, String password, boolean sync){
		LoginResult result;
		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(
					NRequest.Login.Action.LOGIN_BY_PASSWORD).setUserName(userName).setPassword(password).setSync(sync).build();
			final NResponse.Envelope responseEnvelope = sendRequest(url, ServletName.LoginServlet, loginRequest, null);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				NResponse.Login loginResponse = NResponse.Login.parseFrom(responseEnvelope.getPayload());
				result = createResultFromProto(loginResponse);
			} else{
				result = new LoginResult(LoginStatus.InvalidLogin, null);
			}
		} catch (IOException e){
			result = new LoginResult(LoginStatus.NoConnectionToServer, null);
		}

		return result;
	}

	@Override
	public boolean logout(String url, String session){
		boolean ret = false;
		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(NRequest.Login.Action.LOGOUT).build();
			final NResponse.Envelope responseEnvelope = sendRequest(url, ServletName.LoginServlet, loginRequest, session);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				ret = true;
			}
		} catch (IOException ex){
			log.error("No connection to server");
		}
		return ret;
	}

	protected LoginResult createResultFromProto(NResponse.Login protoResult){
		LoginResult result;
		if (protoResult.getStatus() == NResponse.Login.Status.SUCCESS){
			User user = new User();
			user.setId(protoResult.getUser().getUserId());
			user.setFirstName(protoResult.getUser().getFirstName());
			user.setLastName(protoResult.getUser().getLastName());
			user.setUserName(protoResult.getUser().getUserName());
			result = new LoginResult(LoginStatus.Ok, user);
			result.setGroupId(protoResult.getGroupId());
			result.setSessionId(protoResult.getSessionId());
			result.setInstance(protoResult.getInstance());
		} else{
			result = new LoginResult(LoginStatus.InvalidLogin, null);
		}
		return result;
	}
}
