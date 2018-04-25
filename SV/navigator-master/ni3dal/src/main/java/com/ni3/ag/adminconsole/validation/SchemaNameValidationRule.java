/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;

public class SchemaNameValidationRule implements ACValidationRule{

	private SchemaDAO schemaDAO;
	private List<ErrorEntry> errors;

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		Schema schema = ((SchemaAdminModel) model).getCurrentSchema();
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema sch : schemas){
			String newName = schema.getName().replaceAll("[ -]", "").toUpperCase();
			String existingName = sch.getName().replaceAll("[ -]", "").toUpperCase();
			if (sch != schema && newName.equals(existingName)){
				errors.add(new ErrorEntry(TextID.MsgDuplicateSchema, new String[] { schema.getName() }));
				break;
			}
		}

		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
