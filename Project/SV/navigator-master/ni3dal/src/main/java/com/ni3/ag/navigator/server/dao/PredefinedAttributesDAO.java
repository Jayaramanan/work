package com.ni3.ag.navigator.server.dao;

import java.util.Map;

public interface PredefinedAttributesDAO{

	Map<Integer, Integer> getNumericPredefinedValues(int schemaId);

}
