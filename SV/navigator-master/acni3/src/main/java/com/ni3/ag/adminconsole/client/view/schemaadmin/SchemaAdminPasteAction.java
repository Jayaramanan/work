/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.schemaadmin;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.clipboard.PasteAction;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class SchemaAdminPasteAction extends PasteAction{

	private static final long serialVersionUID = 1L;

	private JTable table;

	public SchemaAdminPasteAction(JTable table){
		super(table);
		this.table = table;
	}

	public void pasteColumn(AbstractTableModel model, List<Object> values, Class<?> columnClass, int column, int rowFrom,
	        int rowTo, DatabaseInstance dbInstance){
        List<Object> valuesToUse = values;
		if (table.getColumnName(column).equals(Translation.get(TextID.Name))){
            valuesToUse = new ArrayList<Object>();
            for (Object value : values) {
                String val = (String) value;
                valuesToUse.add(val.replaceAll("[^A-Za-z0-9_]", ""));
            }
		}
		super.pasteColumn(model, valuesToUse, columnClass, column, rowFrom, rowTo, dbInstance);
	}
}
