/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.domain;

import java.util.*;

import com.ni3.ag.navigator.shared.domain.Prefilter;

public class DataFilter{
	private boolean inverted;
	private Map<Integer, Map<Integer, Set<Integer>>> filter;
	private Set<Integer> filteredValues; // for lazy graph, TODO use only one format

	public DataFilter(Schema schema, List<Integer> filteredValues){
		this(schema, filteredValues, true);
	}

	public DataFilter(Schema schema, List<Integer> filteredValues, boolean inverted){
		this.filteredValues = new LinkedHashSet<Integer>(filteredValues);
		this.inverted = inverted;
		Map<Integer, Integer> value2entity = new HashMap<Integer, Integer>();
		Map<Integer, Integer> value2attribute = new HashMap<Integer, Integer>();
		for (ObjectDefinition e : schema.getDefinitions()){
			for (Attribute a : e.getAttributes()){
				if (!a.isPredefined())
					continue;
				for (PredefinedAttribute pa : a.getValues()){
					value2attribute.put(pa.getId(), a.getId());
					value2entity.put(pa.getId(), e.getId());
				}
			}
		}
		filter = new LinkedHashMap<Integer, Map<Integer, Set<Integer>>>();
		// for multi values we put inverted values to filter
		if (inverted)
			addMultiAttributesToFilter(filteredValues, schema, value2entity, value2attribute);
		addSimpleAttributesToFilter(filteredValues, schema, value2entity, value2attribute);
	}

	public DataFilter(List<Prefilter> prefilters){
		filteredValues = new LinkedHashSet<Integer>();
		filter = new LinkedHashMap<Integer, Map<Integer, Set<Integer>>>();
		for (Prefilter prefilter : prefilters){
			final int eId = prefilter.getObjectDefinitionId();
			if (!filter.containsKey(eId)){
				filter.put(eId, new HashMap<Integer, Set<Integer>>());
			}
			Map<Integer, Set<Integer>> entityFilterData = filter.get(eId);
			if (!entityFilterData.containsKey(prefilter.getAttributeId())){
				entityFilterData.put(prefilter.getAttributeId(), new HashSet<Integer>());
			}
			Set<Integer> attributeFilterData = entityFilterData.get(prefilter.getAttributeId());
			attributeFilterData.add(prefilter.getPredefinedId());
			filteredValues.add(prefilter.getPredefinedId());
		}
	}

	public Map<Integer, Map<Integer, Set<Integer>>> getFilter(){
		return filter;
	}

	public Set<Integer> getFilteredValues(){
		return filteredValues;
	}

	private void addMultiAttributesToFilter(List<Integer> filteredValues, Schema schema, Map<Integer, Integer> value2entity,
			Map<Integer, Integer> value2attribute){
		Set<Attribute> processed = new HashSet<Attribute>();
		for (Integer id : filteredValues){
			if (id < 0 || value2entity.get(id) == null){
				continue;
			}
			ObjectDefinition e = schema.getEntity(value2entity.get(id));
			Attribute a = e.getAttribute(value2attribute.get(id));
			if (!a.isMultivalue())
				continue;
			if (processed.contains(a))
				continue;
			for (PredefinedAttribute pa : a.getValues()){
				if (filteredValues.contains(pa.getId()))
					continue;
				addFilterItem(pa.getId(), e, a);
			}
			processed.add(a);
		}
	}

	private void addSimpleAttributesToFilter(List<Integer> filteredValues, Schema schema,
			Map<Integer, Integer> value2entity, Map<Integer, Integer> value2attribute){
		for (Integer id : filteredValues){
			if (id < 0 || value2entity.get(id) == null){
				continue;
			}
			ObjectDefinition e = schema.getEntity(value2entity.get(id));
			Attribute a = e.getAttribute(value2attribute.get(id));
			if (a.isMultivalue() && inverted)
				continue;
			addFilterItem(id, e, a);
		}
	}

	private void addFilterItem(Integer id, ObjectDefinition e, Attribute a){
		if (!filter.containsKey(e.getId()))
			filter.put(e.getId(), new LinkedHashMap<Integer, Set<Integer>>());
		if (!filter.get(e.getId()).containsKey(a.getId()))
			filter.get(e.getId()).put(a.getId(), new HashSet<Integer>());
		filter.get(e.getId()).get(a.getId()).add(a.getValue(id).getId());
	}
}
