/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;

import com.google.protobuf.GeneratedMessage;
import com.ni3.ag.navigator.client.gateway.LoginGatewayEx;
import com.ni3.ag.navigator.client.gateway.SessionStore;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.gateway.impl.LoginGatewayCommon;
import com.ni3.ag.navigator.shared.login.LoginResult;
import com.ni3.ag.navigator.shared.login.LoginStatus;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.Login;
import org.apache.log4j.Logger;

public class HttpLoginGatewayImpl extends LoginGatewayCommon implements LoginGatewayEx{
	private static final Logger log = Logger.getLogger(HttpLoginGatewayImpl.class);

	@Override
	public LoginResult loginWithUserNamePassword(String userName, String password){
		SessionStore.getInstance().clearSession();
		LoginResult result = super.loginWithUserNamePassword(SystemGlobals.ServerURL, userName, password, false);
		if (result != null && result.getLoginStatus() == LoginStatus.Ok){
			storeSession(result);
		}
		return result;
	}

	@Override
	public boolean logout(){
		return super.logout(SystemGlobals.ServerURL, SessionStore.getInstance().getSessionString());
	}

	@Override
	public String getSaltForUser(String login){
		try{
			final NRequest.Login saltRequest = NRequest.Login.newBuilder()
					.setAction(NRequest.Login.Action.GET_SALT_FOR_USER).setUserName(login).build();
			NResponse.Envelope responce = sendRequest(ServletName.LoginServlet, saltRequest);
			if (responce.getStatus() != NResponse.Envelope.Status.SUCCESS){
				log.error("Login servlet returned status != success");
				return "";
			}
			Login loginResponce = Login.parseFrom(responce.getPayload());
			if (loginResponce.getStatus() != Login.Status.SUCCESS){
				log.error("Login servlet response returned status != success");
				return "";
			}
			return loginResponce.getSalt();
		} catch (IOException e){
			log.error("Error gettings user `" + login + "` salt", e);
			return "";
		}
	}

	@Override
	public boolean changePassword(String currentPassword, String newPassword){
		boolean passwordChanged = false;

		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(NRequest.Login.Action.CHANGE_PASSWORD)
					.setUserId(SystemGlobals.getUserId()).setPassword(currentPassword).setNewPassword(newPassword).build();
			final NResponse.Envelope responseEnvelope = sendRequest(ServletName.LoginServlet, loginRequest);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				final NResponse.Login loginResponse = NResponse.Login.parseFrom(responseEnvelope.getPayload());
				if (loginResponse.getStatus() == NResponse.Login.Status.SUCCESS){
					passwordChanged = true;
				}
			}
		} catch (IOException e){
			// ignore, return value will be false anyway
		}

		return passwordChanged;
	}

	@Override
	public boolean resetPassword(String eMail){
		boolean result = false;

		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(NRequest.Login.Action.RESET_PASSWORD)
					.setUserId(SystemGlobals.getUserId()).setEmail(eMail).build();
			final NResponse.Envelope responseEnvelope = sendRequest(ServletName.LoginServlet, loginRequest);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				final NResponse.Login loginResponse = NResponse.Login.parseFrom(responseEnvelope.getPayload());
				if (loginResponse.getStatus() == NResponse.Login.Status.SUCCESS){
					result = true;
				}
			}
		} catch (IOException e){
			// ignore, return value will be false anyway
		}

		return result;
	}

	@Override
	public LoginResult loginWithSID(String sid){
		LoginResult result;
		SessionStore.getInstance().clearSession();

		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(NRequest.Login.Action.LOGIN_BY_SID)
					.setSid(sid).build();
			final NResponse.Envelope responseEnvelope = sendRequest(ServletName.LoginServlet, loginRequest);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				NResponse.Login loginResponse = NResponse.Login.parseFrom(responseEnvelope.getPayload());
				result = createResultFromProto(loginResponse);
				if (result != null && result.getLoginStatus() == LoginStatus.Ok){
					storeSession(result);
				}
			} else{
				result = new LoginResult(LoginStatus.InvalidLogin, null);
			}
		} catch (IOException e){
			result = new LoginResult(LoginStatus.NoConnectionToServer, null);
		}

		return result;
	}

	@Override
	public LoginResult loginWithSSO(String SSO){
		LoginResult result;
		SessionStore.getInstance().clearSession();

		try{
			final NRequest.Login loginRequest = NRequest.Login.newBuilder().setAction(NRequest.Login.Action.LOGIN_BY_SSO)
					.setSso(SSO).build();
			final NResponse.Envelope responseEnvelope = sendRequest(ServletName.LoginServlet, loginRequest);
			if (responseEnvelope.getStatus() == NResponse.Envelope.Status.SUCCESS){
				NResponse.Login loginResponse = NResponse.Login.parseFrom(responseEnvelope.getPayload());
				result = createResultFromProto(loginResponse);
				if (result != null && result.getLoginStatus() == LoginStatus.Ok){
					storeSession(result);
				}
			} else{
				result = new LoginResult(LoginStatus.InvalidLogin, null);
			}
		} catch (IOException e){
			result = new LoginResult(LoginStatus.NoConnectionToServer, null);
		}
		return result;
	}

	@Override
	public boolean hasConnectionToServer(){
		URLEx url = new URLEx(ServletName.LoginServlet);
		// check connection to server
		if (!url.isConnected()){
			return false;
		}
		url.close();
		return true;
	}

	private void storeSession(LoginResult result){
		if (result.getLoginStatus() == LoginStatus.Ok){
			SessionStore.getInstance().setSessionId(result.getSessionId());
		}
	}

	protected NResponse.Envelope sendRequest(ServletName endpoint, GeneratedMessage message) throws IOException{
		return sendRequest(SystemGlobals.ServerURL, endpoint, message, SessionStore.getInstance().getSessionString());
	}
}
