/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectDefinitionImporter extends AbstractImporter{

	private ObjectDefinitionDAO objectDefinitionDAO;

	// =============--------------
	// embedded importers
	// ===========------------
	private AbstractImporter objectAttributeImporter;
	private AbstractImporter predefinedAttributeParentImporter;

	// =============--------------
	// validation rules
	// ===========------------
	private ACValidationRule schemaAdminFieldValidationRule;
	private ACValidationRule userTableNameRule;

	private final static Logger log = Logger.getLogger(ObjectDefinitionImporter.class);

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new LinkedHashMap<String, AbstractImporter>();
		map.put("objectAttribute", objectAttributeImporter);
		map.put("_objectAttribute", predefinedAttributeParentImporter); // same objectAttribute tag is used
		return map;
	}

	public void setUserTableNameRule(ACValidationRule userTableNameRule){
		this.userTableNameRule = userTableNameRule;
	}

	public ACValidationRule getSchemaAdminFieldValidationRule(){
		return schemaAdminFieldValidationRule;
	}

	public void setSchemaAdminFieldValidationRule(ACValidationRule schemaAdminFieldValidationRule){
		this.schemaAdminFieldValidationRule = schemaAdminFieldValidationRule;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public AbstractImporter getObjectAttributeImporter(){
		return objectAttributeImporter;
	}

	public void setObjectAttributeImporter(AbstractImporter objectAttributeImporter){
		this.objectAttributeImporter = objectAttributeImporter;
	}

	public void setPredefinedAttributeParentImporter(AbstractImporter predefinedAttributeParentImporter){
		this.predefinedAttributeParentImporter = predefinedAttributeParentImporter;
	}

	@Override
	public Object getObjectFromXML(Node node){

		NamedNodeMap attrs = node.getAttributes();

		Node definitionsList = node.getParentNode();
		Node schemaRoot = definitionsList.getParentNode();
		String schemaName = schemaRoot.getAttributes().getNamedItem("name").getNodeValue();
		Schema schema = (Schema) parent;

		String objectName = attrs.getNamedItem("name").getTextContent();
		String typeName = attrs.getNamedItem("type").getTextContent();

		ObjectType type = ObjectType.fromLabel(typeName);

		ObjectDefinition update = getObjectDefinitionByName(schema, objectName);
		if (update == null){
			update = new ObjectDefinition();
			update.setSchema(schema);
			update.setCreatedBy(schema.getCreatedBy());
			update.setCreationDate(new Date());
			update.setName(objectName);
			if (type == null)
				errorContainer.addError(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"Invalid object type `" + typeName + "`" });
			update.setObjectType(type);
			if (schema.getObjectDefinitions() == null)
				schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());
			schema.getObjectDefinitions().add(update);
		}

		log.debug("od name: " + objectName + "; parent name: " + schemaName + "; schema: " + schema);

		Node descriptionAttr = attrs.getNamedItem("description");
		if (descriptionAttr != null)
			update.setDescription(descriptionAttr.getTextContent());

		Node sortAttr = attrs.getNamedItem("sort");
		if (sortAttr != null)
			update.setSort(Integer.valueOf(sortAttr.getTextContent()));

		return update;
	}

	@Override
	protected void persist(Object o){
		ObjectDefinition od = (ObjectDefinition) o;
		od = objectDefinitionDAO.saveOrUpdate((ObjectDefinition) o);
		od.setSort(od.getId());
		objectDefinitionDAO.merge(od);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		ObjectDefinition od = (ObjectDefinition) o;
		SchemaAdminModel model = new SchemaAdminModel();
		model.setCurrentObjectDefinition(od);
		Schema parent = od.getSchema();
		if (parent.getObjectDefinitions() == null){
			parent.setObjectDefinitions(new ArrayList<ObjectDefinition>());
		}

		userTableNameRule.performCheck(model);

		return true;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name", "type" };
	}

}
