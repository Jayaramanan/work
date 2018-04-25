/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.excel;

import java.util.ArrayList;

import jxl.Cell;
import jxl.Sheet;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.PredefinedAttributeEditModel;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.PredefAttributeValidationRule;

public class ExcelPredefinedAttributeImporter{
	private static final Logger log = Logger.getLogger(ExcelPredefinedAttributeImporter.class);
	private static final String[] HEADERS = { "Object", "Attribute name", "Possible values", "Labels" };
	private static final int HEADER_ROW = 0;
	private static final int OBJECT_COLUMN = 0;
	private static final int ATTRIBUTE_COLUMN = 1;
	private static final int VALUE_COLUMN = 2;
	private static final int LABEL_COLUMN = 3;
	private static final int PARENT_ATTRIBUTE_COLUMN = 4;
	private static final int PARENT_VALUE_COLUMN = 5;
	private static final int PARENT_LABEL_COLUMN = 6;

	private PredefAttributeValidationRule predefAttributeValidationRule;
	private ObjectAttributeDAO objectAttributeDAO;

	public ObjectAttributeDAO getObjectAttributeDAO(){
		return objectAttributeDAO;
	}

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	public PredefAttributeValidationRule getPredefAttributeValidationRule(){
		return predefAttributeValidationRule;
	}

	public void setPredefAttributeValidationRule(PredefAttributeValidationRule predefAttributeValidationRule){
		this.predefAttributeValidationRule = predefAttributeValidationRule;
	}

	public void importPredefined(Schema newSchema, User user, Sheet sheet) throws ACException{
		log.debug("call ExcelPredefinedAttributeImporter.importPredefined");
		String sheetName = sheet.getName();
		log.debug("sheet name to parse " + sheetName);
		validate(sheet);
		ObjectDefinition od = getObjectForPredefineds(newSchema, sheet);
		ObjectAttribute oa = getAttributeForPredefineds(od, sheet);
		oa.setPredefined_(oa.isFormulaAttribute() ? Formula.FORMULA_PREDEFINED : Formula.PREDEFINED);
		oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
		for (int i = HEADER_ROW + 1; i < sheet.getRows(); i++){

			Cell valueCell = sheet.getCell(VALUE_COLUMN, i);
			Cell labelCell = sheet.getCell(LABEL_COLUMN, i);
			String value = valueCell.getContents();
			String label = labelCell.getContents();
			log.debug("\tparsed value " + label + "(" + value + ")");
			if (label.isEmpty() || value.isEmpty()){
				log.warn("Label or value is empty");
				continue;
			}

			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setObjectAttribute(oa);
			pa.setLabel(label);
			pa.setValue(value);
			oa.getPredefinedAttributes().add(pa);
		}

		PredefinedAttributeEditModel model = new PredefinedAttributeEditModel();
		model.setCurrentAttribute(oa);
		if (!predefAttributeValidationRule.performCheck(model))
			throw new ACException(predefAttributeValidationRule.getErrorEntries());
		objectAttributeDAO.saveOrUpdate(oa);
	}

