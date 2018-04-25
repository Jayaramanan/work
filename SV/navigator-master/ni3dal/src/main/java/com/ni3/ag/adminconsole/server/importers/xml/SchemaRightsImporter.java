/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;

public class SchemaRightsImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(SchemaRightsImporter.class);
	private AbstractImporter objectRightsImporter;
	private GroupDAO groupDAO;

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new HashMap<String, AbstractImporter>();
		map.put("objectRight", objectRightsImporter);
		return map;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setObjectRightsImporter(AbstractImporter objectRightsImporter){
		this.objectRightsImporter = objectRightsImporter;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node canReadAttr = attrs.getNamedItem("canRead");
		Schema schema = getCurrentSchema();
		Group group = (Group) parent;
		log.debug("importing schema rights for schema `" + schema.getName() + "` and group `" + group.getName() + "`");

		SchemaGroup update = getSchemaGroup(schema, group);
		if (update == null){
			update = new SchemaGroup(schema, group);
			if (group.getSchemaGroups() == null)
				group.setSchemaGroups(new ArrayList<SchemaGroup>());
			group.getSchemaGroups().add(update);
		}

		boolean canRead = false;
		if (canReadAttr != null)
			canRead = !"0".equals(canReadAttr.getTextContent());
		update.setCanRead(canRead);
		return update;
	}

	private SchemaGroup getSchemaGroup(Schema schema, Group group){
		if (group.getSchemaGroups() == null)
			return null;
		for (SchemaGroup sg : group.getSchemaGroups()){
			if (sg.getSchema().equals(schema))
				return sg;
		}
		return null;
	}

	@Override
	protected void persist(Object o){
		groupDAO.saveOrUpdate((Group) parent);
		setCurrentGroup((Group) parent);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return true;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] {};
	}

}
