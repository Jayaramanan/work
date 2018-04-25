/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class ChartAttributeTableModelListener implements TableModelListener{

	private ChartController controller;

	public ChartAttributeTableModelListener(ChartController chartController){
		controller = chartController;
	}

	@Override
	public void tableChanged(TableModelEvent e){
		if (e.getType() == TableModelEvent.UPDATE)
			controller.updateChartPreview();
	}

}
