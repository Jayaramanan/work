package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gateway.SessionStore;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.gateway.AbstractGateway;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class AbstractGatewayImpl extends AbstractGateway{

	protected ByteString sendRequest(ServletName endpoint, GeneratedMessage message) throws IOException{
		return sendRequest(endpoint, message, true);
	}

	protected ByteString sendRequest(ServletName endpoint, GeneratedMessage message, boolean showError) throws IOException{
		final String serverURL = SystemGlobals.ServerURL;
		NResponse.Envelope env = null;
		try{
			env = sendRequest(serverURL, endpoint, message, SessionStore.getInstance().getSessionString());
			if (NResponse.Envelope.Status.SESSION_EXPIRED == env.getStatus()){
				boolean ok = Ni3.relogin(SystemGlobals.getUser().getUserName());
				if (ok){
					env = sendRequest(serverURL, endpoint, message, SessionStore.getInstance().getSessionString());
				} else{
					return null;
				}
			}
			if (NResponse.Envelope.Status.INVALID_SCHEMA == env.getStatus()){
				Ni3.reloadSchema();
				env = sendRequest(serverURL, endpoint, message, SessionStore.getInstance().getSessionString());
			}
			if (env.getStatus() != NResponse.Envelope.Status.SUCCESS)
				throw new IOException("Got response from server with error status");
			return env.getPayload();
		} catch (IOException ex){
			if (showError){
				showError(env, ex);
			}
			throw ex;
		}
	}

	protected String envelopGetErrorMessage(NResponse.Envelope result){
		return result != null && result.hasErrorMessage() ? result.getErrorMessage() : null;
	}

	private void showError(NResponse.Envelope env, Throwable th){
		Ni3.showServerCommunicationError(th, envelopGetErrorMessage(env));
	}

	protected void showErrorAndThrow(String message, Throwable th){
		RuntimeException rex;
		if (th == null)
			rex = new RuntimeException(message);
		else
			rex = new RuntimeException(message, th);
		showError(null, rex);
		throw rex;
	}
}