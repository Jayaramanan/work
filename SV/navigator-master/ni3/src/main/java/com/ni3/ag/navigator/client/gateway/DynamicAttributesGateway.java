package com.ni3.ag.navigator.client.gateway;

import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;

public interface DynamicAttributesGateway{
	List<DBObject> getDynamicAttributeValues(Map<Integer, DynamicAttributeDescriptor> dynamicAttributeDescriptors);
}
