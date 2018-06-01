package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.client.domain.Schema;
import java.util.List;

import com.ni3.ag.navigator.client.domain.Schema_;
import com.ni3.ag.navigator.shared.domain.Prefilter;
import com.ni3.ag.navigator.shared.proto.NResponse;

public interface SchemaGateway{

	NResponse.Schema getSchema(Integer schemaId, Integer groupId, Integer languageId);

	List<Schema_> getSchemas();

	List<Prefilter> getPrefilter(int schemaID);
}
