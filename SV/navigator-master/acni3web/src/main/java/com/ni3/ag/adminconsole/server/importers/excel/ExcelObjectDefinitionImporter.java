/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.importers.excel;

import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.DataType;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectType;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.server.service.ObjectDefinitionService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.rules.ObjectDefinitionNameValidationRule;

public class ExcelObjectDefinitionImporter{
	private static final Logger log = Logger.getLogger(ExcelObjectDefinitionImporter.class);
	private final static String[] ROW_LABELS = new String[] { "Attribute name", "Database column name", "Datatype" };
	private final static int HEADER_COLUMN_INDEX = 0;
	private final static int IMPORTABLE_COLUMN_START_INDEX = 2;
	private final static int ATTRIBUTE_LABEL_ROW_INDEX = 0;
	private final static int ATTRIBUTE_NAME_ROW_INDEX = 1;
	private final static int ATTRIBUTE_DATATYPE_ROW_INDEX = 2;

	private static final String[] FIXED_ATTRIBUTES = new String[] { "Cmnt", "Directed", "Strength", "InPath",
			"ConnectionType", "FromID", "ToID" };

	private ObjectDefinitionNameValidationRule objectDefinitionNameValidationRule;
	private ObjectDefinitionService objectDefinitionService;

	int sort = 0, labelSort = 0, filterSort = 0, searchSort = 0, matrixSort = 0;

	public ObjectDefinitionService getObjectDefinitionService(){
		return objectDefinitionService;
	}

	public void setObjectDefinitionService(ObjectDefinitionService objectDefinitionService){
		this.objectDefinitionService = objectDefinitionService;
	}

	public ObjectDefinitionNameValidationRule getObjectDefinitionNameValidationRule(){
		return objectDefinitionNameValidationRule;
	}

	public void setObjectDefinitionNameValidationRule(ObjectDefinitionNameValidationRule objectDefinitionNameValidationRule){
		this.objectDefinitionNameValidationRule = objectDefinitionNameValidationRule;
	}

	public void importObject(Schema newSchema, User user, Sheet sheet) throws ACException{
		prepare();
		String newName = sheet.getName();
		log.debug("import object with name " + newName);
		log.debug("validating object name...");
		SchemaAdminModel model = new SchemaAdminModel();
		ObjectDefinition od = new ObjectDefinition();
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		od.setName(newName);
		model.setCurrentObjectDefinition(od);
		if (!objectDefinitionNameValidationRule.performCheck(model)){
			log.error("invlaid name for object difinition");
			throw new ACException(objectDefinitionNameValidationRule.getErrorEntries());
		}
		validateObjectSheetStructure(sheet);
		log.debug("validating object name...OK");

		od = objectDefinitionService.addObjectDefinition(newSchema, newName, user);
		od.setObjectAttributes(new ArrayList<ObjectAttribute>());
		log.debug("Object definition created");

		ObjectAttribute oa = new ObjectAttribute();
		oa.setName(ObjectAttribute.SRCID_ATTRIBUTE_NAME);
		oa.setLabel(ObjectAttribute.SRCID_ATTRIBUTE_NAME);
		oa.setSort(++sort);
		oa.setLabelSort(++labelSort);
		oa.setFilterSort(++filterSort);
		oa.setSearchSort(++searchSort);
		oa.setMatrixSort(++matrixSort);
		oa.setDataType(DataType.TEXT);
		oa.setObjectDefinition(od);
		od.getObjectAttributes().add(oa);

		log.debug("srcid attrbute created");

		List<ObjectAttribute> attrs = extractAttributes(od, sheet);
		ObjectType ot = resolveObjectType(attrs);
		od.setObjectType(ot);
		od.getObjectAttributes().addAll(filterAttributes(od, attrs));
		od.setSort(od.getId());

		model.setCurrentObjectDefinition(od);

		objectDefinitionService.addAttributeGroups(od.getObjectAttributes(), od);
		od = objectDefinitionService.updateObjectDefinition(od, true);
		newSchema.getObjectDefinitions().add(od);
	}

