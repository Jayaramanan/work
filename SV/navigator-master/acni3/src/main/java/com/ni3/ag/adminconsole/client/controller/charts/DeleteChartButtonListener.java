/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;


public class DeleteChartButtonListener extends ProgressActionListener{

	public DeleteChartButtonListener(ChartController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ChartController controller = (ChartController) getController();
		ChartView view = (ChartView) controller.getView();
		ChartModel model = (ChartModel) controller.getModel();
		view.stopTableEditing();
		view.clearErrors();

		Object object = model.getCurrentObject();
		if (object == null || !(object instanceof Chart)){
			return;
		}
		Chart currentChart = (Chart) model.getCurrentObject();
		Object[] path = getNewSelection(currentChart, view.getObjectTree().getSelectionPath());

		if (!controller.deleteChart())
			return;
		controller.loadData();
		controller.updateTree(true);

		TreeModelSupport support = new TreeModelSupport();
		TreePath found = support.findPathByNodes(path, view.getTreeModel());
		view.getObjectTree().setSelectionPath(found);
	}

	private Object[] getNewSelection(Chart current, TreePath oldPath){
		Object[] path = oldPath.getPath();
		Chart diffChart = null;
		List<Chart> charts = current.getSchema().getCharts();
		for (Chart chart : charts){
			if (!chart.equals(current)){
				diffChart = chart;
				break;
			}
		}
		if (diffChart != null){
			path[path.length - 1] = diffChart;
		} else{
			path = Arrays.copyOf(path, path.length - 1);
		}
		return path;
	}

}
