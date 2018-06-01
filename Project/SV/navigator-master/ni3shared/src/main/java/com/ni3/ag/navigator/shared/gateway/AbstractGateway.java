package com.ni3.ag.navigator.shared.gateway;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.google.protobuf.GeneratedMessage;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NResponse;

public abstract class AbstractGateway{
	protected static final String COOKIE = "Cookie";

	protected NResponse.Envelope sendRequest(String serverURL, ServletName endpoint, GeneratedMessage message,
	        String sessionId) throws IOException{
		final String endpointUrl = serverURL + endpoint.getUrl();
		URL url = new URL(endpointUrl);
		final URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		if (sessionId != null){
			connection.setRequestProperty(COOKIE, sessionId);
		}
		message.writeTo(connection.getOutputStream());

		NResponse.Envelope result = NResponse.Envelope.parseFrom(connection.getInputStream());
		return result;
	}

}
