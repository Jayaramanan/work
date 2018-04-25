/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.protobuf.ByteString;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.IconDAO;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class IconProvider extends Ni3Servlet{

	private static final long serialVersionUID = -1740813387042124684L;

	private static IconDAO iconDAO = NSpringFactory.getInstance().getIconDao();

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		final InputStream is = request.getInputStream();
		final NRequest.Icons protoRequest = NRequest.Icons.parseFrom(is);
		final NResponse.Icons.Builder iconsResponse = NResponse.Icons.newBuilder();
		switch (protoRequest.getAction()){
			case GET_ICON_BY_NAME:
				final byte[] imageBytes = iconDAO.getImageBytes(protoRequest.getIconName());
				if (imageBytes != null && imageBytes.length > 0){
					iconsResponse.setIconBytes(ByteString.copyFrom(imageBytes));
				}
				break;
			case GET_ICON_NAMES:
				final List<String> iconNames = iconDAO.getIconNames();
				if (iconNames != null && !iconNames.isEmpty()){
					iconsResponse.addAllIconNames(iconNames);
				}
				break;
		}
		final NResponse.Envelope.Builder responseBuilder = NResponse.Envelope.newBuilder();
		responseBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		final ByteString payload = iconsResponse.build().toByteString();
		responseBuilder.setPayload(payload);
		final NResponse.Envelope env = responseBuilder.build();
		env.writeTo(response.getOutputStream());
	}

	@Override
	protected UserActivityType getActivityType(){
		// not used
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}

}
