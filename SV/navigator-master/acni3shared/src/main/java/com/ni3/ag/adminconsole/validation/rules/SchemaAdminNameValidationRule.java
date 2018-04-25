/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class SchemaAdminNameValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		if (model == null)
			return true;
		SchemaAdminModel sModel = ((SchemaAdminModel) model);
		Schema schema = sModel.getCurrentSchema();
		ObjectDefinition object = sModel.getCurrentObjectDefinition();
		if (schema == null && object == null)
			return true;
		if (schema != null && (schema.getName() == null || schema.getName().isEmpty())){
			errors.add(new ErrorEntry(TextID.MsgEmptySchemaName));
			return false;
		}
		if (schema != null && schema.getName().length() > Schema.SCHEMA_MAX_NAME_LENGTH)
			errors.add(new ErrorEntry(TextID.MsgEnteredNameTooLong));
		else{
			List<Schema> schemas = ((SchemaAdminModel) model).getSchemaList();
			for (Schema sch : schemas){
				String newName = schema.getName().replaceAll("[ -]", "").toUpperCase();
				String existingName = sch.getName().replaceAll("[ -]", "").toUpperCase();
				if (sch != schema && newName.equals(existingName)){
					errors.add(new ErrorEntry(TextID.MsgDuplicateSchema, new String[] { schema.getName() }));
					break;
				}
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
