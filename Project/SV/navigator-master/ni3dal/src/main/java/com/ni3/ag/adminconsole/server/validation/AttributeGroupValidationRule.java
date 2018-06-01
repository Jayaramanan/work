/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.validation;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeGroupValidationRule implements ACValidationRule{

	private static Logger log = Logger.getLogger(AttributeGroupValidationRule.class);
	private AttributeGroupDAO attributeGroupDAO;
	private List<ObjectAttribute> attributes;
	private List<ErrorEntry> errors;

	public void setAttributeGroupDAO(AttributeGroupDAO attributeGroupDAO){
		this.attributeGroupDAO = attributeGroupDAO;
	}

	public boolean performCheck(AbstractModel model){
		log.debug("perform check for attributes");
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel saMdl = (SchemaAdminModel) model;
		ObjectDefinition od = saMdl.getCurrentObjectDefinition();
		if (od == null || od.getObjectAttributes() == null)
			return true;
		attributes = od.getObjectAttributes();

		for (ObjectAttribute attribute : attributes){
			if (attribute.getId() == null){
				continue;
			}
			List<AttributeGroup> attributeGroups = attributeGroupDAO.getAttributeGroups(attribute.getId());
			addErrorMessage(attribute, attributeGroups);
		}
		return errors.isEmpty();
	}

	private void addErrorMessage(ObjectAttribute attribute, List<AttributeGroup> attributeGroups){
		if (attributeGroups == null || attributeGroups.size() <= 0)
			return;

		log.debug("fk violations found for id: " + attribute.getName());
		log.debug("form error message");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < attributeGroups.size(); i++){
			if (i != 0)
				sb.append(",");
			sb.append(attributeGroups.get(i).getGroup().getName());
		}
		String[] params = new String[2];
		params[0] = attribute.getName();
		params[1] = sb.toString();
		errors.add(new ErrorEntry(TextID.MsgAttributeCannotBeDeleted, params));
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
