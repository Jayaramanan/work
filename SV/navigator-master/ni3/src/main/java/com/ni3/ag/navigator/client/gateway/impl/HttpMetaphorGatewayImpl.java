package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.MetaphorGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpMetaphorGatewayImpl extends AbstractGatewayImpl implements MetaphorGateway{

	@Override
	public List<String> getMetaphorSets(int schemaID){
		NRequest.Schema request = NRequest.Schema.newBuilder().setAction(NRequest.Schema.Action.GET_METAPHOR_SETS)
				.setSchemaId(schemaID).build();
		try{
			ByteString payload = sendRequest(ServletName.SchemaServlet, request);
			NResponse.MetaphorSets protoSets = NResponse.MetaphorSets.parseFrom(payload);
			return protoSets.getMetaphorSetsList();
		} catch (IOException ex){
			showErrorAndThrow("Error get metaphor sets", ex);
			return null;
		}
	}

}
