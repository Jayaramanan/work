/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.VisibilityService;
import org.apache.log4j.Logger;

public class VisibilityServiceImpl implements VisibilityService{
	private static final Logger log = Logger.getLogger(VisibilityServiceImpl.class);

	@Override
	public Schema getSchemaWithPrivileges(Schema initialSchema, int groupId){
		log.debug("Filter schema according to current accesses");
		Schema newSchema = new Schema();
		newSchema.setId(initialSchema.getId());
		newSchema.setName(initialSchema.getName());
		newSchema.setCreation(initialSchema.getCreation());
		newSchema.setDefinitions(new ArrayList<ObjectDefinition>());

		for (ObjectDefinition initialEntity : initialSchema.getDefinitions()){
			ObjectDefinitionGroup objectGroup = getObjectGroup(groupId, initialEntity.getObjectPermissions());
			if (objectGroup == null || !objectGroup.isCanRead()){
				log.debug("Skipping entity: " + initialEntity.getName());
				continue;
			}
			//TODO move all this to clone function
			ObjectDefinition newEntity = initialEntity.clone();
			newEntity.setSchema(newSchema);

			List<Attribute> attributes = fillAttributes(initialEntity, newEntity, groupId);
			newEntity.setAttributes(attributes);

			newEntity.setContexts(initialEntity.getContexts());
			newEntity.setUrlOperations(initialEntity.getUrlOperations());
			newEntity.setObjectPermissions(new ArrayList<ObjectDefinitionGroup>());
			newEntity.getObjectPermissions().add(objectGroup);
			newSchema.getDefinitions().add(newEntity);
		}
		log.debug("Accessible entities count: " + newSchema.getDefinitions().size());
		return newSchema;
	}

	List<Attribute> fillAttributes(ObjectDefinition initialEntity, ObjectDefinition newEntity, int groupId){
		List<Attribute> attributes = new ArrayList<Attribute>();
		for (Attribute initialAttribute : initialEntity.getAttributes()){
			Attribute attribute = initialAttribute.clone();
			attribute.setEntity(newEntity);

			AttributeGroup ag = getAttributeGroup(groupId, initialAttribute.getAttributeGroups());
			if (ag == null || !ag.getCanRead()){
				log.warn("Attribute group not found for attribute " + attribute.getId() + ", group " + groupId);
				//continue;
				ag = new AttributeGroup();
			}
			attribute.setEditLocked(ag.getEditingLock());
			attribute.setEditUnlocked(ag.getEditingUnlock());

			if (attribute.isPredefined()){
				List<PredefinedAttribute> values = fillValues(initialAttribute.getValues(), attribute);
				attribute.setValues(values);
			}
			attributes.add(attribute);
		}
		return attributes;
	}

	List<PredefinedAttribute> fillValues(List<PredefinedAttribute> initialValues, Attribute attribute){
		List<PredefinedAttribute> values = new ArrayList<PredefinedAttribute>();
		if (initialValues != null){
			for (PredefinedAttribute initialValue : initialValues){
				final PredefinedAttribute value = initialValue.clone();
				value.setAttribute(attribute);
				values.add(value);
			}
		}
		return values;
	}

	ObjectDefinitionGroup getObjectGroup(int groupId, List<ObjectDefinitionGroup> objectGroups){
		ObjectDefinitionGroup result = null;
		for (ObjectDefinitionGroup og : objectGroups){
			if (og.getGroupId() == groupId){
				result = og;
				break;
			}
		}
		return result;
	}

	AttributeGroup getAttributeGroup(int groupId, List<AttributeGroup> attributeGroups){
		AttributeGroup result = null;
		for (AttributeGroup og : attributeGroups){
			if (og.getGroupId() == groupId){
				result = og;
				break;
			}
		}
		return result;
	}
}
