/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.exporters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.domain.ExportData;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.BasicErrorContainer;

public class XLSDataExporter implements Exporter{

	private static JFileChooser fileChooser = new JFileChooser();
	static{
		fileChooser.setDialogTitle(Translation.get(TextID.SelectFolderForExportFile));
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File f){
				return true;
			}

			@Override
			public String getDescription(){
				return Translation.get(TextID.MicrosoftExcelFiles);
			}
		});
	}

	private SchemaAdminController controller;
	private ACValidationRule selectionRule = null;

	public XLSDataExporter(SchemaAdminController schemaAdminController){
		this.controller = schemaAdminController;
		ACSpringFactory factory = ACSpringFactory.getInstance();
		selectionRule = (ACValidationRule) factory.getBean("schemaSelectionValidationRule");
	}

	@Override
	public boolean doExport(){
		SchemaAdminModel model = controller.getModel();
		if (!selectionRule.performCheck(controller.getModel())){
			controller.getView().renderErrors(selectionRule.getErrorEntries());
			return false;
		}
		Schema schema = model.getCurrentSchema();
		if (schema == null){
			schema = model.getCurrentObjectDefinition().getSchema();
		}

		controller.getView().clearErrors();
		File file = requestFolderToSave(schema);
		if (file == null)
			return true;
		SchemaAdminService service = (SchemaAdminService) ACSpringFactory.getInstance().getBean("schemaAdminService");
		try{
			if (!file.getName().endsWith(".xls"))
				file = new File(file.getAbsolutePath() + ".xls");
			file.createNewFile();
			ExportData exportData = service.exportUserDataToXLS(schema.getName(), SessionData.getInstance().getUser());
			FileOutputStream fs = new FileOutputStream(file);
			fs.write(exportData.getFileData());
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

	private File requestFolderToSave(Schema sch){
		fileChooser.setSelectedFile(new File(sch.getName() + ".xls"));
		int result = fileChooser.showDialog(controller.getView(), Translation.get(TextID.Save));
		File f = null;
		if (result == JFileChooser.APPROVE_OPTION){
			f = fileChooser.getSelectedFile();
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
