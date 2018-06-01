/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;

public class ObjectAttributeTableModelListener implements TableModelListener{

	private static int PREDEFINED_COLUMN_INDEX = 6;
	private SchemaAdminController controller;

	public ObjectAttributeTableModelListener(SchemaAdminController controller){
		this.controller = controller;
	}

	public void tableChanged(TableModelEvent e){
		if (e.getType() != TableModelEvent.UPDATE)
			return;
		int col = e.getColumn();
		if (col != PREDEFINED_COLUMN_INDEX)
			return;
		SchemaAdminView view = controller.getView();
		view.getRightPanel().getAttributesTable().repaint();
	}

}
