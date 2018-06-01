/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class AttributeNameValidationRule implements ACValidationRule{
	private List<ErrorEntry> errors;
	public static final String[] restrictedNames = { "ACCESS", "ADD", "ALL", "ALTER", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY",
	        "AS", "ASC", "ASYMMETRIC", "AUDIT", "AUTHORIZATION", "BETWEEN", "BINARY", "BOTH", "BY", "CASE", "CAST", "CHAR",
	        "CHECK", "CLUSTER", "COLLATE", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CONSTRAINT", "CREATE", "CROSS",
	        "CURRENT", "CURRENT_CATALOG", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_TIME",
	        "CURRENT_TIMESTAMP", "CURRENT_USER", "DATE", "DECIMAL", "DEFAULT", "DEFERRABLE", "DELETE", "DESC", "DISTINCT",
	        "DO", "DROP", "ELSE", "END", "END-EXEC", "EXCEPT", "EXCLUSIVE", "EXISTS", "FALSE", "FETCH", "FILE", "FLOAT",
	        "FOR", "FOREIGN", "FREEZE", "FROM", "FULL", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "ILIKE", "IMMEDIATE",
	        "IN", "INCREMENT", "INDEX", "INITIAL", "INITIALLY", "INNER", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS",
	        "ISNULL", "JOIN", "LEADING", "LEFT", "LEVEL", "LIKE", "LIMIT", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG",
	        "MAXEXTENTS", "MINUS", "MLSLABEL", "MODE", "MODIFY", "NATURAL", "NEW", "NOAUDIT", "NOCOMPRESS", "NOT",
	        "NOTNULL", "NOWAIT", "NULL", "NUMBER", "OF", "OFF", "OFFLINE", "OFFSET", "OLD", "ON", "ONLINE", "ONLY",
	        "OPTION", "OR", "ORDER", "OUTER", "OVER", "OVERLAPS", "PCTFREE", "PLACING", "PRIMARY", "PRIOR", "PRIVILEGES",
	        "PUBLIC", "RAW", "REFERENCES", "RENAME", "RESOURCE", "RETURNING", "REVOKE", "RIGHT", "ROW", "ROWID", "ROWNUM",
	        "ROWS", "SELECT", "SESSION", "SESSION_USER", "SET", "SHARE", "SIMILAR", "SIZE", "SMALLINT", "SOME", "START",
	        "SUCCESSFUL", "SYMMETRIC", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRAILING", "TRIGGER", "TRUE", "UID",
	        "UNION", "UNIQUE", "UPDATE", "USER", "USING", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VARIADIC",
	        "VERBOSE", "VIEW", "WHEN", "WHENEVER", "WHERE", "WINDOW", "WITH" };

	@Override
	public boolean performCheck(AbstractModel amodel){
		errors = new ArrayList<ErrorEntry>();
		SchemaAdminModel model = (SchemaAdminModel) amodel;
		ObjectDefinition od = model.getCurrentObjectDefinition();
		if (od == null)
			return true;
		for (ObjectAttribute oa : od.getObjectAttributes()){
			for (String s : restrictedNames){
				if (s.equalsIgnoreCase(oa.getName())){
					errors.add(new ErrorEntry(TextID.MsgRestrictedAttributeName, new String[] { oa.getName() }));
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
