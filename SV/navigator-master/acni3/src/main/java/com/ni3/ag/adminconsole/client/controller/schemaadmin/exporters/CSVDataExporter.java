/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.schemaadmin.CSVFileChooser;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.BasicErrorContainer;

public class CSVDataExporter implements Exporter{

	private static CSVFileChooser chooser = null;
	static{
		chooser = new CSVFileChooser(false);
	}

	private SchemaAdminController controller;
	private ACValidationRule selectionRule = null;

	public CSVDataExporter(SchemaAdminController schemaAdminController){
		this.controller = schemaAdminController;
		ACSpringFactory factory = ACSpringFactory.getInstance();
		selectionRule = (ACValidationRule) factory.getBean("schemaSelectionValidationRule");
	}

	private boolean exportObject(ObjectDefinition od){
		File file = requestFolderToSave(od);
		if (file == null)
			return true;
		SchemaAdminService service = (SchemaAdminService) ACSpringFactory.getInstance().getBean("schemaAdminService");
		try{
			if (!file.getName().toLowerCase().endsWith(".csv"))
				file = new File(file.getAbsolutePath() + ".csv");
			file.createNewFile();
			String lineSeparator = chooser.getLineSeparator();
			String columnSeparator = chooser.getColumnSeparator();
			byte[] exportData = service.exportUserDataToCSV(od, SessionData.getInstance().getUser(), columnSeparator,
			        lineSeparator);
			FileOutputStream fs = new FileOutputStream(file);
			fs.write(exportData);
			fs.close();
		} catch (ACException ex){
			controller.getView().renderErrors(ex);
			return false;
		} catch (IOException ex){
			controller.getView().renderErrors(new BasicErrorContainer(new ErrorEntry(TextID.MsgFailedToCreateOrSaveFile)));
			return false;
		}
		return true;
	}

	@Override
	public boolean doExport(){
		SchemaAdminModel model = controller.getModel();
		if (!selectionRule.performCheck(controller.getModel())){
			controller.getView().renderErrors(selectionRule.getErrorEntries());
			return false;
		}
		controller.getView().clearErrors();

		boolean exportOk = true;
		Schema schema = model.getCurrentSchema();
		ObjectDefinition currentObject = model.getCurrentObjectDefinition();

		if (currentObject != null){
			exportOk = exportObject(currentObject);
		} else if (schema != null){
			if (schema.getObjectDefinitions().isEmpty())
				return true;
			for (ObjectDefinition od : schema.getObjectDefinitions())
				exportOk = exportObject(od);
		}
		return exportOk;
	}

	private File requestFolderToSave(ObjectDefinition od){
		chooser.setSelectedFile(new File(od.getName() + ".csv"));
		int result = chooser.showDialog(controller.getView(), Translation.get(TextID.Save));
		File f = null;
		if (result == CSVFileChooser.APPROVE_OPTION){
			f = chooser.getSelectedFile();
			if (f.exists()){
				int response = ACOptionPane.showConfirmDialog(null, Translation.get(TextID.QuestionOverwriteExistingFile),
				        Translation.get(TextID.ConfirmOverwrite));
				if (response == ACOptionPane.NO_OPTION)
					return null;
			}
		}
		return f;
	}
}
