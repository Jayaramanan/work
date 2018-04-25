/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.schemaadmin.importers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.schemaadmin.SchemaAdminController;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.schemaadmin.CSVFileChooser;
import com.ni3.ag.adminconsole.client.view.schemaadmin.SchemaAdminView;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class CSVDataImporter implements Importer{
	private final static Logger log = Logger.getLogger(XLSDataImporter.class);
	private static CSVFileChooser chooser = null;
	static{
		chooser = new CSVFileChooser(true);
	}

	private SchemaAdminController controller = null;
	private ACValidationRule selectionRule = null;

	public CSVDataImporter(SchemaAdminController controller){
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
		String columnSeparator = null;
		boolean recalculateFormulas = false;
		if (result == JFileChooser.APPROVE_OPTION){
			file = chooser.getSelectedFile();
			columnSeparator = chooser.getColumnSeparator();
			recalculateFormulas = chooser.isRecalculateFormulas();
		}
		if (file == null){
			return false;
		}

		boolean ok = true;
		try{
			List<String> lines = readLines(file);
			if (lines.size() > 2){
				SchemaAdminService schemaAdminService = ACSpringFactory.getInstance().getSchemaAdminService();
				Integer userId = SessionData.getInstance().getUserId();
				schemaAdminService.importUserDataFromCSV(lines, schema.getId(), userId, file.getName(), columnSeparator,
						recalculateFormulas);

				schemaAdminService.setInvalidationRequired(DataGroup.Schema, true);
				MainPanel2.setInvalidationNeeded(TextID.Schemas, true);
			} else{
				view.renderErrors(Arrays.asList(new ErrorEntry(TextID.MsgFileEmptyOrInvalid)));
			}
		} catch (ACException ex){
			log.debug(ex.getMessage(), ex);
			view.renderErrors(new ServerErrorContainerWrapper(ex));
			ok = false;
		}
		return ok;
	}

	private List<String> readLines(File file){
		List<String> lines = new ArrayList<String>();
		try{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String strLine;
			while ((strLine = br.readLine()) != null){
				lines.add(strLine);
			}
			in.close();
		} catch (IOException e){
			log.error("Cannot read from file: " + file.getName());
		}
		return lines;
	}
}
