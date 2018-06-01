/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.charts;

import static com.ni3.ag.adminconsole.client.view.Translation.get;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.charts.ChartAttributeTableModel;
import com.ni3.ag.adminconsole.client.view.charts.ChartTableModel;
import com.ni3.ag.adminconsole.client.view.charts.ChartTreeModel;
import com.ni3.ag.adminconsole.client.view.charts.ChartView;
import com.ni3.ag.adminconsole.client.view.common.ACForceConfirmDialog;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.ChartDisplayOperation;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ObjectChart;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ChartModel;
import com.ni3.ag.adminconsole.shared.service.def.ChartService;
import com.ni3.ag.adminconsole.validation.ACException;

public class ChartController extends AbstractController{
	private ChartView view;
	private ChartModel model;

	private UpdateObjectChartsButtonListener updateListener;

	private ChartController(){

	}

	@Override
	public ChartView getView(){
		return view;
	}

	public void setView(ChartView c){
		view = c;
	}

	public void setModel(ChartModel m){
		model = m;
	}

	@Override
	public void setView(Component c){
		view = (ChartView) c;
	}

	@Override
	public ChartModel getModel(){
		return model;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (ChartModel) m;
	}

	@Override
	public void initializeController(){
		loadData();
		super.initializeController();
		ACTree tree = (ACTree) view.getObjectTree();
		tree.setCurrentController(this);
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		ChartTreeModel treeModel = new ChartTreeModel(this.model.getSchemaMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		this.view.getObjectTree().setModel(treeModel);
		this.model.setCurrentObject(null);
		ChartTableModel ctm = new ChartTableModel(null);
		ChartAttributeTableModel catm = new ChartAttributeTableModel(null);
		ctm.addTableModelListener(new ChartTableModelListener(this));
		catm.addTableModelListener(new ChartAttributeTableModelListener(this));
		this.view.setChartTableModel(ctm);
		this.view.setChartAttributeTableModel(catm);
		updateChartTable();
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.getObjectTree().addTreeSelectionListener(new SchemaTreeSelectionListener(this));
		this.view.getAddObjectChartButton().addActionListener(new AddObjectChartButtonListener(this));
		this.view.getDeleteObjectChartButton().addActionListener(new DeleteObjectChartButtonListener(this));
		this.view.getRefreshButton().addActionListener(new RefreshObjectChartsButtonListener(this));
		updateListener = new UpdateObjectChartsButtonListener(this);
		this.view.getUpdateButton().addActionListener(updateListener);
		this.view.getDeleteChartButton().addActionListener(new DeleteChartButtonListener(this));
		this.view.getAddChartButton().addActionListener(new AddChartButtonListener(this));
		this.view.getAddChartAttributeButton().addActionListener(new AddAttributeButtonListener(this));
		this.view.getDeleteChartAttributeButton().addActionListener(new DeleteAttributeButtonListener(this));
		this.view.addChartTreeEditorChartNameListener(updateListener);

		final ObjectChartTableSelectionListener selectionListener = new ObjectChartTableSelectionListener(this);
		this.view.getChartTable().getSelectionModel().addListSelectionListener(selectionListener);
	}

	public void loadData(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			ChartService service = (ChartService) ACSpringFactory.getInstance().getBean("chartService");
			model.setSchemas(service.getSchemas());
			model.setObjectDefinitions(service.getObjectDefinitions());
		}
	}

	@Override
	public void reloadData(){
		loadData();
		populateDataToView(model, view);
	}

