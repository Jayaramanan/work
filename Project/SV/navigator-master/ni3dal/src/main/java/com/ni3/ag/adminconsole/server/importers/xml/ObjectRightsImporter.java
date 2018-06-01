/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectRightsImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(ObjectRightsImporter.class);
	private AbstractImporter attributeRightsImporter;

	private GroupDAO groupDAO;

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new HashMap<String, AbstractImporter>();
		map.put("attributeRight", attributeRightsImporter);
		return map;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public void setAttributeRightsImporter(AbstractImporter attributeRightsImporter){
		this.attributeRightsImporter = attributeRightsImporter;
	}

	public AbstractImporter getAttributeRightsImporter(){
		return attributeRightsImporter;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node objectAttr = attrs.getNamedItem("object");
		log.debug("importing object rights for object `" + objectAttr.getTextContent() + "`");
		Node canReadAttr = attrs.getNamedItem("canRead");
		Node canCreateAttr = attrs.getNamedItem("canCreate");
		Node canUpdateAttr = attrs.getNamedItem("canUpdate");
		Node canDeleteAttr = attrs.getNamedItem("canDelete");

		Schema schema = getCurrentSchema();
		Group group = getCurrentGroup();
		try{
			ObjectDefinition od = getObject(schema, objectAttr);
			ObjectGroup update = getObjectGroup(od, group);
			if (update == null){
				update = new ObjectGroup(od, group);
				if (od.getObjectGroups() == null)
					od.setObjectGroups(new ArrayList<ObjectGroup>());
				od.getObjectGroups().add(update);
			}

			boolean canCreate = false;
			boolean canDelete = false;
			boolean canRead = false;
			boolean canUpdate = false;
			if (canCreateAttr != null)
				canCreate = !"0".equals(canCreateAttr.getTextContent());
			if (canDeleteAttr != null)
				canDelete = !"0".equals(canDeleteAttr.getTextContent());
			if (canReadAttr != null)
				canRead = !"0".equals(canReadAttr.getTextContent());
			if (canUpdateAttr != null)
				canUpdate = !"0".equals(canUpdateAttr.getTextContent());
			update.setCanCreate(canCreate);
			update.setCanDelete(canDelete);
			update.setCanRead(canRead);
			update.setCanUpdate(canUpdate);
			return update;
		} catch (ACException e){
			errorContainer.addAllErrors(e.getErrors());
		}
		return null;
	}

	private ObjectGroup getObjectGroup(Object object, Group group){
		if (group.getObjectGroups() == null)
			return null;
		for (ObjectGroup sg : group.getObjectGroups()){
			if (sg.getObject().equals(object))
				return sg;
		}
		return null;
	}

	private ObjectDefinition getObject(Schema schema, Node objectNode) throws ACException{
		ObjectDefinition od = getObjectDefinitionByName(schema, objectNode.getTextContent());
		if (od == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
			        "no object `" + objectNode.getTextContent() + "` found for object right" });
		return od;
	}

	@Override
	protected void persist(Object o){
		groupDAO.saveOrUpdate(getCurrentGroup());
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return true;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "object" };
	}

}
