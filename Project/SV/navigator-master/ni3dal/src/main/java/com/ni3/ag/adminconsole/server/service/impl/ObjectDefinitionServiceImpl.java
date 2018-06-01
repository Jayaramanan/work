/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.*;
import java.util.Map;
import javax.sql.DataSource;

import com.ni3.ag.adminconsole.domain.*;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.server.service.ObjectDefinitionService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

public class ObjectDefinitionServiceImpl implements ObjectDefinitionService{
	private static final Logger log = Logger.getLogger(ObjectDefinitionServiceImpl.class);

	private DataSource dataSource;
	private SchemaDAO schemaDAO;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private GroupDAO groupDAO;
	private ACValidationRule userDataExistanceRule;
	private ACValidationRule userTableNameRule;
	private ACValidationRule attributeInMetaphorUseRule;

	private UserTableStructureService userTableStructureService;

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setUserTableNameRule(ACValidationRule userTableNameRule){
		this.userTableNameRule = userTableNameRule;
	}

	public void setUserDataExistanceRule(ACValidationRule userDataExistanceRule){
		this.userDataExistanceRule = userDataExistanceRule;
	}

	public void setAttributeInMetaphorUseRule(ACValidationRule attributeInMetaphorUseRule){
		this.attributeInMetaphorUseRule = attributeInMetaphorUseRule;
	}

	@Override
	public ObjectDefinition addObjectDefinition(Schema parent, String name, User user) throws ACException{
		log.info("AddObjectDefinition");
		if (parent == null){
			throw new ACException(TextID.MsgSchemaNotSelected);
		}
		ObjectDefinition od = new ObjectDefinition();
		od.setCreationDate(new Date());
		od.setName(name);
		od.setDescription(name);
		od.setCreatedBy(user);

		Schema schema = schemaDAO.getSchema(parent.getId());
		if (schema == null){
			throw new ACException(TextID.MsgSchemaWithIdDoesNotExists, new String[] { parent.getId().toString() });
		}
		od.setSchema(schema);

		od.setObjectType(ObjectType.NODE);
		od = objectDefinitionDAO.saveOrUpdate(od);
		setObjectSort(od);

		adjustVisibilities(od, schema);
		od = objectDefinitionDAO.saveOrUpdate(od);

		return od;
	}

	private void adjustVisibilities(ObjectDefinition od, Schema schema){
		Map<Integer, Boolean> canReadMap = new HashMap<Integer, Boolean>();
		for (SchemaGroup sg : schema.getSchemaGroups())
			canReadMap.put(sg.getGroup().getId(), sg.isCanRead());
		List<Group> groups = groupDAO.getGroups();
		od.setObjectGroups(new ArrayList<ObjectGroup>());
		for (Group g : groups){
			ObjectGroup oug = new ObjectGroup(od, g);
			if (canReadMap.containsKey(g.getId()))
				oug.setCanRead(canReadMap.get(g.getId()));
			else
				oug.setCanRead(false);
			oug.setCanCreate(false);
			oug.setCanUpdate(false);
			oug.setCanDelete(false);
			od.getObjectGroups().add(oug);
			g.getObjectGroups().add(oug);
		}
	}

	private void setObjectSort(ObjectDefinition od){
		od.setSort(od.getId());
	}

	@Override
	public void addAttributeGroups(List<ObjectAttribute> attributes, ObjectDefinition parent){
		Map<Integer, Boolean> canReadMap = new HashMap<Integer, Boolean>();
		for (ObjectGroup og : parent.getObjectGroups())
			canReadMap.put(og.getGroup().getId(), og.isCanRead());
		List<Group> groups = getGroupDAO().getGroups();
		for (Group group : groups){
			if (group.getAttributeGroups() == null){
				group.setAttributeGroups(new ArrayList<AttributeGroup>());
			}
			for (ObjectAttribute attribute : attributes){
				AttributeGroup ag = new AttributeGroup(attribute, group);
				if (canReadMap.containsKey(group.getId()))
					ag.setCanRead(canReadMap.get(group.getId()));
				group.getAttributeGroups().add(ag);
			}
			groupDAO.update(group);
		}
	}

	@Override
	public List<ObjectAttribute> getNewAttributes(List<ObjectAttribute> attributes){
		List<ObjectAttribute> newAttributes = new ArrayList<ObjectAttribute>();
		if (attributes == null){
			return newAttributes;
		}
		for (ObjectAttribute attribute : attributes){
			if (attribute.getId() == null || attribute.getId() <= 0){
				newAttributes.add(attribute);
			}
		}
		return newAttributes;
	}

	@Override
	public ObjectDefinition updateObjectDefinition(ObjectDefinition od, boolean ignoreUserData) throws ACException{
		SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentObjectDefinition(od);
		if (!ignoreUserData && !userDataExistanceRule.performCheck(model)){
			throw new ACException(userDataExistanceRule.getErrorEntries());
		}

		String oldTable = od.getTableName();

		log.info("UpdateObjectDefinition");
		userTableNameRule.performCheck(model);

		boolean tableNameChanged = oldTable != null && !oldTable.equalsIgnoreCase(od.getTableName());
		if (tableNameChanged){
			ACRoutingDataSource dSource = (ACRoutingDataSource) dataSource;
			if (dSource.isCluster()){
				throw new ACException(TextID.MsgCantRenameOnClusteredInstance);
			}
		}
		if (od.getId() != null){
			model.setCurrentObjectDefinition(od);
			if (!attributeInMetaphorUseRule.performCheck(model))
				throw new ACException(attributeInMetaphorUseRule.getErrorEntries());
		}

		List<ObjectAttribute> newAttributes = getNewAttributes(od.getObjectAttributes());
		od = objectDefinitionDAO.saveOrUpdate(od);
		if (newAttributes.size() > 0){
			addAttributeGroups(newAttributes, od);
		}

		updateObjectDependencies(od);

		log.debug("Object updated: " + od);
		od = objectDefinitionDAO.merge(od);

		if (tableNameChanged){
			renameTable(oldTable, od.getTableName(), od.hasContextAttributes());
		}

		return od;
	}

	private void updateObjectDependencies(ObjectDefinition od){
		List<ObjectAttribute> oaList = od.getObjectAttributes();
		for (ObjectAttribute oa : oaList){
			Hibernate.initialize(oa.getPredefinedAttributes());
			if (!oa.isPredefined() && oa.getPredefinedAttributes() != null && !oa.getPredefinedAttributes().isEmpty()){
				oa.getPredefinedAttributes().clear();
			}
			if (!oa.isFormulaAttribute() && oa.getFormula() != null){
				oa.setFormula(null);
			}
		}
	}

	private void renameTable(String oldTable, String newTable, boolean hasContextAttributes) throws ACException{

		log.debug("Renaming user table from " + oldTable + " to " + newTable);
		try{
			userTableStructureService.renameUserTable(oldTable, newTable, false);
			if (hasContextAttributes){
				userTableStructureService.renameUserTable(oldTable + ObjectAttribute.CONTEXT_TABLE_SUFFIX, newTable
						+ ObjectAttribute.CONTEXT_TABLE_SUFFIX, true);
			}
		} catch (Exception ex){
			log.error("Cannot rename table", ex);
			throw new ACException(TextID.MsgErrorRenameUserTable);
		}
	}

}
