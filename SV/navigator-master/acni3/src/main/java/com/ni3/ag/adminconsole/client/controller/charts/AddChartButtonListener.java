/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.charts.ChartTreeModel;
import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.client.view.common.ACOptionPane;
import com.ni3.ag.adminconsole.client.view.common.StringValidator;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class AddChartButtonListener extends ProgressActionListener{

	private ChartController controller;
	private ACValidationRule chartNameValidationRule;

	public AddChartButtonListener(ChartController controller){
		super(controller);
		this.controller = controller;
		this.chartNameValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("chartNameValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		ChartView view = controller.getView();
		ChartModel model = controller.getModel();
		view.stopTableEditing();
		view.clearErrors();

		Object object = model.getCurrentObject();
		if (object == null){
			return;
		}

		addNewChart();
	}

	private void addNewChart(){
		ChartModel model = controller.getModel();
		ChartView view = controller.getView();
		Object object = model.getCurrentObject();
		Schema currentSchema = null;
		if (object instanceof Schema){
			currentSchema = (Schema) object;
		} else if (object instanceof Chart){
			currentSchema = ((Chart) object).getSchema();
		}

		if (currentSchema != null){
			String message = Translation.get(TextID.MsgEnterNameOfNewChart);
			String chartName = ACOptionPane.showInputDialog(view, message, Translation.get(TextID.NewChart));
			chartName = StringValidator.validate(chartName);
			if (chartName == null || chartName.length() == 0){
				return ;
			}
			model.setNewChartName(chartName);

			if (!chartNameValidationRule.performCheck(model)){
				view.renderErrors(chartNameValidationRule.getErrorEntries());
				return ;
			}

			TreePath currentPath = view.getObjectTree().getSelectionPath();

			Chart chart = new Chart();
			chart.setSchema(currentSchema);
			chart.setName(chartName);
			Chart newChart = controller.addChart(chart);

			controller.loadData();
			controller.updateTree(false);
			restoreTreeSelection(currentPath, newChart);
		}
	}

	private void restoreTreeSelection(TreePath selectedPath, Chart newChart){
		Object[] oldNodes = selectedPath.getPath();
		Object[] newNodes = new Object[] { oldNodes[0], oldNodes[1], oldNodes[2], newChart };

		ChartTreeModel treeModel = controller.getView().getTreeModel();
		TreePath found = new TreeModelSupport().findPathByNodes(newNodes, treeModel);
		if (found != null){
			controller.getView().getObjectTree().setSelectionPath(found);
		}
	}

}
