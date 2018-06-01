/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl.diag;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.service.DiagnosticTask;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACFixTaskException;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskStatus;

public class PredefinedAttributeCheckTask implements DiagnosticTask{
	private static final String DESCRIPTION = "Checking validity of Predefined attributes";
	private static final String TOOLTIP_NO_PREDEFINEDS = "Object attribute has no predefined attributes: ";
	private static final String TOOLTIP_NO_VISIBLE_PREDEFINEDS = "Object attribute has no visible predefined attributes: ";
	private final static String ACTION_ADD_PREDEFINED_PRIVILEGES = "Go to Users tab and set `Can read` to at least one predefined attribute of the attribute `%1` for the group `%2`";
	private final static String ACTION_ADD_PREDEFINED_VALUES = "Go to Attributes -> Attribute values tab and add values to the attribute `%1` of the object `%2` ";

	private GroupDAO groupDAO;

	private SchemaDAO schemaDAO;

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	public DiagnoseTaskResult makeDiagnose(Schema sch){
		List<Group> groups = groupDAO.getGroups();
		for (Group group : groups){
			List<ObjectAttribute> visibleAttributes = getVisibleAttributesWithValueList(group, sch);
			for (ObjectAttribute oa : visibleAttributes){
				String oaString = "object `" + oa.getObjectDefinition().getName() + "` attribute `" + oa.getLabel() + "`";
				if (!hasPredefinedAttributes(oa)){
					final String actionDescription = ACTION_ADD_PREDEFINED_VALUES.replaceAll("%1",
					        oa.getObjectDefinition().getName()).replaceAll("%2", oa.getLabel());

					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Error,
					        TOOLTIP_NO_PREDEFINEDS + oaString, actionDescription);
				}

				if (!hasVisiblePredefinedValues(oa, group)){
					final String actionDescription = ACTION_ADD_PREDEFINED_PRIVILEGES.replaceAll("%1",
					        oa.getObjectDefinition().getName()).replaceAll("%2", group.getName());
					return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Warning,
					        TOOLTIP_NO_VISIBLE_PREDEFINEDS + oaString, actionDescription);
				}
			}
		}
		return new DiagnoseTaskResult(getClass().getName(), DESCRIPTION, false, DiagnoseTaskStatus.Ok, null, null);
	}

	private boolean hasPredefinedAttributes(ObjectAttribute oa){
		List<PredefinedAttribute> predefs = oa.getPredefinedAttributes();
		return predefs != null && !predefs.isEmpty();
	}

	private boolean hasVisiblePredefinedValues(ObjectAttribute oa, Group group){
		List<PredefinedAttribute> predefs = oa.getPredefinedAttributes();
		for (PredefinedAttribute pa : predefs){
			if (isAvalibleForGroup(pa, group))
				return true;
		}
		return false;
	}

	private boolean isAvalibleForGroup(PredefinedAttribute pa, Group group){
		List<GroupPrefilter> predefinedAttributeGroups = group.getPredefAttributeGroups();
		for (GroupPrefilter gp : predefinedAttributeGroups){
			if (gp.getPredefinedAttribute().equals(pa))
				return false;
		}
		return true;
	}

	private List<ObjectAttribute> getAvailableAttributesPredefined(List<ObjectAttribute> allAttributes, Group group){
		List<ObjectAttribute> result = new ArrayList<ObjectAttribute>();

		List<AttributeGroup> ags = group.getAttributeGroups();
		for (ObjectAttribute attr : allAttributes){
			if (!attr.isPredefined())
				continue;
			for (AttributeGroup ag : ags){
				if (ag.getObjectAttribute().equals(attr) && ag.isCanRead()){
					result.add(attr);
					break;
				}
			}
		}
		return result;
	}

	private List<ObjectAttribute> getVisibleAttributesWithValueList(Group group, Schema sch){
		List<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		sch = schemaDAO.getSchema(sch.getId());
		Hibernate.initialize(sch.getObjectDefinitions());
		for (ObjectDefinition od : sch.getObjectDefinitions()){
			attrs.addAll(getAvailableAttributesPredefined(od.getObjectAttributes(), group));
		}
		return attrs;
	}

	@Override
	public DiagnoseTaskResult makeFix(DiagnoseTaskResult taskResult) throws ACFixTaskException, ACException{
		throw new ACFixTaskException("ACFixTaskException", "Non fixable");
	}

	@Override
	public String getTaskDescription(){
		return DESCRIPTION;
	}

}
