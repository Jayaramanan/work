/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.LineWeight;
import com.ni3.ag.adminconsole.server.dao.LineWeightDAO;

public class LineWeightDAOMock implements LineWeightDAO{

	public List<LineWeight> getLineWeights(){
		List<LineWeight> lineWeights = new ArrayList<LineWeight>();

		LineWeight lineWeight1 = new LineWeight();
		lineWeight1.setId(1);
		lineWeight1.setLabel("1px");
		lineWeight1.setWidth(BigDecimal.ONE);

		LineWeight lineWeight2 = new LineWeight();
		lineWeight2.setId(2);
		lineWeight2.setLabel("2px");
		lineWeight2.setWidth(BigDecimal.ONE);

		lineWeights.add(lineWeight1);
		lineWeights.add(lineWeight2);

		return lineWeights;
	}

	@Override
	public LineWeight getDefaultLineWeight(){
		LineWeight lineWeight = new LineWeight();

		lineWeight.setId(0);
		lineWeight.setLabel("1px");
		lineWeight.setWidth(BigDecimal.ONE);

		return lineWeight;
	}

	@Override
	public LineWeight getLineWeightByName(String name){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveOrUpdateAll(List<LineWeight> lineWeights){
		// TODO Auto-generated method stub

	}

}
