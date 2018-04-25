/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorContainer;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class OwnerImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(OwnerImporter.class);
	private UserDAO userDAO;

	private User owner;

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public ErrorContainer importData(NodeList nodeList){
		super.importData(nodeList);
		if (nodeList.getLength() == 0)
			errorContainer.addError(TextID.MsgNoOwner);
		return errorContainer;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node nameNode = attrs.getNamedItem("name");
		String userName = nameNode.getTextContent();

		log.debug("importing owner `" + userName + "`");
		User user = userDAO.getUser(userName);
		if (user == null){
			user = new User();
			user.setUserName(userName);
		}
		return user;
	}

	@Override
	protected void persist(Object o){
		owner = (User) o;
	}

	public User getOwner(){
		return owner;
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		return o != null;
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "name" };
	}

}
