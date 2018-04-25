/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.importers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.client.view.schemaadmin.XLSImportFileChooser;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class XLSDataImporter implements Importer{
	private final static Logger log = Logger.getLogger(XLSDataImporter.class);
	private static XLSImportFileChooser chooser = null;
	static{
		chooser = new XLSImportFileChooser();
	}

	private SchemaAdminController controller = null;
	private ACValidationRule selectionRule = null;

	public XLSDataImporter(SchemaAdminController controller){

		this.controller = controller;
		ACSpringFactory factory = ACSpringFactory.getInstance();
		selectionRule = (ACValidationRule) factory.getBean("schemaSelectionValidationRule");
	}

	@Override
	public boolean doImport(){
		SchemaAdminView view = controller.getView();
		SchemaAdminModel model = controller.getModel();

		if (!selectionRule.performCheck(model)){
			view.renderErrors(selectionRule.getErrorEntries());
			return false;
		}
		Schema schema = model.getCurrentSchema();
		if (schema == null){
			schema = model.getCurrentObjectDefinition().getSchema();
		}

		int result = chooser.showDialog(controller.getView(), Translation.get(TextID.Open));
		File file = null;
		if (result == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
		}
		if (file == null){
			return false;
		}

		boolean ok = true;
		try{
			InputStream is = new FileInputStream(file);
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

			SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
			schemaAdminService.importUserDataFromXLS(baos.toByteArray(), schema.getId(), SessionData.getInstance()
					.getUserId(), chooser.isRecalculateFormulas());

			schemaAdminService.setInvalidationRequired(DataGroup.Schema, true);
			MainPanel2.setInvalidationNeeded(TextID.Schemas, true);

		} catch (IOException ex){
			log.debug(ex.getMessage(), ex);
			ok = false;
		} catch (ACException ex){
			log.debug(ex.getMessage(), ex);
			view.renderErrors(new ServerErrorContainerWrapper(ex));
			ok = false;
		}
		return ok;
	}

}
