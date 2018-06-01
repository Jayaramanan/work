package com.ni3.ag.adminconsole.client.controller.charts;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;

public class ObjectChartTableSelectionListener implements ListSelectionListener{
	private ChartController controller;

	public ObjectChartTableSelectionListener(ChartController ctrl){
		controller = ctrl;
	}

	public void valueChanged(ListSelectionEvent e){
		if (e.getValueIsAdjusting())
			return;

		ChartView view = (ChartView) controller.getView();
		ChartModel model = (ChartModel) controller.getModel();

		view.resetEditedFields();
		view.clearErrors();

		ObjectChart objectChart = ((ChartView) controller.getView()).getSelectedObjectChart();
		model.setCurrentObjectChart(objectChart);

		controller.updateAttributeTable();
		controller.updateChartPreview();
	}

	public boolean canSwitch(){
		final ChartView cView = controller.getView();
		if (cView.isAttributeTableChanged()){
			return controller.canSwitch(true);
		}
		return true;
	}
}