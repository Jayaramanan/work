/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.export;

import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;

public class DefaultNi3ExcelStyleSheet{

	public static final int FONT_SIZE = 10;

	private static final DefaultNi3ExcelStyleSheet instance = new DefaultNi3ExcelStyleSheet();

	private DefaultNi3ExcelStyleSheet(){
	}

	public static DefaultNi3ExcelStyleSheet getInstance(){
		return instance;
	}

	public CellFormat getTableHeaderStyle(){
		WritableFont font = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.BOLD, false);
		return new WritableCellFormat(font);
	}

	public CellFormat getMandatoryObjectAttributeStyle(){
		WritableFont font = new WritableFont(WritableFont.ARIAL, FONT_SIZE, WritableFont.NO_BOLD, false,
		        UnderlineStyle.NO_UNDERLINE, Colour.RED);
		return new WritableCellFormat(font);
	}

}
