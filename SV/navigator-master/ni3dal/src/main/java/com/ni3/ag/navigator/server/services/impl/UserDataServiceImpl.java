/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.*;

import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.dictionary.DBObject;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.Context;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.services.UserDataService;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class UserDataServiceImpl extends JdbcDaoSupport implements UserDataService{
	private static final Logger log = Logger.getLogger(UserDataServiceImpl.class);
	private Map<String, AttributeDataSource> attributeDataSources;

	public void setAttributeDataSources(Map<String, AttributeDataSource> attributeDataSources){
		this.attributeDataSources = attributeDataSources;
	}

	private void relinkEdges(int oldNodeId, int newNodeId, Collection<Integer> edges, final String sql){
		List<Object> params = new ArrayList<Object>();
		params.add(newNodeId);
		params.add(oldNodeId);
		params.add(newNodeId);
		for (final Integer edge : edges){
			params.add(edge);
		}
		getJdbcTemplate().update(sql, params.toArray(new Object[params.size()]));
	}

	@Override
	public DBObject loadObject(final ObjectDefinition entity, int objectId, final List<Integer> attributeIds){
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(objectId);
		Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> objects = getDataForIdList(entity, ids);
		com.ni3.ag.navigator.shared.domain.DBObject dbObject = objects.get(objectId);
		if (dbObject == null)
			return null;
		DBObject result = new DBObject(objectId, entity, new HashMap<Attribute, String>());
		for (Integer attrId : dbObject.getData().keySet()){
			Attribute a = entity.getAttribute(attrId);
			result.setAttributeValue(a, dbObject.getData().get(attrId));
		}
		return result;
	}

	@Override
	public void relinkEdges(int oldNodeId, int newNodeId, Collection<Integer> edges){
		String inSql = "";
		for (int i = 0; i < edges.size(); i++){
			if (i > 0)
				inSql += ",";
			inSql += "?";
		}
		final String sql = "UPDATE cis_edges SET fromid = ? WHERE fromid = ? and toid <> ? and id in (" + inSql + ")";
		relinkEdges(oldNodeId, newNodeId, edges, sql);

		final String sqlTo = "UPDATE cis_edges SET toid = ? WHERE toid = ? and fromid <> ? and id in (" + inSql + ")";
		relinkEdges(oldNodeId, newNodeId, edges, sqlTo);
	}

	@Override
	public Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> getDataForIdList(ObjectDefinition entity, Collection<Integer> ids){
		log.trace("Loading data for entity: " + entity.getId());
		Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> destination = new HashMap<Integer, com.ni3.ag.navigator.shared.domain.DBObject>();
		Map<String, List<Attribute>> dataSourceAttributeMap = new HashMap<String, List<Attribute>>();
		for (Attribute attribute : entity.getAttributes()){
			String dataSource = attribute.getDataSource();
			if (dataSource == null)
				log.error("Attribute " + attribute.getLabel() + "(" + attribute.getName() + ") is not mapped to any dataSource");
			if(!dataSourceAttributeMap.containsKey(dataSource))
				dataSourceAttributeMap.put(dataSource, new ArrayList<Attribute>());
			dataSourceAttributeMap.get(dataSource).add(attribute);
		}
		for(int id : ids){
			//fix for salesForce edges
			com.ni3.ag.navigator.shared.domain.DBObject obj = new com.ni3.ag.navigator.shared.domain.DBObject(id, entity.getId());
			obj.setData(new HashMap<Integer, String>());
			destination.put(id, obj);
		}
		for(String dataSource : dataSourceAttributeMap.keySet()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(dataSource);
			attributeDataSource.get(ids, dataSourceAttributeMap.get(dataSource), destination);
		}
		return destination;
	}

	@Override
	public Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> getContextDataForIdList(ObjectDefinition entity,
																							 int contextId, String key,
																							 Collection<Integer> ids){
		Map<Integer, com.ni3.ag.navigator.shared.domain.DBObject> result =
				new HashMap<Integer, com.ni3.ag.navigator.shared.domain.DBObject>();
		Context context = entity.getContext(contextId);
		if(context == null){
			log.error("Entity " + entity.getName() + " does not contain context with id: " + contextId);
			return result;
		}
		log.debug("Getting data for context " + context);
		Map<String, List<Attribute>> dataSourceAttributeMap = new HashMap<String, List<Attribute>>();
		Attribute pkAttribute = context.getPkAttribute();
		for(Attribute attribute : context.getAttributes()){
			if(!dataSourceAttributeMap.containsKey(attribute.getDataSource()))
				dataSourceAttributeMap.put(attribute.getDataSource(), new ArrayList<Attribute>());
			dataSourceAttributeMap.get(attribute.getDataSource()).add(attribute);
		}

		for(String dataSource : dataSourceAttributeMap.keySet()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(dataSource);
			attributeDataSource.getContext(ids, pkAttribute, key, dataSourceAttributeMap.get(dataSource), result);
		}
		return result;
	}

	@Override
	public Map<Integer, Set<Integer>> getDataOfPredefinedForIdList(ObjectDefinition entity, List<Integer> ids){
		log.trace("Loading predefined data for entity: " + entity.getId());
		Map<Integer, Set<Integer>> destination = new HashMap<Integer, Set<Integer>>();
		Map<String, List<Attribute>> dataSourceAttributeMap = new HashMap<String, List<Attribute>>();
		for (Attribute attribute : entity.getAttributes()){
			if(!attribute.isPredefined() || attribute.isInContext())
				continue;
			String dataSource = attribute.getDataSource();
			if (dataSource == null)
				log.error("Attribute " + attribute.getLabel() + "(" + attribute.getName() + ") is not mapped to any dataSource");
			if(!dataSourceAttributeMap.containsKey(dataSource))
				dataSourceAttributeMap.put(dataSource, new ArrayList<Attribute>());
			dataSourceAttributeMap.get(dataSource).add(attribute);
		}
		for(int id : ids){
			//fix for salesForce edges
			destination.put(id, new HashSet<Integer>());
		}
		for(String dataSource : dataSourceAttributeMap.keySet()){
			AttributeDataSource attributeDataSource = attributeDataSources.get(dataSource);
			attributeDataSource.getPredefinedOnly(ids, dataSourceAttributeMap.get(dataSource), destination);
		}
		return destination;
	}
}
