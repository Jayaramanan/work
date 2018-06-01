/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.LineWeight;

public interface LineWeightDAO{
	public List<LineWeight> getLineWeights();

	public LineWeight getDefaultLineWeight();

	public LineWeight getLineWeightByName(String name);

	public void saveOrUpdateAll(List<LineWeight> lineWeights);
}
