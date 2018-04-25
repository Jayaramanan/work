package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.Schema_;
import com.ni3.ag.navigator.client.gateway.SchemaGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.Prefilter;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpSchemaGatewayImpl extends AbstractGatewayImpl implements SchemaGateway{

	@Override
	public NResponse.Schema getSchema(Integer schemaId, Integer groupId, Integer languageId){

		final NRequest.Schema.Builder builder = NRequest.Schema.newBuilder();
		builder.setAction(NRequest.Schema.Action.GET_SCHEMA_DATA);
		builder.setSchemaId(schemaId);
		builder.setLanguageId(languageId);

		final NRequest.Schema request = builder.build();

		try{
			ByteString payload = sendRequest(ServletName.SchemaServlet, request);
			return NResponse.Schema.parseFrom(payload);
		} catch (IOException e){
			showErrorAndThrow("No connection to server", e);
			return null;
		}
	}

	@Override
	public List<Schema_> getSchemas(){
		NRequest.Schema request = NRequest.Schema.newBuilder().setAction(NRequest.Schema.Action.GET_SCHEMAS).build();
		try{
			ByteString payload = sendRequest(ServletName.SchemaServlet, request);
			NResponse.Schemas schemasBag = NResponse.Schemas.parseFrom(payload);
			List<NResponse.Schema> protoSchemas = schemasBag.getSchemasList();
			List<Schema_> schemas = new ArrayList<Schema_>();
			for (NResponse.Schema sch : protoSchemas){
				Schema_ s = new Schema_();
				s.setId(sch.getId());
				s.setName(sch.getName());
				schemas.add(s);
			}
			return schemas;
		} catch (IOException ex){
			showErrorAndThrow("Error get schemas", ex);
			return null;
		}
	}

	@Override
	public List<Prefilter> getPrefilter(int schemaID){
		NRequest.Schema request = NRequest.Schema.newBuilder().setAction(NRequest.Schema.Action.GET_PREFILTER_DATA)
				.setSchemaId(schemaID).build();
		try{
			ByteString payload = sendRequest(ServletName.SchemaServlet, request);
			NResponse.Prefilter protoPrefilter = NResponse.Prefilter.parseFrom(payload);
			List<NResponse.PrefilterItem> protoItems = protoPrefilter.getItemList();
			List<Prefilter> result = new ArrayList<Prefilter>();
			for (NResponse.PrefilterItem protoItem : protoItems){
				Prefilter pf = new Prefilter();
				pf.setId(protoItem.getId());
				pf.setGroupId(protoItem.getGroupId());
				pf.setSchemaId(protoItem.getSchemaId());
				pf.setObjectDefinitionId(protoItem.getObjectDefinitionId());
				pf.setAttributeId(protoItem.getAttributeId());
				pf.setPredefinedId(protoItem.getPredefinedId());
				result.add(pf);
			}
			return result;
		} catch (IOException ex){
			showErrorAndThrow("Error get prefilter for schema (" + schemaID + ")", ex);
			return null;
		}
	}
}
