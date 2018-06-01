/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import org.apache.log4j.Logger;

import static com.ni3.ag.adminconsole.shared.language.TextID.*;

public class SchemaAdminFieldValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	private final static Logger log = Logger.getLogger(SchemaAdminFieldValidationRule.class);

	SchemaAdminFieldValidationRule(){
	}

	private void checkAttributes(List<ObjectAttribute> attributes){
		if (attributes == null || attributes.isEmpty())
			return;
		for (ObjectAttribute attribute : attributes){
			if (attribute.getName() == null || attribute.getName().isEmpty() || attribute.getLabel() == null
					|| attribute.getLabel().isEmpty() || attribute.getDataType() == null
					|| attribute.getDataSource() == null || attribute.getDataSource().isEmpty()){
				errors.add(new ErrorEntry(MsgAttributeFieldsEmpty));
				break;
			}
		}
	}

	void checkObjectName(ObjectDefinition object, Schema schema){
		String objectName = object.getName();
		if (objectName != null && objectName.length() > ObjectDefinition.OBJECT_MAX_NAME_LENGTH){
			errors.add(new ErrorEntry(TextID.MsgObjectNameTooLong));
		}

		if (objectName != null && objectName.length() > 0 && errors.isEmpty()){
			log.debug("parent: " + schema + "; od list: " + schema.getObjectDefinitions());
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				String newName = objectName.replaceAll("[ -]", "").toUpperCase();
				String existingName = od.getName().replaceAll("[ -]", "").toUpperCase();
				if (newName.equals(existingName) && !od.equals(object)){
					errors.add(new ErrorEntry(MsgDuplicateObjects, new String[] { objectName }));
					break;
				}
			}
		}
	}

	private void checkAttributeDuplicates(List<ObjectAttribute> attributes){
		if (attributes != null && attributes.size() > 0){
			for (ObjectAttribute attribute : attributes){
				for (ObjectAttribute attribute1 : attributes){
					if (attribute != attribute1 && attribute.getName().equalsIgnoreCase(attribute1.getName())){
						errors.add(new ErrorEntry(MsgDuplicateAttributes, new String[] { attribute.getName() }));
						return;
					}
				}
			}
		}
	}

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();

		if (aModel == null)
			return true;
		SchemaAdminModel model = (SchemaAdminModel) aModel;
		ObjectDefinition od = model.getCurrentObjectDefinition();

		if (od != null){
			if (od.getSort() == null){
				errors.add(new ErrorEntry(MsgSortEmpty));
			}
			if (od.getName() == null || od.getName().length() == 0){
				errors.add(new ErrorEntry(MsgObjectNameEmpty));
			}

			checkAttributes(od.getObjectAttributes());

			checkObjectName(od, model.getCurrentObjectDefinition().getSchema());

			if (errors.isEmpty()){
				checkAttributeDuplicates(od.getObjectAttributes());
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
