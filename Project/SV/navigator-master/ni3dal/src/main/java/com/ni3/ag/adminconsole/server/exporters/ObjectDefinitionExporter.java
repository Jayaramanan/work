/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class ObjectDefinitionExporter extends AbstractExporter<WritableSheet, ObjectDefinition>{

	private final static Logger log = Logger.getLogger(ObjectDefinitionExporter.class);

	private final static int ATTRIBUTE_NAME_ROW = 0;
	private final static int DATABASE_COLUMN_NAME_ROW = 1;
	private final static int DATATYPE_ROW = 2;
	private final static String SRCID_ATTRIBUTE = "srcid";

	private final static String[] ROW_LABELS = new String[] { "Attribute name", "Database column name", "Datatype" };

	private int startRow;
	private int startCol;
	private int currentCol;
	private int currentRow;

	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();

	public int getStartRow(){
		return startRow;
	}

	public void setStartRow(int startRow){
		this.startRow = startRow;
	}

	public int getStartCol(){
		return startCol;
	}

	public void setStartCol(int startCol){
		this.startCol = startCol;
	}

	protected void makeObjectExport(WritableSheet sheet, ObjectDefinition od) throws ACException{
		Hibernate.initialize(od.getObjectAttributes());

		int baseCol = currentCol;
		int baseRow = currentRow;
		try{
			sheet.addCell(new Label(baseCol, baseRow, ""));
			sheet.addCell(new Label(baseCol, baseRow + 1, "id"));
			sheet.addCell(new Label(baseCol, baseRow + 2, "text"));
			baseCol++;
			for (int i = 0; i < od.getObjectAttributes().size(); i++){
				ObjectAttribute oa = od.getObjectAttributes().get(i);
				if (SRCID_ATTRIBUTE.equalsIgnoreCase(oa.getName())){
					continue;
				}
				int width = oa.getName().length();
				if (oa.getLabel().length() > width){
					width = oa.getLabel().length();
				}
				width++;
				if (width < 10)
					width = 10;

				Label oaLabelCell = new Label(baseCol, baseRow, oa.getLabel());
				Label oaNameCell = new Label(baseCol, baseRow + 1, oa.getName());
				Label oaDataTypeCell = new Label(baseCol, baseRow + 2, oa.getDataType().getTextId().getKey());

				sheet.addCell(oaLabelCell);
				sheet.addCell(oaNameCell);
				sheet.addCell(oaDataTypeCell);
				sheet.setColumnView(baseCol, width);
				baseCol++;
			}
		} catch (RowsExceededException e){
			log.error(e);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		} catch (WriteException e){
			log.error(e);
			throw new ACException(TextID.MsgFailedToMakeSchemaExport);
		}
		currentRow = baseRow;

	}

	protected void makeDecoration(WritableSheet sheet, ObjectDefinition od){
		try{
			Label l = new Label(startCol, startRow, ROW_LABELS[ATTRIBUTE_NAME_ROW]);
			l.setCellFormat(styleSheet.getTableHeaderStyle());
			sheet.addCell(l);
			l = new Label(startCol, startRow + 1, ROW_LABELS[DATABASE_COLUMN_NAME_ROW]);
			l.setCellFormat(styleSheet.getTableHeaderStyle());
			sheet.addCell(l);
			l = new Label(startCol, startRow + 2, ROW_LABELS[DATATYPE_ROW]);
			l.setCellFormat(styleSheet.getTableHeaderStyle());
			sheet.addCell(l);
		} catch (RowsExceededException e){
			log.error(e);
		} catch (WriteException e){
			log.error(e);
		}
		currentCol = startCol + 1;
		currentRow = startRow;
	}

	@Override
	protected void makeAfterDecoration(WritableSheet target, ObjectDefinition od){
		for (int i = startCol; i < target.getColumns(); i++){
			int maxWidth = 8;
			for (int j = startRow; j < target.getRows(); j++){
				int width = target.getCell(i, j).getContents().length();
				if (width > maxWidth){
					maxWidth = width;
				}
			}
			target.setColumnView(i, maxWidth);
		}
	}
}