	void updateChartTable(){
		ChartTableModel ctm = view.getChartTableModel();
		ChartAttributeTableModel catm = view.getChartAttributeTableModel();
		List<ObjectDefinition> objects = new ArrayList<ObjectDefinition>();
		Chart ch = null;
		ObjectChart objectChart = null;
		catm.setData(null);
		catm.fireTableDataChanged();
		if (model.getCurrentObject() == null || model.isSchemaSelected()){
			ctm.setData(null);
			view.getChartPreview().updateView(null);
		} else{
			ch = (Chart) model.getCurrentObject();
			objects = getObjectsForSchema(ch.getSchema());
			ctm.setData(ch.getObjectCharts());
			view.getChartPreview().updateView(objectChart);
		}
		ctm.fireTableDataChanged();

		view.setObjectDefinitions(objects);
		if (ch != null && ch.getObjectCharts() != null && !ch.getObjectCharts().isEmpty())
			view.getChartTable().getSelectionModel().setSelectionInterval(0, 0);
	}

	void updateAttributeTable(){
		ObjectChart objectChart = model.getCurrentObjectChart();
		ChartAttributeTableModel catm = view.getChartAttributeTableModel();
		if (objectChart != null){
			catm.setData(objectChart.getChartAttributes());
			view.getChartPreview().updateView(objectChart);
			view.setAttributeComboData(objectChart.getObject() != null ? objectChart.getObject().getObjectAttributes()
			        : null);
		} else{
			catm.setData(null);
		}
		view.refreshChartAttributeTable();
	}

	private List<ObjectDefinition> getObjectsForSchema(Schema schema){
		List<ObjectDefinition> objs = new ArrayList<ObjectDefinition>();
		List<ObjectDefinition> nodes = model.getObjectDefinitions();
		for (ObjectDefinition od : nodes){
			if (od.getSchema().equals(schema))
				objs.add(od);
		}
		return objs;
	}

	public void addObjectChart(){
		Chart ch = (Chart) model.getCurrentObject();
		ObjectChart oc = new ObjectChart();
		oc.setChart(ch);
		oc.setChartType(ChartType.PIE);
		oc.setDisplayOperation(ChartDisplayOperation.SUM);
		oc.setIsValueDisplayed(false);
		oc.setLabelFontSize("Dialog,1,13");
		oc.setLabelInUse(false);
		oc.setMaxScale(new BigDecimal(ObjectChart.DEFAULT_MAX_SCALE));
		oc.setMaxValue(0);
		oc.setMinScale(new BigDecimal(ObjectChart.DEFAULT_MIN_SCALE));
		oc.setMinValue(0);
		oc.setNumberFormat("#");
		oc.setFontColor("#FFFFFF");
		oc.setChartAttributes(new ArrayList<ChartAttribute>());
		ch.getObjectCharts().add(oc);
		int index = ch.getObjectCharts().size() - 1;
		view.getChartTableModel().fireTableRowsInserted(index, index);
		view.setChartTableActiveRow(oc);
	}

	public void deleteObjectChart(){
		int index = view.getChartTable().getSelectedRow();
		if (index < 0 || index >= view.getChartTable().getRowCount())
			return;
		int modelIndex = view.getChartTable().convertRowIndexToModel(index);
		Chart ch = (Chart) model.getCurrentObject();
		ch.getObjectCharts().remove(modelIndex);
		((AbstractTableModel) view.getChartTable().getModel()).fireTableRowsDeleted(index, index);
		List<ObjectChart> objects = ch.getObjectCharts();
		if (modelIndex >= 0 && !objects.isEmpty()){
			ObjectChart next = (modelIndex < objects.size()) ? objects.get(modelIndex) : objects.get(modelIndex - 1);
			view.setChartTableActiveRow(next);
		}
	}

	public void resetCurrentObject(){
		Object o = model.getCurrentObject();
		if (o instanceof Chart){
			Chart currentChart = (Chart) model.getCurrentObject();
			for (Schema schema : model.getSchemas()){
				for (Chart ch : schema.getCharts()){
					if (currentChart.equals(ch)){
						model.setCurrentObject(ch);
						return;
					}
				}
			}
		} else if (o instanceof Schema){
			Schema currentSchema = (Schema) o;
			for (Schema schema : model.getSchemas()){
				if (schema.equals(currentSchema)){
					model.setCurrentObject(currentSchema);
					return;
				}
			}
		}
		model.setCurrentObject(null);
	}

