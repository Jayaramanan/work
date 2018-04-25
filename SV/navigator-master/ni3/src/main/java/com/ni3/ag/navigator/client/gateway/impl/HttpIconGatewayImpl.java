/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.IconGateway;
import com.ni3.ag.navigator.client.util.Str;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpIconGatewayImpl extends AbstractGatewayImpl implements IconGateway{
	private static final Logger log = Logger.getLogger(HttpIconGatewayImpl.class);
	private String url;

	public HttpIconGatewayImpl(String url){
		this.url = url;
	}

	@Override
	public List<String> getIconNames(){
		NRequest.Icons.Builder builder = NRequest.Icons.newBuilder();
		builder.setAction(NRequest.Icons.Action.GET_ICON_NAMES);
		NRequest.Icons request = builder.build();
		try{
			List<String> result = new ArrayList<String>();
			ByteString payload = sendRequest(ServletName.IconProvider, request);
			NResponse.Icons ic = NResponse.Icons.parseFrom(payload);
			if (ic.getIconNamesCount() > 0){
				result = ic.getIconNamesList();
			}
			return result;
		} catch (IOException ex){
			showErrorAndThrow("Error get icon names ", ex);
			return null;
		}
	}

	@Override
	public Image loadImage(String name){
		log.debug("Loading " + name);
		Image image = null;
		try{
			final String imageUrl = url + '/' + Str.escape(name);
			image = ImageIO.read(new URL(imageUrl));
		} catch (IOException e){
			log.error("Can't load " + name + " from " + url, e);
		}
		return image;

	}
}
