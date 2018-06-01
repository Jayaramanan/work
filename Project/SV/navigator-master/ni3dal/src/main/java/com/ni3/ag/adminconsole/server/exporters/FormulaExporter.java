/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import java.util.List;

import org.apache.log4j.Logger;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.ni3.ag.adminconsole.domain.Formula;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.validation.ACException;

public class FormulaExporter extends AbstractExporter<WritableSheet, List<ObjectAttribute>>{

	public static final String SHEET_NAME = "Formulas";
	private static final String[] CELL_LABELS = { "Object", "Attribute name", "Value" };
	private static final int OBJECT_COLUMN = 0;
	private static final int ATTRIBUTE_NAME_COLUMN = 1;
	private static final int FORMULA_COLUMN = 2;
	private static final int START_DATA_ROW = 1;

	private static final Ni3ExcelStyleSheet styleSheet = DefaultNi3ExcelStyleSheet.getInstance();
	private static final Logger log = Logger.getLogger(FormulaExporter.class);

	@Override
	protected void makeObjectExport(WritableSheet target, List<ObjectAttribute> dataContainer) throws ACException{

		for (int i = 0; i < dataContainer.size(); i++){
			ObjectAttribute attr = dataContainer.get(i);
			try{
				Formula f = attr.getFormula();
				target.addCell(new Label(OBJECT_COLUMN, START_DATA_ROW + i, attr.getObjectDefinition().getName()));
				target.addCell(new Label(ATTRIBUTE_NAME_COLUMN, START_DATA_ROW + i, attr.getName()));
				if (f != null)
					target.addCell(new Label(FORMULA_COLUMN, START_DATA_ROW + i, f.getFormula()));
			} catch (RowsExceededException e){
				log.error(e);
				throw new ACException(TextID.MsgFailedToMakeSchemaExport);
			} catch (WriteException e){
				log.error(e);
				throw new ACException(TextID.MsgFailedToMakeSchemaExport);
			}
		}
	}

	protected void makeDecoration(WritableSheet target, List<ObjectAttribute> oaList){
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

	protected void makeAfterDecoration(WritableSheet target, List<ObjectAttribute> oaList){
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
