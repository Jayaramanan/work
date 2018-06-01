/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UpdateObjectChartsButtonListener extends ProgressActionListener implements CellEditorListener{

	private ChartController controller;
	private ACValidationRule ocValidationRule;
	private ACValidationRule duplicateValidationRule;
	private ACValidationRule mandatoryRule;
	private ACValidationRule attrMandatoryRule;
	private ACValidationRule attrNameMandatoryRule;
	private ACValidationRule chartNameValidationRule;

	public UpdateObjectChartsButtonListener(ChartController chartController){
		super(chartController);
		controller = chartController;
		ocValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("objectChartValidationRule");
		duplicateValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "duplicateObjectChartValidationRule");
		mandatoryRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("objectChartMandatoryFieldsValidationRule");
		attrMandatoryRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "chartAttributesMandatoryFieldsValidationRule");
		attrNameMandatoryRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "chartAttributeUniqueValidationRule");
		chartNameValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("chartNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		ChartView view = (ChartView) controller.getView();
		ObjectChart objectChart = view.getChartTableActiveRow();
		ChartAttribute dynamicAttr = view.getAttributeTableActiveRow();
		ChartModel cm = controller.getModel();
		cm.setNewChartName(null);
		if (!save()){
			return;
		}
		if (objectChart != null)
			view.setChartTableActiveRow(objectChart);
		if (dynamicAttr != null)
			view.setAttributeTableActiveRow(dynamicAttr, true);
	}

	public boolean save(){
		ChartView view = (ChartView) controller.getView();
		view.stopTableEditing();
		view.clearErrors();
		ChartModel model = (ChartModel) controller.getModel();
		Object o = model.getCurrentObject();
		if (o == null || !(o instanceof Chart))
			return true;

		if (!chartNameValidationRule.performCheck(model)){
			view.renderErrors(chartNameValidationRule.getErrorEntries());
			return false;
		}

		if (!ocValidationRule.performCheck(model)){
			view.renderErrors(ocValidationRule.getErrorEntries());
			return false;
		}

		if (!duplicateValidationRule.performCheck(model)){
			view.renderErrors(duplicateValidationRule.getErrorEntries());
			return false;
		}

		if (!mandatoryRule.performCheck(model)){
			view.renderErrors(mandatoryRule.getErrorEntries());
			return false;
		}

		if (!attrMandatoryRule.performCheck(model)){
			view.renderErrors(attrMandatoryRule.getErrorEntries());
			return false;
		}

		if (!attrNameMandatoryRule.performCheck(model)){
			view.renderErrors(attrNameMandatoryRule.getErrorEntries());
			return false;
		}
		controller.saveCurrentChart();

		return true;
	}

	@Override
	public void editingCanceled(ChangeEvent e){

	}

	@Override
	public void editingStopped(ChangeEvent e){

	}

	public boolean saveFromTree(Chart chrt){
		ChartModel model = controller.getModel();
		Schema s = chrt.getSchema();
		List<Chart> charts = s.getCharts();
		boolean removed = charts.remove(chrt);

		Object oldObject = model.getCurrentObject();
		String oldNewChartName = model.getNewChartName();

		model.setCurrentObject(chrt);
		model.setNewChartName(chrt.getName());
		boolean ok = save();
		if (removed)
			charts.add(chrt);

		model.setCurrentObject(oldObject);
		model.setNewChartName(oldNewChartName);

		return ok;
	}

}
