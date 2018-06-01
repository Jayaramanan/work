/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

public class DefaultNi3ExcelStyleSheet implements Ni3ExcelStyleSheet{

	public static final int FONT_SIZE = 10;

	private static final DefaultNi3ExcelStyleSheet instance = new DefaultNi3ExcelStyleSheet();
	private final CellFormat intCellFormat = new WritableCellFormat(NumberFormats.INTEGER);
	private final CellFormat floatCellFormat = new WritableCellFormat(NumberFormats.FLOAT);
	private final WritableFont headerFont = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD, false);
	private final WritableFont mandatoryFont = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.NO_BOLD, false,
			UnderlineStyle.NO_UNDERLINE, Colour.RED);
	private final CellFormat headerStyle = new WritableCellFormat(headerFont);
	private final CellFormat mandatoryStyle = new WritableCellFormat(mandatoryFont);


	private DefaultNi3ExcelStyleSheet(){
	}

	public static DefaultNi3ExcelStyleSheet getInstance(){
		return instance;
	}

	public CellFormat getTableHeaderStyle(){
		return headerStyle;
	}

	public CellFormat getMandatoryObjectAttributeStyle(){
		return mandatoryStyle;
	}

	public CellFormat getIntegerCellFormat(){
		return intCellFormat;
	}

	public CellFormat getFloatCellFormat(){
		return floatCellFormat;
	}

}
