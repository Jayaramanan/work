/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.exporters;

import jxl.format.CellFormat;

public interface Ni3ExcelStyleSheet{
	public CellFormat getTableHeaderStyle();

	public CellFormat getMandatoryObjectAttributeStyle();
}
