package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.gateway.DynamicAttributesGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpDynamicAttributesGatewayImpl extends AbstractGatewayImpl implements DynamicAttributesGateway{
	@Override
	public List<DBObject> getDynamicAttributeValues(Map<Integer, DynamicAttributeDescriptor> descriptors){
		NRequest.CalculateDynamicAttributes.Builder request = NRequest.CalculateDynamicAttributes.newBuilder();
		for (DynamicAttributeDescriptor dad : descriptors.values()){
			request.addDynamicAttribute(NRequest.DynamicAttribute.newBuilder().setFakeId(dad.getFakeAttributeId())
					.setFromEntity(dad.getFromEntity())
					.setFromAttribute(dad.getFromAttribute())
					.setOperation(dad.getOperation())
					.setSchemaId(dad.getSchema())
					.addAllId(dad.getIds()));
		}

		return responseExtractResult(request);
	}

	private List<DBObject> responseExtractResult(NRequest.CalculateDynamicAttributes.Builder request){
		try{
			ByteString resultBytes = sendRequest(ServletName.DynamicAttributesServlet, request.build());
			NResponse.SimpleSearch result = NResponse.SimpleSearch.parseFrom(resultBytes);
			List<NResponse.DBObject> protoObjects = result.getObjectList();
			List<DBObject> objects = new ArrayList<DBObject>();
			for (NResponse.DBObject protoObject : protoObjects){
				DBObject dbo = new DBObject(protoObject.getId(), protoObject.getEntityId());
				List<NResponse.DataPair> protoDataList = protoObject.getDataPairList();
				dbo.setData(new HashMap<Integer, String>());
				for (NResponse.DataPair protoData : protoDataList){
					dbo.getData().put(protoData.getAttributeId(), protoData.getValue());
				}
				objects.add(dbo);
			}
			return objects;
		} catch (IOException ex){
			showErrorAndThrow("Error delete thematic map", ex);
			return null;
		}
	}
}
