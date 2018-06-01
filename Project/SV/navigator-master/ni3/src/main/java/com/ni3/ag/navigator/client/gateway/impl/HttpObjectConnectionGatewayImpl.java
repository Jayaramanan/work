package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.ObjectConnection;
import com.ni3.ag.navigator.client.gateway.ObjectConnectionGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.LineStyle;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpObjectConnectionGatewayImpl extends AbstractGatewayImpl implements ObjectConnectionGateway{

	@Override
	public List<ObjectConnection> getObjectConnections(int schema){
		NRequest.Schema request = NRequest.Schema.newBuilder().setAction(NRequest.Schema.Action.GET_CONNECTIONS)
				.setSchemaId(schema).build();
		try{
			ByteString payload = sendRequest(ServletName.SchemaServlet, request);
			NResponse.ObjectConnections bag = NResponse.ObjectConnections.parseFrom(payload);
			List<ObjectConnection> ocs = new ArrayList<ObjectConnection>();
			List<NResponse.ObjectConnection> objectConnections = bag.getObjectConnectionsList();
			for (NResponse.ObjectConnection oc : objectConnections){
				ObjectConnection domainOC = new ObjectConnection();
				domainOC.setConnectionType(oc.getConnectionType());
				domainOC.setLineStyle(LineStyle.fromInt(oc.getLineStyle()));
				domainOC.setLineWidth(oc.getLineWidth());
				domainOC.setColor(oc.getColor());
				domainOC.setConnectionObject(oc.getConnectionObject());
				domainOC.setFromObject(oc.getFromObject());
				domainOC.setToObject(oc.getToObject());
				ocs.add(domainOC);
			}
			return ocs;
		} catch (IOException ex){
			showErrorAndThrow("Error get object connections", ex);
			return null;
		}
	}
}
