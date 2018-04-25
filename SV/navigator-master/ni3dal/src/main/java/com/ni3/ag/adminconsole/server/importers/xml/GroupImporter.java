/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;

public class GroupImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(GroupImporter.class);
	private GroupDAO groupDAO;

	private AbstractImporter userImporter;
	private AbstractImporter schemaRightsImporter;
	private AbstractImporter scopeImporter;

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setScopeImporter(AbstractImporter scopeImporter){
		this.scopeImporter = scopeImporter;
	}

	public void setUserImporter(AbstractImporter userImporter){
		this.userImporter = userImporter;
	}

	public AbstractImporter getUserImporter(){
		return userImporter;
	}

	public void setSchemaRightsImporter(AbstractImporter schemaRightsImporter){
		this.schemaRightsImporter = schemaRightsImporter;
	}

	@Override
	public Object getObjectFromXML(Node node){
		String groupName = node.getAttributes().getNamedItem("name").getNodeValue();
		Group g = groupDAO.getGroupByName(groupName);
		log.debug("importing group `" + groupName + "`");
		if (g == null){
			g = new Group();
			g.setName(groupName);
			g.setEdgeScope('A');
			g.setNodeScope('A');
		}
		return g;
	}

	@Override
	protected void persist(Object o){
		groupDAO.saveOrUpdate((Group) o);
		setCurrentGroup((Group) o);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return errorContainer.getErrors().isEmpty();
	}

	protected Map<String, AbstractImporter> getTagToEmbeddedImporterMapping(){
		Map<String, AbstractImporter> map = new HashMap<String, AbstractImporter>();
		map.put("user", userImporter);
		map.put("schemaRight", schemaRightsImporter);
		map.put("nodeScope", scopeImporter);
		map.put("edgeScope", scopeImporter);
		return map;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name" };
	}

}
