/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserTableNameValidationRule implements ACValidationRule{

	private static final String CIS_EDGES_TABLE_NAME = "CIS_EDGES";
	private static final String CIS_NODES_TABLE_NAME = "CIS_NODES";

	private List<ErrorEntry> errors = new ArrayList<ErrorEntry>();

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	@Override
	public boolean performCheck(AbstractModel model){

		SchemaAdminModel saModel = (SchemaAdminModel) model;
		ObjectDefinition od = saModel.getCurrentObjectDefinition();

		od.setTableName(tableNameForObject(od));
		if (od.getObjectAttributes() != null){
			for (ObjectAttribute ao : od.getObjectAttributes()){
				if (isEdge(od) && ObjectAttribute.isFixedEdgeAttribute(ao, true))
					ao.setInTable(CIS_EDGES_TABLE_NAME);
				else if (isNode(od) && ObjectAttribute.isFixedNodeAttribute(ao, true))
					ao.setInTable(CIS_NODES_TABLE_NAME);
				else if (ao.isInContext())
					ao.setInTable(od.getTableName() + ObjectAttribute.CONTEXT_TABLE_SUFFIX);
				else
					ao.setInTable(od.getTableName());
			}
		}

		return true;
	}

	private String tableNameForObject(ObjectDefinition childClone){
		final String TABLE_NAME_PREFIX = "USR_";
		if (isEdge(childClone) && hasOnlyFixed(childClone))
			return CIS_EDGES_TABLE_NAME;
		else{
			String schemaName = childClone.getSchema().getName();
			schemaName = schemaName.trim().replaceAll("[ -]", "").toUpperCase();
			String objectName = childClone.getName().trim().replaceAll("[ -]", "").toUpperCase();
			String tableName = TABLE_NAME_PREFIX + schemaName + "_" + objectName;
			return tableName;
		}
	}

	private boolean isEdge(ObjectDefinition obj){
		return obj.isEdge();
	}

	private boolean isNode(ObjectDefinition obj){
		return obj.isNode();
	}

	private boolean hasOnlyFixed(ObjectDefinition childClone){
		if (childClone.getObjectAttributes() != null)
			for (ObjectAttribute oa : childClone.getObjectAttributes()){
				if (!ObjectAttribute.isFixedEdgeAttribute(oa, true))
					return false;
			}
		return true;
	}

}
