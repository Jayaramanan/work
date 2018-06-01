/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;

public class RefreshObjectChartsButtonListener extends ProgressActionListener{

	public RefreshObjectChartsButtonListener(ChartController chartController){
		super(chartController);
	}

	@Override
	public void performAction(ActionEvent e){
		ChartController controller = (ChartController) getController();
		ChartView view = (ChartView) controller.getView();
		ObjectChart objectChart = view.getChartTableActiveRow();
		ChartAttribute dynamicAttr = view.getAttributeTableActiveRow();
		view.renderErrors(null);
		view.stopTableEditing();
		controller.loadData();
		controller.resetCurrentObject();
		controller.updateTree(false);
		controller.updateChartTable();
		if (objectChart != null)
			view.setChartTableActiveRow(objectChart);
		if (dynamicAttr != null)
			view.setAttributeTableActiveRow(dynamicAttr, true);
		view.resetEditedFields();
	}

}
