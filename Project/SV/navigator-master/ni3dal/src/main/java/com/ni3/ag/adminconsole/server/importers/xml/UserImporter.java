/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.xml;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserImporter extends AbstractImporter{
	private final static Logger log = Logger.getLogger(UserImporter.class);
	private UserDAO userDAO;
	private GroupDAO groupDAO;

	private ACValidationRule userAdminValidationRule;

	public ACValidationRule getUserAdminValidationRule(){
		return userAdminValidationRule;
	}

	public void setUserAdminValidationRule(ACValidationRule userAdminValidationRule){
		this.userAdminValidationRule = userAdminValidationRule;
	}

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	public Object getObjectFromXML(Node node){
		NamedNodeMap attrs = node.getAttributes();
		Node userNameAttr = attrs.getNamedItem("username");
		Node passwordAttr = attrs.getNamedItem("password");
		Node firstNameAttr = attrs.getNamedItem("firstname");
		Node lastNameAttr = attrs.getNamedItem("lastname");
		Node emailAttr = attrs.getNamedItem("email");
		Node activeAttr = attrs.getNamedItem("active");
		Node sidAttr = attrs.getNamedItem("sid");
		Node hasOfflineClientAttr = attrs.getNamedItem("hasOfflineClient");
		Node etlUserAttr = attrs.getNamedItem("ETLUser");
		Node etlPasswordAttr = attrs.getNamedItem("ETLPassword");

		log.debug("importing user `" + userNameAttr.getTextContent() + "`");

		Node userList = node.getParentNode();
		Node groupNode = userList.getParentNode();
		String groupName = groupNode.getAttributes().getNamedItem("name").getTextContent();

		Group group = groupDAO.getGroupByName(groupName);
		List<Group> groupList = new ArrayList<Group>();
		groupList.add(group);

		String userName = userNameAttr.getTextContent();
		User update = userDAO.getUser(userName);
		if (update == null){
			update = new User();
			update.setUserName(userName);
		}

		update.setFirstName(firstNameAttr.getTextContent());
		update.setLastName(lastNameAttr.getTextContent());
		update.setPassword(passwordAttr.getTextContent());

		if (emailAttr != null)
			update.seteMail(emailAttr.getTextContent());
		if (activeAttr == null)
			update.setActive(true);
		else{
			String activeString = activeAttr.getTextContent();
			update.setActive("1".equals(activeString));
		}
		if (sidAttr != null)
			update.setSID(sidAttr.getTextContent());
		if (hasOfflineClientAttr != null){
			String hasOfflineClientStr = hasOfflineClientAttr.getTextContent();
			int hasOfflineClientInt = Integer.parseInt(hasOfflineClientStr);
			update.setHasOfflineClient(hasOfflineClientInt != 0);
		}

		if (etlUserAttr != null)
			update.setEtlUser(etlUserAttr.getTextContent());
		if (etlPasswordAttr != null)
			update.setEtlPassword(etlPasswordAttr.getTextContent());

		update.setGroups(groupList);
		persist(update);
		return update;
	}

	@Override
	protected void persist(Object o){
		userDAO.saveOrUpdate((User) o);
	}

	@Override
	protected boolean validateObject(Object o, Node node){
		UserAdminModel model = new UserAdminModel();

		List<User> users = new ArrayList<User>();
		users.add((User) o);
		Group g = new Group();
		g.setUsers(users);
		List<Group> groups = new ArrayList<Group>();
		groups.add(g);
		model.setGroups(groups);
		model.setCurrentGroup(g);
		Group unassigned = new Group();
		unassigned.setUsers(userDAO.getUnassignedUsers());
		model.setUnassignedGroup(unassigned);
		userAdminValidationRule.performCheck(model);
		errorContainer.addAllErrors(userAdminValidationRule.getErrorEntries());
		return errorContainer.getErrors().isEmpty();
	}

	@Override
	public String[] getMandatoryXMLAttributes(){
		return new String[] { "username", "password", "firstname", "lastname" };
	}

}
