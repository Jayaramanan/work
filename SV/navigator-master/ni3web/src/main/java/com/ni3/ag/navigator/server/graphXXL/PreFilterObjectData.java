/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.graphXXL;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.shared.domain.DBObject;
import org.apache.log4j.Logger;

public class PreFilterObjectData{
	private static final Logger log = Logger.getLogger(PreFilterObjectData.class);
	private int objectDefinitionID;
	private Map<Integer, DBObject> objectMap;
	private List<Attribute> attributes;
	private Pattern pattern = Pattern.compile("(\\{\\d+\\})");

	PreFilterObjectData(){
	}

	public PreFilterObjectData(int objectDefinitionID){
		this.objectDefinitionID = objectDefinitionID;
		initData();
	}

	public int getObjectDefinitionID(){
		return objectDefinitionID;
	}

	private void initData(){
		log.debug("Initing pre filter for object " + objectDefinitionID);
		objectMap = new HashMap<Integer, DBObject>();
		initPredefinedAttributes(objectDefinitionID);
		initObjectData(null);
	}

	private void initObjectData(List<Integer> ids){
		Map<String, List<Attribute>> dataSourceAttributeMap = new HashMap<String, List<Attribute>>();
		for (Attribute attribute : attributes){
			if (!dataSourceAttributeMap.containsKey(attribute.getDataSource()))
				dataSourceAttributeMap.put(attribute.getDataSource(), new ArrayList<Attribute>());
			dataSourceAttributeMap.get(attribute.getDataSource()).add(attribute);
		}
		for (String dataSource : dataSourceAttributeMap.keySet()){
			AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(dataSource);
			if (ids == null)
				attributeDataSource.get(dataSourceAttributeMap.get(dataSource), objectMap);
			else
				attributeDataSource.get(ids, dataSourceAttributeMap.get(dataSource), objectMap);
		}
	}

	private void initPredefinedAttributes(int objectDefinitionID){
		attributes = new ArrayList<Attribute>();
		ObjectDefinition entity = getObjectDefinition(objectDefinitionID);
		for (Attribute attribute : entity.getAttributes()){
			if (!attribute.isPredefined())
				continue;
			attributes.add(attribute);
		}
	}

	private ObjectDefinition getObjectDefinition(int objectDefinitionID){
		SchemaLoaderService schemaLoaderService = NSpringFactory.getInstance().getSchemaLoaderService();
		List<Schema> schemas = schemaLoaderService.getAllSchemas();
		for(Schema sch : schemas)
			for(ObjectDefinition od : sch.getDefinitions())
				if(od.getId() == objectDefinitionID)
					return od;
		return null;
	}

	public boolean checkObject(int objectId, Map<Integer, Set<Integer>> filteredData){
		boolean filteredOut = false;
		DBObject dbObject = objectMap.get(objectId);
		if (dbObject == null){
			updateObject(objectId);
			dbObject = objectMap.get(objectId);
		}
		if (dbObject == null){
			return filteredOut;
		}
		for (Attribute attribute : attributes){
			Set<Integer> filtered = filteredData.get(attribute.getId());
			if (filtered == null){
				continue;
			}
			if (attribute.isMultivalue()){
				List<Integer> value = multiValueToList(dbObject.getData().get(attribute.getId()));
				if (value == null)
					continue;
				filteredOut = true;
				for (Integer val : value){
					if (filtered.contains(val)){// should not be filtered out
						filteredOut = false;
						break;
					}
				}
				if (filteredOut){
					break;
				}
			} else{
				String sValue = dbObject.getData().get(attribute.getId());
				if (sValue == null)
					continue;
				Integer value = Integer.parseInt(sValue);
				if (value != null && filtered.contains(value)){// should be filtered out
					filteredOut = true;
					break;
				}
			}
		}
		return filteredOut;
	}

	private List<Integer> multiValueToList(String multiValue){
		if(multiValue == null)
			return Collections.emptyList();
		Matcher matcher = pattern.matcher(multiValue);
		List<Integer> result = new ArrayList<Integer>();
		while (matcher.find()){
			String sVal = matcher.group();
			result.add(Integer.parseInt(sVal.substring(1, sVal.length() - 1)));
		}
		return result;
	}

	public boolean updateObject(int id){
		initObjectData(Arrays.asList(id));
		return true;
	}
}
