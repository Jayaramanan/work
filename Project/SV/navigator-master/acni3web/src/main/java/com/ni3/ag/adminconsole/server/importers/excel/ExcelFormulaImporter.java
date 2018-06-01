/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jxl.Cell;
import jxl.Sheet;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ExcelFormulaImporter{
	private static final Logger log = Logger.getLogger(ExcelFormulaImporter.class);
	private static final String[] HEADERS = { "Object", "Attribute name", "Value" };
	private static final int OBJECT_COLUMN = 0;
	private static final int ATTRIBUTE_COLUMN = 1;
	private static final int FORMULA_COLUMN = 2;
	private static int HEADER_ROW = 0;

	private ObjectAttributeDAO objectAttributeDAO;

	public ObjectAttributeDAO getObjectAttributeDAO(){
		return objectAttributeDAO;
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	public void importFormulas(Schema newSchema, User user, Sheet sheet) throws ACException{
		log.debug("importFormulas");
		validate(sheet);
		log.debug("sheet validated");
		List<ObjectAttribute> attributes = new ArrayList<ObjectAttribute>();
		for (int i = HEADER_ROW + 1; i < sheet.getRows(); i++){
			Cell objectCell = sheet.getCell(OBJECT_COLUMN, i);
			Cell attributeCell = sheet.getCell(ATTRIBUTE_COLUMN, i);
			Cell formulaCell = sheet.getCell(FORMULA_COLUMN, i);
			if (objectCell == null || attributeCell == null || formulaCell == null){
				log.error("Invalid excel format - cannot find one of columns objCell=" + objectCell + " attrCell="
				        + attributeCell + " formulaCell=" + formulaCell);
				throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
				        "Invalid format on sheet " + sheet.getName() });
			}
			log.debug("setting formula for " + objectCell.getContents() + "." + attributeCell.getContents() + "="
			        + formulaCell.getContents());
			ObjectDefinition od = getObjectForFormula(newSchema, objectCell.getContents());
			ObjectAttribute oa = getAttributeForFormula(od, attributeCell.getContents());
			oa.setPredefined_(oa.isPredefined() ? Formula.FORMULA_PREDEFINED : Formula.FORMULA_BASED);
			if (oa.getFormula() == null)
				oa.setFormula(new Formula());
			oa.getFormula().setAttribute(oa);
			oa.getFormula().setFormula(formulaCell.getContents());
			attributes.add(oa);
		}
		objectAttributeDAO.saveOrUpdateAll(attributes);
	}

	private ObjectAttribute getAttributeForFormula(ObjectDefinition od, String name) throws ACException{
		log.debug("getAttributeForPredefineds");
		for (ObjectAttribute oa : od.getObjectAttributes())
			if (oa.getName().equals(name))
				return oa;
		log.error("object for name `" + name + "` not found");
		throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
		        "No such attribute " + name + " in object " + od.getName() });
	}

	private ObjectDefinition getObjectForFormula(Schema newSchema, String name) throws ACException{
		log.debug("getObjectForPredefineds");
		for (ObjectDefinition od : newSchema.getObjectDefinitions())
			if (od.getName().equals(name))
				return od;
		log.error("object for name `" + name + "` not found");
		throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0", "No such object " + name });
	}

	private void validate(Sheet sheet) throws ACException{
		for (int i = 0; i < HEADERS.length; i++){
			Cell c = sheet.getCell(i, HEADER_ROW);
			if (!HEADERS[i].equals(c.getContents()))
				throw new ACException(TextID.MsgImportError, new String[] { "In worksheet `" + sheet.getName() + "` row "
				        + HEADER_ROW + " col " + i + " expected " + HEADERS[i] });
		}
	}
}
