/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.LineStyle;

public interface LineStyleDAO{
	public List<LineStyle> getLineStyles();

	public LineStyle getLineStyleByName(String name);

	public void saveOrUpdateAll(ArrayList<LineStyle> lineStyles);
}
