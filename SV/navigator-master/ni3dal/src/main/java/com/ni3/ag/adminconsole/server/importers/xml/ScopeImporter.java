/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupScope;

public class ScopeImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(ScopeImporter.class);

	@Override
	public Object getObjectFromXML(Node node){
		Group g = (Group) parent;
		String nodename = node.getNodeName();
		log.debug("importing scope `" + nodename + "`for group `" + g.getName() + "`");

		NamedNodeMap attrs = node.getAttributes();
		Node isUsedNode = attrs.getNamedItem("isUsed");
		boolean isUsed = isUsedNode != null && !"0".equals(isUsedNode.getTextContent());

		GroupScope gs = g.getGroupScope();
		if (gs == null){
			gs = new GroupScope();
			gs.setGroup(g.getId());
			g.setGroupScope(gs);
		}
		if ("nodeScope".equals(nodename)){
			g.setNodeScope(isUsed ? 'S' : 'A');
			gs.setNodeScope(node.getTextContent());
		} else if ("edgeScope".equals(nodename)){
			g.setEdgeScope(isUsed ? 'S' : 'A');
			gs.setEdgeScope(node.getTextContent());
		}
		return g;
	}

	@Override
	protected void persist(Object o){
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return true;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "isUsed" };
	}

}