	private ObjectAttribute getAttributeForPredefineds(ObjectDefinition od, Sheet sheet) throws ACException{
		log.debug("getAttributeForPredefineds");
		Cell c = sheet.getCell(ATTRIBUTE_COLUMN, HEADER_ROW + 1);
		if (c == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"Expected cell with object name row " + (HEADER_ROW + 1) + " col " + OBJECT_COLUMN });
		String attrName = c.getContents();
		log.debug("Searching to object `" + attrName + "`");
		if (attrName == null || attrName.isEmpty())
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"Expected cell with attribute name row " + (HEADER_ROW + 1) + " col " + ATTRIBUTE_COLUMN });
		for (ObjectAttribute oa : od.getObjectAttributes())
			if (oa.getName().equalsIgnoreCase(attrName))
				return oa;
		log.error("object for name `" + attrName + "` not found");
		throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
				"No such attribute " + attrName + " in object " + od.getName() });
	}

	private ObjectDefinition getObjectForPredefineds(Schema newSchema, Sheet sheet) throws ACException{
		log.debug("getObjectForPredefineds");
		Cell c = sheet.getCell(OBJECT_COLUMN, HEADER_ROW + 1);
		if (c == null)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"Expected cell with object name row " + (HEADER_ROW + 1) + " col " + OBJECT_COLUMN });
		String objectName = c.getContents();
		log.debug("Searching to object `" + objectName + "`");
		if (objectName == null || objectName.isEmpty())
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
					"Expected cell with object name row " + (HEADER_ROW + 1) + " col " + OBJECT_COLUMN });
		for (ObjectDefinition od : newSchema.getObjectDefinitions())
			if (od.getName().equalsIgnoreCase(objectName))
				return od;
		log.error("object for name `" + objectName + "` not found");
		throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0", "No such object " + objectName });
	}

	private void validate(Sheet sheet) throws ACException{
		for (int i = 0; i < HEADERS.length; i++){
			Cell c = sheet.getCell(i, HEADER_ROW);
			if (!HEADERS[i].equals(c.getContents()))
				throw new ACException(TextID.MsgImportError, new String[] { "In worksheet `" + sheet.getName() + "` row "
						+ HEADER_ROW + " col " + i + " expected " + HEADERS[i] });
		}
	}

	public void importPredefinedParents(Schema newSchema, User user, Sheet sheet) throws ACException{
		log.debug("call ExcelPredefinedAttributeImporter.importPredefinedParents");
		String sheetName = sheet.getName();
		log.debug("sheet name to parse " + sheetName);
		validate(sheet);
		ObjectDefinition od = getObjectForPredefineds(newSchema, sheet);
		ObjectAttribute oa = getAttributeForPredefineds(od, sheet);
		for (int i = HEADER_ROW + 1; i < sheet.getRows(); i++){
			Cell valueCell = sheet.getCell(VALUE_COLUMN, i);
			Cell labelCell = sheet.getCell(LABEL_COLUMN, i);
			Cell pAttrCell = sheet.getCell(PARENT_ATTRIBUTE_COLUMN, i);
			Cell pValueCell = sheet.getCell(PARENT_VALUE_COLUMN, i);
			Cell pLabelCell = sheet.getCell(PARENT_LABEL_COLUMN, i);

			String value = valueCell.getContents();
			String label = labelCell.getContents();
			String pAttrName = pAttrCell.getContents();
			String pValue = pValueCell.getContents();
			String pLabel = pLabelCell.getContents();

			if (value.isEmpty() || label.isEmpty() || pAttrName.isEmpty() || pLabel.isEmpty() || pValue.isEmpty()){
				continue;
			}
			log.debug("\tparsed parent predefined " + pLabel + "(" + pValue + ")");

			PredefinedAttribute pa = getPredefinedAttributeByValueAndLabel(oa, value, label);
			if (pa != null){
				ObjectAttribute parentAttr = getObjectAttributeByName(od, pAttrName);
				if (parentAttr != null){
					PredefinedAttribute parentPa = getPredefinedAttributeByValueAndLabel(parentAttr, pValue, pLabel);
					if (parentPa != null){
						pa.setParent(parentPa);
					} else{
						log.warn("Cannot find parent predefined attribute: " + pLabel + "(" + pValue + ")");
					}
				} else{
					log.warn("Cannot find parent attribute: " + pAttrName);
				}
			}
		}

		objectAttributeDAO.saveOrUpdate(oa);
	}

	PredefinedAttribute getPredefinedAttributeByValueAndLabel(ObjectAttribute oa, String value, String label){
		if (oa.getPredefinedAttributes() == null)
			return null;
		for (PredefinedAttribute pa : oa.getPredefinedAttributes()){
			if (pa.getValue().equals(value) && pa.getLabel().equals(label))
				return pa;
		}
		return null;
	}

	ObjectAttribute getObjectAttributeByName(ObjectDefinition object, String oaName){
		if (object.getObjectAttributes() == null)
			return null;
		for (ObjectAttribute attr : object.getObjectAttributes()){
			if (attr.getName().equals(oaName))
				return attr;
		}
		return null;
	}
}
