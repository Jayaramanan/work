/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.DynamicAttributeService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.servlets.GraphServlet;
import com.ni3.ag.navigator.shared.constants.DynamicAttributeOperation;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.DynamicAttributeDescriptor;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DynamicAttributeServiceImpl extends JdbcDaoSupport implements DynamicAttributeService{
	private static final Logger log = Logger.getLogger(DynamicAttributeServiceImpl.class);

	private SchemaLoaderService schemaLoaderService;

	public void setSchemaLoaderService(SchemaLoaderService schemaLoaderService){
		this.schemaLoaderService = schemaLoaderService;
	}

	@Override
	public List<DBObject> getDynamicValues(User user, DynamicAttributeDescriptor descriptor){
		Schema schema = schemaLoaderService.getSchema(descriptor.getSchema());
		if (schema == null){
			log.error("Error get schema by id " + descriptor.getSchema());
			return null;
		}
		log.debug("Schema " + descriptor);
		ObjectDefinition from = schema.getEntity(descriptor.getFromEntity());
		Attribute attribute = from.getAttribute(descriptor.getFromAttribute());
		DynamicAttributeOperation operation = DynamicAttributeOperation.valueOf(descriptor.getOperation());

		GraphNi3Engine graph = GraphServlet.getGraph(descriptor.getSchema());
		List<DBObject> results = new ArrayList<DBObject>();
		AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(attribute.getDataSource());

		for (Integer id : descriptor.getIds()){
			Collection<Integer> connected;
			if(from.isNode())
				connected = graph.getAllConnectedNodes(id);
			else
				connected = graph.getAllEdges(id);
			String res = attributeDataSource.aggregate(attribute, operation.toString(), connected);
			if (res != null){
				DBObject object = new DBObject(id, from.getId());
				object.setData(new HashMap<Integer, String>());
				object.getData().put(descriptor.getFakeId(), res);
				results.add(object);
			}
		}
		return results;
	}
}
