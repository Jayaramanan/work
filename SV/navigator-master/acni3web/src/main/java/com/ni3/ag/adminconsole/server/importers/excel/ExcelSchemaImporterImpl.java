/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dbservice.UserTableStructureService;
import com.ni3.ag.adminconsole.server.service.ExcelSchemaImporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.SchemaNameValidationRule;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.log4j.Logger;

public class ExcelSchemaImporterImpl implements ExcelSchemaImporter{
	private static final Logger log = Logger.getLogger(ExcelSchemaImporterImpl.class);

	private static final String FORMULAS_SHEET_NAME = "Formulas";
	private static final String USERS_SHEET_NAME = "Users";
	private static final String CONNECTION_TYPES_SHEET_NAME = "ObjectConnections";
	private static final String PREDEFINED_SHEET_NAME_PREFIX = "lov_";

	private ExcelObjectDefinitionImporter excelObjectDefinitionImporter;
	private ExcelPredefinedAttributeImporter excelPredefinedAttributeImporter;
	private ExcelFormulaImporter excelFormulaImporter;
	private ExcelConnectionTypeImporter excelConnectionTypeImporter;

	private SchemaAdminService schemaAdminService;

	private SchemaNameValidationRule schemaNameValidationRule;
	private UserTableStructureService userTableStructureService;

	public void setUserTableStructureService(UserTableStructureService userTableStructureService){
		this.userTableStructureService = userTableStructureService;
	}

	public void setExcelFormulaImporter(ExcelFormulaImporter excelFormulaImporter){
		this.excelFormulaImporter = excelFormulaImporter;
	}

	public void setExcelPredefinedAttributeImporter(ExcelPredefinedAttributeImporter excelPredefinedAttributeImporter){
		this.excelPredefinedAttributeImporter = excelPredefinedAttributeImporter;
	}

	public void setExcelObjectDefinitionImporter(ExcelObjectDefinitionImporter excelObjectDefinitionImporter){
		this.excelObjectDefinitionImporter = excelObjectDefinitionImporter;
	}

	public void setSchemaNameValidationRule(SchemaNameValidationRule schemaNameValidationRule){
		this.schemaNameValidationRule = schemaNameValidationRule;
	}

	public void setSchemaAdminService(SchemaAdminService schemaAdminService){
		this.schemaAdminService = schemaAdminService;
	}

	public void setExcelConnectionTypeImporter(ExcelConnectionTypeImporter excelConnectionTypeImporter){
		this.excelConnectionTypeImporter = excelConnectionTypeImporter;
	}

	@Override
	public void importExcelSchema(byte[] data, String schemaName, User user) throws ACException{
		log.debug("Call importExcelSchema");
		try{
			File f = File.createTempFile("" + System.currentTimeMillis() + "_" + schemaName, ".xls");
			FileOutputStream fs = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fs);
			bos.write(data);
			bos.flush();
			fs.flush();
			bos.close();
			fs.close();
			log.debug("excel serialized to temp file " + f.getName());
			Workbook book = Workbook.getWorkbook(f);
			log.debug("Workbook instance create from file");
			processExcel(book, schemaName, user);
			log.debug("workbook processed");
			book.close();
		} catch (IOException e){
			log.error("error creating temp file with excel", e);
			throw new ACException(TextID.MsgImportError, new String[] { e.getMessage() });
		} catch (BiffException e){
			log.error("error reading excel file", e);
			throw new ACException(TextID.MsgImportError, new String[] { e.getMessage() });
		}
	}

	private void processExcel(Workbook book, String schemaName, User user) throws ACException{
		log.debug("creating schema");
		Schema newSchema = createSchema(schemaName, user);
		log.debug("schema `" + schemaName + "` created");
		Sheet[] sheets = book.getSheets();
		log.debug("workbook contains " + sheets.length + " sheets to process");
		for (int i = 0; i < sheets.length; i++){
			Sheet sheet = sheets[i];
			String sheetName = sheet.getName().toLowerCase();
			log.debug("processing sheet `" + sheetName + "`...");
			if (FORMULAS_SHEET_NAME.equalsIgnoreCase(sheetName)){
				log.debug("Looks like it is formulas sheet");
				importFormulas(newSchema, user, sheet);
			} else if (CONNECTION_TYPES_SHEET_NAME.equalsIgnoreCase(sheetName)){
				log.debug("Looks like it is connections sheet");
				importConnectionTypes(newSchema, sheet);
			} else if (USERS_SHEET_NAME.equalsIgnoreCase(sheetName)){
				log.debug("Looks like it is users sheet");
			} else if (sheetName.startsWith(PREDEFINED_SHEET_NAME_PREFIX)){
				log.debug("Looks like it is predefined value list sheet");
				importPredefined(newSchema, user, sheet);
			} else{
				log.debug("Looks like it is object sheet");
				importObject(newSchema, user, sheet);
			}
			log.debug("call schema generation");
			log.debug("processing sheet `" + sheetName + "`...done");
		}

		for (int i = 0; i < sheets.length; i++){
			Sheet sheet = sheets[i];
			String sheetName = sheet.getName().toLowerCase();
			if (sheetName.startsWith(PREDEFINED_SHEET_NAME_PREFIX)){
				log.debug("Looks like it is predefined value list sheet");
				importPredefinedParents(newSchema, user, sheet);
			}
		}
		userTableStructureService.updateUserTables(newSchema);
	}

	private void importConnectionTypes(Schema newSchema, Sheet sheet){
		excelConnectionTypeImporter.importObject(newSchema, sheet);
	}

	private void importObject(Schema newSchema, User user, Sheet sheet) throws ACException{
		excelObjectDefinitionImporter.importObject(newSchema, user, sheet);
	}

	private void importPredefined(Schema newSchema, User user, Sheet sheet) throws ACException{
		excelPredefinedAttributeImporter.importPredefined(newSchema, user, sheet);
	}

	private void importPredefinedParents(Schema newSchema, User user, Sheet sheet) throws ACException{
		excelPredefinedAttributeImporter.importPredefinedParents(newSchema, user, sheet);
	}

	private void importFormulas(Schema newSchema, User user, Sheet sheet) throws ACException{
		excelFormulaImporter.importFormulas(newSchema, user, sheet);
	}

	private Schema createSchema(String schemaName, User user) throws ACException{
		Schema schema = new Schema();

		log.debug("Validating given schema name");
		SchemaAdminModel model = new SchemaAdminModel();
		schema.setName(schemaName);
		model.setCurrentSchema(schema);
		if (!schemaNameValidationRule.performCheck(model)){
			log.error("schema with such name already exists - cannot proceed import");
			throw new ACException(schemaNameValidationRule.getErrorEntries());
		}

		schema = schemaAdminService.addSchema(schemaName, user);
		schema.setObjectDefinitions(new ArrayList<ObjectDefinition>());

		return schema;
	}
}
