/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.LicenseGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.License;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpLicenseGatewayImpl extends AbstractGatewayImpl implements LicenseGateway{
	private static final Logger log = Logger.getLogger(HttpLicenseGatewayImpl.class);

	@Override
	public License getLicense(){
		NRequest.License.Builder request = NRequest.License.newBuilder();
		request.setAction(NRequest.License.Action.GET_LICENSE);
		final License license = new License();
		try{
			ByteString payload = sendRequest(ServletName.LicenseProvider, request.build());
			NResponse.License result = NResponse.License.parseFrom(payload);

			license.setValid(result.getValid());
			license.setBaseModule(result.getBaseModule());
			license.setDataCaptureModule(result.getDataCaptureModule());
			license.setChartsModule(result.getChartsModule());
			license.setMapsModule(result.getMapsModule());
			license.setGeoAnalyticsModule(result.getGeoAnalyticsModule());
			license.setRemoteClientModule(result.getRemoteClientModule());
			license.setReportsModule(result.getReportsModule());
		} catch (IOException ex){
			log.error("Error get license", ex);
		}
		return license;
	}
}