	public void saveCurrentChart(){
		Chart ch = (Chart) model.getCurrentObject();
		ChartService service = (ChartService) ACSpringFactory.getInstance().getBean("chartService");
		service.updateChart(ch);
		reloadCurrent();
		updateChartTable();
		view.resetEditedFields();
	}

	public boolean deleteChart(){
		Chart chart = (Chart) model.getCurrentObject();
		ChartService service = (ChartService) ACSpringFactory.getInstance().getBean("chartService");

		ACForceConfirmDialog confirmDialog = new ACForceConfirmDialog(get(TextID.Delete), get(TextID.ConfirmDeleteChart),
		        get(TextID.ForceDelete));
		String action = confirmDialog.getSelectedAction();
		if (!action.equals(ACForceConfirmDialog.OK_ACTION))
			return false;
		try{
			service.deleteChart(chart, confirmDialog.isForceOption());
		} catch (ACException e){
			view.renderErrors(e.getErrors());
			return false;
		}
		return true;
	}

	public void updateTree(boolean delete){
		ChartTreeModel tModel = (ChartTreeModel) view.getObjectTree().getModel();
		tModel.setSchemas(model.getSchemaMap());
		view.getObjectTree().updateUI();
		if (delete){
			model.setCurrentObject(null);
			updateChartTable();
		}
	}

	public void addNewAttribute(){
		ObjectChart ch = model.getCurrentObjectChart();
		if (ch == null){
			return;
		}
		ChartAttribute cca = new ChartAttribute();
		cca.setRgb("#000000");
		cca.setObjectChart(ch);
		ch.getChartAttributes().add(cca);
		int index = ch.getChartAttributes().size() - 1;
		view.getChartAttributeTableModel().fireTableRowsInserted(index, index);
		view.setAttributeTableActiveRow(cca);
		updateChartPreview();
	}

	public void deleteAttribute(){
		ObjectChart ch = (ObjectChart) model.getCurrentObjectChart();
		int index = view.getAttributeTable().getSelectedRow();
		if (index < 0 || index >= view.getAttributeTable().getRowCount())
			return;
		int modelIndex = view.getAttributeTable().convertRowIndexToModel(index);

		ch.getChartAttributes().remove(modelIndex);
		((AbstractTableModel) view.getAttributeTable().getModel()).fireTableDataChanged();

		List<ChartAttribute> dynamicAttrs = ch.getChartAttributes();
		if (modelIndex >= 0 && !dynamicAttrs.isEmpty()){
			ChartAttribute next = (modelIndex < dynamicAttrs.size()) ? dynamicAttrs.get(modelIndex) : dynamicAttrs
			        .get(modelIndex - 1);
			view.setAttributeTableActiveRow(next);
		}
		updateChartPreview();
	}

	@Override
	public void clearData(){
		model.getSchemaMap().clear();
		model.clearObjectDefinitions();
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded()){
			reloadData();
			TreePath found = view.getTreeModel().findPathForEqualObject(dbInstance);
			view.getObjectTree().setSelectionPath(found);
			return false;
		}
		return true;
	}

	public void updateChartPreview(){
		ObjectChart ch = (ObjectChart) model.getCurrentObjectChart();
		view.getChartPreview().updateView(ch);
	}

	@Override
	public boolean save(){
		return updateListener.save();
	}

	@Override
	public void reloadCurrent(){
		Chart currentChart = (Chart) model.getCurrentObject();
		if (currentChart == null){
			return;
		}

		ChartService service = (ChartService) ACSpringFactory.getInstance().getBean("chartService");
		Chart newChart = service.getChart(currentChart.getId());
		currentChart.setObjectCharts(newChart.getObjectCharts());
		updateChartTable();
	}

	public Chart addChart(Chart chart){
		ChartService service = (ChartService) ACSpringFactory.getInstance().getBean("chartService");
		return service.saveChart(chart);
	}
}
