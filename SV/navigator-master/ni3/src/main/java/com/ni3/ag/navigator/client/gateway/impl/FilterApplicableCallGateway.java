package com.ni3.ag.navigator.client.gateway.impl;

import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.shared.proto.NRequest;

public abstract class FilterApplicableCallGateway extends AbstractGatewayImpl{
	protected NRequest.Filter makeFilter(DataFilter preFilter){
		NRequest.Filter.Builder protoFilter = NRequest.Filter.newBuilder();
		protoFilter.addAllValueId(preFilter.filter.keySet());
		return protoFilter.build();
	}
}
