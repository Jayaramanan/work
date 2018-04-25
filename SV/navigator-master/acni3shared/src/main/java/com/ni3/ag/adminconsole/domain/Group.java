/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.domain;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Group implements Serializable, Comparable<Group>{

	private static final long serialVersionUID = -6622982526432533227L;

	// constant for Criteria in DAO objects - please adjust accordingly is the field name is changed
	public static final String NAME_DB_COLUMN = "name";
	public static final String ADMINISTRATORS_GROUP_NAME = "Administrators";

	public static final String USERS_WITH_OFFLINE_CLIENT_FILTER = "offlineClientUsersFilter";

	public static final String USERS_PROPERTY = "users";

	private Integer id;
	private String name;
	private Character nodeScope;
	private Character edgeScope;
	private List<User> users;
	private List<ChartGroup> chartGroups;
	private List<Map> maps;
	private List<ObjectGroup> objectGroups;
	private List<SchemaGroup> schemaGroups;
	private List<AttributeGroup> attributeGroups;
	private List<GroupPrefilter> predefAttributeGroups;
	private List<GroupSetting> groupSettings;
	private GroupScope groupScope;

	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Character getNodeScope(){
		return nodeScope;
	}

	public void setNodeScope(Character nodeScope){
		this.nodeScope = nodeScope;
	}

	public Character getEdgeScope(){
		return edgeScope;
	}

	public void setEdgeScope(Character edgeScope){
		this.edgeScope = edgeScope;
	}

	public List<User> getUsers(){
		return users;
	}

	public void setUsers(List<User> users){
		this.users = users;
	}

	public List<ChartGroup> getChartGroups(){
		return chartGroups;
	}

	public void setChartGroups(List<ChartGroup> chartGroups){
		this.chartGroups = chartGroups;
	}

	public List<Map> getMaps(){
		return maps;
	}

	public void setMaps(List<Map> maps){
		this.maps = maps;
	}

	public List<ObjectGroup> getObjectGroups(){
		return objectGroups;
	}

	public void setObjectGroups(List<ObjectGroup> objectGroups){
		this.objectGroups = objectGroups;
	}

	public List<SchemaGroup> getSchemaGroups(){
		return schemaGroups;
	}

	public void setSchemaGroups(List<SchemaGroup> schemaGroups){
		this.schemaGroups = schemaGroups;
	}

	public List<AttributeGroup> getAttributeGroups(){
		return attributeGroups;
	}

	public void setAttributeGroups(List<AttributeGroup> attributeGroups){
		this.attributeGroups = attributeGroups;
	}

	public List<GroupPrefilter> getPredefAttributeGroups(){
		return predefAttributeGroups;
	}

	public void setPredefAttributeGroups(List<GroupPrefilter> predefAttributeGroups){
		this.predefAttributeGroups = predefAttributeGroups;
	}

	public List<GroupSetting> getGroupSettings(){
		return groupSettings;
	}

	public void setGroupSettings(List<GroupSetting> groupSettings){
		this.groupSettings = groupSettings;
	}

	public GroupScope getGroupScope(){
		return groupScope;
	}

	public void setGroupScope(GroupScope groupScope){
		this.groupScope = groupScope;
	}

	public int compareTo(Group o){
		if (this.getName() == null && (o == null || o.getName() == null)){
			return 0;
		} else if (this.getName() == null){
			return -1;
		} else if (o == null || o.getName() == null){
			return 1;
		}
		return getName().compareTo(o.getName());
	}

	@Override
	public boolean equals(java.lang.Object o){
		if (o == null)
			return false;
		if (!(o instanceof Group))
			return false;
		if (o == this)
			return true;
		Group dt = (Group) o;
		if (getId() == null || dt.getId() == null)
			return false;
		return getId().equals(dt.getId());
	}

	@Override
	public String toString(){
		return name;
	}

	public Group cloneDeep(String newName) throws CloneNotSupportedException{
		Group group = new Group();
		group.setName(newName);
		group.setNodeScope(getNodeScope());
		group.setEdgeScope(getEdgeScope());

		cloneSchemaGroupsTo(group);
		cloneObjectGroupsTo(group);
		cloneAttributeGroupsTo(group);
		cloneGroupPrefiltersTo(group);
		cloneChartGroupsTo(group);

		return group;
	}

	protected void cloneChartGroupsTo(Group group) throws CloneNotSupportedException{
		final LinkedList<ChartGroup> chartGroups = new LinkedList<ChartGroup>();
		if (getChartGroups() != null){
			for (ChartGroup chartGroup : getChartGroups()){
				chartGroups.add(chartGroup.clone(chartGroup.getChart(), group));
			}
			group.setChartGroups(chartGroups);
		}
	}

	protected void cloneAttributeGroupsTo(Group group) throws CloneNotSupportedException{
		final LinkedList<AttributeGroup> attributeGroups = new LinkedList<AttributeGroup>();
		if (getAttributeGroups() != null){
			for (AttributeGroup attributeGroup : getAttributeGroups()){
				attributeGroups.add(attributeGroup.clone(attributeGroup.getObjectAttribute(), group));
			}
			group.setAttributeGroups(attributeGroups);
		}
	}

	protected void cloneObjectGroupsTo(Group group) throws CloneNotSupportedException{
		final List<ObjectGroup> objectGroups = new LinkedList<ObjectGroup>();
		if (getObjectGroups() != null){
			for (ObjectGroup objectGroup : getObjectGroups()){
				objectGroups.add(objectGroup.clone(objectGroup.getObject(), group));
			}
			group.setObjectGroups(objectGroups);
		}
	}

	protected void cloneSchemaGroupsTo(Group group) throws CloneNotSupportedException{
		final LinkedList<SchemaGroup> schemaGroups = new LinkedList<SchemaGroup>();
		if (getSchemaGroups() != null){
			for (SchemaGroup schemaGroup : getSchemaGroups()){
				schemaGroups.add(schemaGroup.clone(schemaGroup.getSchema(), group));
			}
			group.setSchemaGroups(schemaGroups);
		}
	}

	protected void cloneGroupPrefiltersTo(Group group) throws CloneNotSupportedException{
		final List<GroupPrefilter> groupPrefilters = new LinkedList<GroupPrefilter>();
		if (getPredefAttributeGroups() != null){
			for (GroupPrefilter groupPrefilter : getPredefAttributeGroups()){
				groupPrefilters.add(groupPrefilter.clone(group));
			}
			group.setPredefAttributeGroups(groupPrefilters);
		}
	}
}
