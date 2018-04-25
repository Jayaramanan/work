/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */

package com.ni3.ag.adminconsole.validation.rules;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UniqueObjectSortValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	@Override
	public boolean performCheck(AbstractModel amodel){
		SchemaAdminModel model = (SchemaAdminModel) amodel;
		errors = new ArrayList<ErrorEntry>();
		ObjectDefinition object = model.getCurrentObjectDefinition();
		Schema schema = null;
		if (object != null){
			schema = object.getSchema();
		} else if (model.getCurrentSchema() != null){
			schema = model.getCurrentSchema();
		}
		if(schema == null)
			return true; //oops
		Set<Integer> sorts = new HashSet<Integer>();
		for(ObjectDefinition od : schema.getObjectDefinitions()){
			if(sorts.contains(od.getSort())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateSortValue, new String[]{od.getSort().toString()}));
				return false;
			}else
				sorts.add(od.getSort());
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
