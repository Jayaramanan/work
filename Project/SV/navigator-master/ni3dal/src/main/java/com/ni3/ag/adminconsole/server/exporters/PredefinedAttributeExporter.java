/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.util.List;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ObjectAttributeDAO;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class PredefinedAttributeExporter extends AbstractExporter<WritableWorkbook, Schema>{

	private final static Logger log = Logger.getLogger(PredefinedAttributeExporter.class);

	public static final String SHEET_NAME_PREFIX = "LOV_";

	private static final int OBJECT_DEFINITION_COLUMN = 0;
	private static final int OBJECT_ATTRIBUTE_COLUMN = 1;
	private static final int PREDEFINED_ATTRIBUTE_VALUE_COLUMN = 2;
	private static final int PREDEFINED_ATTRIBUTE_LABEL_COLUMN = 3;
	private static final int PARENT_ATTRIBUTE_COLUMN = 4;
	private static final int PARENT_VALUE_COLUMN = 5;
	private static final int PARENT_LABEL_COLUMN = 6;

	private static final int START_DATA_ROW = 1;

	private static final String[] CELL_LABELS = { "Object", "Attribute name", "Possible values", "Labels",
			"Parent attribute", "Parent value", "Parent name" };

	private int startSheetNr;

	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();

	private ObjectAttributeDAO objectAttributeDAO;

	public void setObjectAttributeDAO(ObjectAttributeDAO objectAttributeDAO){
		this.objectAttributeDAO = objectAttributeDAO;
	}

	@Override
	protected void makeObjectExport(WritableWorkbook book, Schema schema) throws ACException{
		List<ObjectDefinition> objects = schema.getObjectDefinitions();
		startSheetNr = book.getNumberOfSheets();
		int sheetNr = startSheetNr;
		int index = 1;
		for (ObjectDefinition object : objects){
			List<ObjectAttribute> attributes = objectAttributeDAO.getPredefinedObjectAttributes(object);
			for (ObjectAttribute attribute : attributes){
				WritableSheet sheet = book.createSheet(SHEET_NAME_PREFIX + attribute.getName() + "_" + index++, sheetNr++);
				exportObjectAttribute(sheet, attribute, object.getName());
			}
		}
		for (int i = startSheetNr; i < book.getNumberOfSheets(); i++){
			makeDecoration(book.getSheet(i), schema);
			makeAfterDecoration(book.getSheet(i), schema);
		}
	}

	private void exportObjectAttribute(WritableSheet target, ObjectAttribute attribute, String odName) throws ACException{
		try{
			target.addCell(new Label(OBJECT_DEFINITION_COLUMN, START_DATA_ROW, odName));
			target.addCell(new Label(OBJECT_ATTRIBUTE_COLUMN, START_DATA_ROW, attribute.getName()));
		} catch (RowsExceededException e){
			log.error(e);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		} catch (WriteException e){
			log.error(e);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		}
		List<PredefinedAttribute> predefineds = attribute.getPredefinedAttributes();
		exportPredefineds(target, predefineds);
	}

	private void exportPredefineds(WritableSheet target, List<PredefinedAttribute> predefineds){
		int row = START_DATA_ROW;
		for (PredefinedAttribute predefined : predefineds){
			try{
				target.addCell(new Label(PREDEFINED_ATTRIBUTE_VALUE_COLUMN, row, predefined.getValue()));
				target.addCell(new Label(PREDEFINED_ATTRIBUTE_LABEL_COLUMN, row, predefined.getLabel()));
				final PredefinedAttribute parent = predefined.getParent();
				if (parent != null){
					target.addCell(new Label(PARENT_ATTRIBUTE_COLUMN, row, parent.getObjectAttribute().getName()));
					target.addCell(new Label(PARENT_VALUE_COLUMN, row, parent.getValue()));
					target.addCell(new Label(PARENT_LABEL_COLUMN, row, parent.getLabel()));
				}
			} catch (RowsExceededException e){
				log.error(e);
			} catch (WriteException e){
				log.error(e);
			}
			row++;
		}
	}

	protected void makeDecoration(WritableSheet target, Schema schema){
		try{
			for (int i = 0; i < CELL_LABELS.length; i++){
				Label l = new Label(i, 0, CELL_LABELS[i]);
				l.setCellFormat(styleSheet.getTableHeaderStyle());
				target.addCell(l);
			}
		} catch (RowsExceededException e){
			log.error(e);
		} catch (WriteException e){
			log.error(e);
		}
	}

	protected void makeAfterDecoration(WritableSheet target, Schema dataContainer){
		for (int i = 0; i < CELL_LABELS.length; i++){
			int maxWidth = 0;
			for (int j = 0; j < target.getRows(); j++){
				int width = target.getCell(i, j).getContents().length();
				if (width > maxWidth)
					maxWidth = width;
			}
			target.setColumnView(i, maxWidth);
		}
	}
}