	private List<ObjectAttribute> filterAttributes(ObjectDefinition od, List<ObjectAttribute> attrs){
		if (!od.isNode())
			return attrs;
		List<ObjectAttribute> result = new ArrayList<ObjectAttribute>();
		for (ObjectAttribute oa : attrs)
			result.add(oa);
		return result;
	}

	private ObjectType resolveObjectType(List<ObjectAttribute> attrs){
		for (String FIXED_ATTRIBUTE : FIXED_ATTRIBUTES){
			boolean found = false;
			for (ObjectAttribute oa : attrs){
				if (oa.getName().equalsIgnoreCase(FIXED_ATTRIBUTE)){
					found = true;
					break;
				}
			}
			if (!found)
				return ObjectType.NODE;
		}
		return ObjectType.EDGE;
	}

	private void prepare(){
		sort = 0;
		labelSort = 0;
		filterSort = 0;
		searchSort = 0;
		matrixSort = 0;
	}

	private List<ObjectAttribute> extractAttributes(ObjectDefinition od, Sheet sheet) throws ACException{
		List<ObjectAttribute> attrs = new ArrayList<ObjectAttribute>();
		for (int column = IMPORTABLE_COLUMN_START_INDEX; column < sheet.getColumns(); column++){
			ObjectAttribute oa = new ObjectAttribute();
			oa.setSort(++sort);
			oa.setLabelSort(++labelSort);
			oa.setFilterSort(++filterSort);
			oa.setSearchSort(++searchSort);
			oa.setMatrixSort(++matrixSort);
			oa.setPredefinedAttributes(new ArrayList<PredefinedAttribute>());
			Cell labelCell = sheet.getCell(column, ATTRIBUTE_LABEL_ROW_INDEX);
			if (labelCell == null)
				throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"Expected cell with attribute label row " + ATTRIBUTE_LABEL_ROW_INDEX + " col " + column });
			Cell nameCell = sheet.getCell(column, ATTRIBUTE_NAME_ROW_INDEX);
			if (nameCell == null)
				throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"Expected cell with attribute name row " + ATTRIBUTE_NAME_ROW_INDEX + " col " + column });
			Cell dataTypeCell = sheet.getCell(column, ATTRIBUTE_DATATYPE_ROW_INDEX);
			if (dataTypeCell == null)
				throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"Expected cell with attribute datatype row " + ATTRIBUTE_DATATYPE_ROW_INDEX + " col " + column });

			String label = labelCell.getContents();
			oa.setLabel(label);
			String name = nameCell.getContents();
			oa.setName(name);
			String dt = dataTypeCell.getContents();
			oa.setDataType(getDataTypeForName(dt));
			oa.setObjectDefinition(od);
			attrs.add(oa);
			log.debug("\tfor object " + od.getName() + " parsed attribute " + oa.getLabel() + "/" + oa.getName() + "("
					+ oa.getDataType().getTextId().getKey() + ")");
		}
		return attrs;
	}

	private DataType getDataTypeForName(String dt) throws ACException{
		log.debug("Get dataType for string " + dt);
		DataType dataType = DataType.fromLabel(dt);
		if (dataType != null){
			return dataType;
		} else{
			log.error("Cannot resolve datatype for string " + dt);
			throw new ACException(TextID.MsgInvalidDataType, new String[] { dt });
		}
	}

	private void validateObjectSheetStructure(Sheet sheet) throws ACException{
		if (sheet.getRows() < ROW_LABELS.length)
			throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0", "Column count less then 3" });
		for (int i = 0; i < ROW_LABELS.length; i++){
			Cell c = sheet.getCell(HEADER_COLUMN_INDEX, i);
			String content = c.getContents();
			if (!ROW_LABELS[i].equals(content))
				throw new ACException(TextID.MsgWrongInputFormatForService, new String[] { "0",
						"Expected: " + ROW_LABELS[i] + " at row " + i + " col " + HEADER_COLUMN_INDEX });
		}
	}

}
