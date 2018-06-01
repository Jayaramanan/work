/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.importers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class XMLSchemaImporter implements Importer{
	private final static Logger log = Logger.getLogger(XMLSchemaImporter.class);
	SchemaAdminController controller;

	public XMLSchemaImporter(SchemaAdminController controller){
		this.controller = controller;
	}

	public boolean doImport(){
		SchemaAdminView view = controller.getView();
		view.getLeftPanel().showXMLChooser();
		File xml = view.getLeftPanel().getSelectedXML();

		if (xml == null){
			return false;
		}

		SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
		boolean ok = true;
		try{
			InputStream is = new FileInputStream(xml);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int available = is.available();
			while (available > 0){
				byte[] buf = new byte[available];
				is.read(buf);
				baos.write(buf);
				available = is.available();
			}
			baos.flush();
			baos.close();
			is.close();

			String xmlContents = new String(baos.toByteArray(), "UTF-8");
			schemaAdminService.importSchemaFromXML(xmlContents);
		} catch (IOException ex){
			log.debug(ex.getMessage(), ex);
			ok = false;
		} catch (ACException ex){
			log.debug(ex.getMessage(), ex);
			view.renderErrors(new ServerErrorContainerWrapper(ex));
			ok = false;
		}

		if (ok){
			schemaAdminService.setAllInvalidationRequired(true, DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
			MainPanel2.setAllInvalidationNeeded(DataGroup.Attributes, DataGroup.Users, DataGroup.Schema);
		}
		return ok;
	}
}
