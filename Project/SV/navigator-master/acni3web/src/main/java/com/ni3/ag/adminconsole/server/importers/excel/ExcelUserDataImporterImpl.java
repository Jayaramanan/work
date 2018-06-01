/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.importers.UserDataImporter;
import com.ni3.ag.adminconsole.server.importers.UserDataTable;
import com.ni3.ag.adminconsole.server.service.ExcelUserDataImporter;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ExcelUserDataImporterImpl implements ExcelUserDataImporter{
	private static final Logger log = Logger.getLogger(ExcelUserDataImporterImpl.class);
	private static final int ATTRIBUTE_NAME_ROW_INDEX = 1;
	private static final int DATA_COLUMN_OFFSET = 1;
	private static final int FIRST_DATA_ROW_INDEX = 3;
	private static final String ID_ATTRIBUTE_NAME = "id";
	private SchemaDAO schemaDAO;
	private UserDataImporter userDataImporter;

	public void setUserDataImporter(UserDataImporter userDataImporter){
		this.userDataImporter = userDataImporter;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	@Override
	public void importDataFromExcel(byte[] data, Integer schemaId, Integer userId, boolean recalculateFormulas)
			throws ACException{
		log.debug("importDataFromExcel");
		Schema sch = schemaDAO.getSchema(schemaId);
		log.debug("GOT schema: " + sch.getName());

		try{
			File f = File.createTempFile("" + System.currentTimeMillis() + sch.getName(), ".xls");
			FileOutputStream fos = new FileOutputStream(f);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(data);
			bos.flush();
			fos.flush();
			bos.close();
			fos.close();
			Workbook book = Workbook.getWorkbook(f);
			ObjectDefinition currentObject;
			for (Sheet sh : book.getSheets()){
				// in some cases excel stores marcoses in hidden sheets named __VBA__*
				if (sh.getName().startsWith("__VBA__"))
					continue;
				currentObject = resolveObjectDefinition(sh.getName(), sch);
				if (currentObject == null){
					log.error("cannot resolve object for name: " + sh.getName());
					throw new ACException(TextID.MsgObjectWithGivenNameNotFound, new String[] { sh.getName() });
				}
				if (currentObject.isNode()){
					processExcelSheet(sh, userId, currentObject, recalculateFormulas);
				}
			}
			for (Sheet sh : book.getSheets()){
				// in some cases excel stores marcoses in hidden sheets named
				if (sh.getName().startsWith("__VBA__"))
					continue;
				currentObject = resolveObjectDefinition(sh.getName(), sch);
				if (currentObject.isEdge()){
					processExcelSheet(sh, userId, currentObject, recalculateFormulas);
				}
			}
		} catch (IOException e){
			log.error("Error saving excel data to temp file", e);
			throw new ACException(TextID.MsgCannotParseFile);
		} catch (BiffException e){
			log.error("Error saving excel data to temp file", e);
			throw new ACException(TextID.MsgCannotParseFile);
		}
	}

	private void processExcelSheet(Sheet sh, Integer userId, ObjectDefinition currentObject, boolean recalculateFormulas)
			throws ACException{
		log.debug("processing sheet: " + sh.getName() + "...");
		UserDataTable data = getUserDataForObject(sh, currentObject);
		if (data == null || data.isEmpty()){
			log.warn("File is empty");
		} else{
			Map<String, Integer> nodeSrcIdMap = null;
			Map<String, Integer> edgeSrcIdMap = null;
			if (currentObject.isEdge()){
				nodeSrcIdMap = userDataImporter.getNodeSrcIdMap(currentObject.getSchema());
				edgeSrcIdMap = userDataImporter.getSrcIdMap(currentObject, null);
			} else{
				nodeSrcIdMap = userDataImporter.getSrcIdMap(currentObject, null);
			}
			userDataImporter.storeUserDataForObject(currentObject, data, userId, nodeSrcIdMap, edgeSrcIdMap,
					recalculateFormulas);
			log.debug("Stored data for object: " + currentObject.getName());
		}
		log.debug("processing sheet: " + sh.getName() + "...[ok]");
	}

	private UserDataTable getUserDataForObject(Sheet sheet, ObjectDefinition currentObject) throws ACException{
		log.debug("Importing object " + currentObject.getName());
		ObjectAttribute[] attributes = findAttributes(currentObject, sheet);
		log.debug("Resolved attribute count: " + attributes.length);
		if (currentObject.isEdge() && !checkFromToIds(attributes)){
			throw new ACException(TextID.MsgMandatoryFromToId);
		}
		log.debug("parsing data rows");
		UserDataTable dataTable = new UserDataTable(currentObject, attributes);
		for (int row = FIRST_DATA_ROW_INDEX; row < sheet.getRows(); row++){
			if (isEmptyRow(sheet, row)){
				log.debug("skipping empty row " + row);
				continue;
			}
			String[] data = getRowData(sheet, row);
			if (data.length < attributes.length){
				log.warn("Error parsing " + row + " data[].length=" + data.length + " | attribute[].length="
						+ attributes.length);
			}
			dataTable.addRow(data);
		}
		log.debug("Parsed row count for object definition " + currentObject.getName() + ": " + dataTable.size());
		return dataTable;
	}

	private boolean isEmptyRow(Sheet sheet, int row){
		boolean empty = true;
		for (int i = 0; i < sheet.getColumns() - 1; i++){
			Cell c = sheet.getCell(i + DATA_COLUMN_OFFSET, row);
			if (c.getContents() != null && !c.getContents().isEmpty()){
				empty = false;
				break;
			}
		}
		return empty;
	}

	private String[] getRowData(Sheet sheet, int row){
		List<String> ar = new ArrayList<String>();
		for (int i = 0; i < sheet.getColumns() - 1; i++){
			Cell c = sheet.getCell(i + DATA_COLUMN_OFFSET, row);
			ar.add(c.getContents());
		}
		return ar.toArray(new String[ar.size()]);

	}

	private ObjectAttribute[] findAttributes(ObjectDefinition od, Sheet sheet) throws ACException{
		if (sheet.getColumns() < 2)
			throw new ACException(TextID.MsgInvalidExcelColumnCount, new String[] { sheet.getName() });
		List<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		for (int i = 0; i < sheet.getColumns() - 1; i++){
			boolean found = false;
			Cell c = sheet.getCell(i + DATA_COLUMN_OFFSET, ATTRIBUTE_NAME_ROW_INDEX);
			String name = c.getContents();
			if (name.trim().isEmpty())
				break;
			if (ID_ATTRIBUTE_NAME.equalsIgnoreCase(name)){
				attrs.add(createFakeAttribute());
				log.debug("Found attribute with name id, skip it");
				continue;
			}
			for (ObjectAttribute oa : od.getObjectAttributes()){
				if (oa.getName().equalsIgnoreCase(name)){
					attrs.add(oa);
					found = true;
					log.debug("attribute " + name + " found");
					break;
				}
			}
			if (!found){
				log.error("attribute `" + name + "` not found");
				throw new ACException(TextID.MsgAttributeNotFoundForParameter, new String[] { name });
			}

		}
		return attrs.toArray(new ObjectAttribute[attrs.size()]);
	}

	private ObjectAttribute createFakeAttribute(){
		ObjectAttribute fake = new ObjectAttribute();
		fake.setId(-1);
		return fake;
	}

	private boolean checkFromToIds(ObjectAttribute[] attributes){
		boolean foundFrom = false;
		boolean foundTo = false;
		for (ObjectAttribute attr : attributes){
			if (attr == null)
				continue;
			if (ObjectAttribute.FROM_ID_ATTRIBUTE_NAME.equalsIgnoreCase(attr.getName())){
				foundFrom = true;
			} else if (ObjectAttribute.TO_ID_ATTRIBUTE_NAME.equalsIgnoreCase(attr.getName())){
				foundTo = true;
			}
		}

		return foundFrom && foundTo;
	}

	private ObjectDefinition resolveObjectDefinition(String sheetName, Schema sch){
		log.debug("Searching for object: " + sheetName);
		ObjectDefinition od = getObjectByName(sheetName, sch);
		if (od == null){
			int index = sheetName.lastIndexOf("_");
			if (index >= 0){
				od = getObjectByName(sheetName.substring(0, index), sch);
			}
		}

		if (od == null)
			log.warn("Object with name: " + sheetName + " not found");
		return od;
	}

	private ObjectDefinition getObjectByName(String sheetName, Schema sch){
		for (ObjectDefinition od : sch.getObjectDefinitions())
			if (sheetName.equalsIgnoreCase(od.getName()))
				return od;
		return null;
	}

}
