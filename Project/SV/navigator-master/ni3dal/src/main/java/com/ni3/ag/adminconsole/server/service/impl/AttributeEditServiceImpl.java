/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.Context;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ContextDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.ObjectDefinitionService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.AttributeEditService;
import com.ni3.ag.adminconsole.validation.ACException;

public class AttributeEditServiceImpl implements AttributeEditService{

	private SchemaDAO schemaDAO;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private ObjectDefinitionService objectDefinitionService;
	private ContextDAO contextDAO;

	public void setContextDAO(ContextDAO contextDAO){
		this.contextDAO = contextDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setObjectDefinitionService(ObjectDefinitionService objectDefinitionService){
		this.objectDefinitionService = objectDefinitionService;
	}

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema schema : schemas){
			Hibernate.initialize(schema.getObjectDefinitions());

			List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
			for (ObjectDefinition objectDefinition : objectDefinitions){
				Hibernate.initialize(objectDefinition.getObjectAttributes());
				if (objectDefinition.getContext() != null)
					Hibernate.initialize(objectDefinition.getContext().getContextAttributes());
				// the following is needed to ensure that halos are nullified for predefined attributes
				// (see AttributeInFilterValidationRule)
				if (objectDefinition.getObjectAttributes() != null)
					for (ObjectAttribute oa : objectDefinition.getObjectAttributes())
						if (oa.isPredefined())
							Hibernate.initialize(oa.getPredefinedAttributes());
			}
		}
		return schemas;
	}

	@Override
	public ObjectDefinition reloadObjectDefinition(Integer id) throws ACException{
		ObjectDefinition od = objectDefinitionDAO.getObjectDefinition(id);
		if (od == null)
			throw new ACException(TextID.MsgObjectWithIdNotFound, new String[] { id != null ? id.toString() : "null" });
		Hibernate.initialize(od.getObjectAttributes());
		// the following is needed to ensure that halos are nullified for predefined attributes
		// (see AttributeInFilterValidationRule)
		if (od.getObjectAttributes() != null)
			for (ObjectAttribute oa : od.getObjectAttributes())
				if (oa.isPredefined())
					Hibernate.initialize(oa.getPredefinedAttributes());
		if (od.getContext() != null)
			Hibernate.initialize(od.getContext().getContextAttributes());
		return od;
	}

	@Override
	public void updateObjectDefinition(ObjectDefinition object, List<Context> contextsToDelete, boolean updateLiveData)
	        throws ACException{
		if (contextsToDelete != null)
			contextDAO.deleteAll(contextsToDelete);
		objectDefinitionService.updateObjectDefinition(object, updateLiveData);
	}

}
