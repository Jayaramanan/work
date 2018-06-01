/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters.CSVDataExporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters.Exporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters.XLSDataExporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters.XLSSchemaExporter;
import com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters.XMLSchemaExporter;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ExportButtonListener extends ProgressActionListener{

	public ExportButtonListener(SchemaAdminController schemaAdminController){
		super(schemaAdminController);
	}

	@Override
	public void performAction(ActionEvent e){
		SchemaAdminController controller = (SchemaAdminController) getController();
		String action = e.getActionCommand();
		Exporter exporter = null;
		if (TextID.XLSSchemaExport.toString().equals(action)){
			exporter = new XLSSchemaExporter(controller);
		} else if (TextID.XLSDataExport.toString().equals(action)){
			exporter = new XLSDataExporter(controller);
		} else if (TextID.CSVDataExport.toString().equals(action)){
			exporter = new CSVDataExporter(controller);
		} else if (TextID.XMLSchemaExport.toString().equals(action)){
			exporter = new XMLSchemaExporter(controller);
		}

		exporter.doExport();
	}

}
