/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.diag;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.diag.DiagnosticsView;
import com.ni3.ag.adminconsole.client.view.diag.SchemaTreeModel;
import com.ni3.ag.adminconsole.client.view.diag.TaskTableModel;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.DiagnosticsModel;
import com.ni3.ag.adminconsole.shared.service.def.DiagnosticsService;
import com.ni3.ag.adminconsole.validation.DiagnoseTaskResult;

public class DiagnosticsController extends AbstractController{
	private DiagnosticsView view;
	private DiagnosticsModel model;

	@Override
	public void initializeController(){
		loadData();
		model.setDatabaseInstances(SessionData.getInstance().getConnectedDatabaseInstances());
		super.initializeController();
		view.setTreeController(this);
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		updateTableData(null);
	}

	public void updateTableData(){
		Schema schema = model.getCurrentSchema();
		updateTableData(schema != null ? getTasks() : null);
	}

	public void clearTaskResults(){
		List<DiagnoseTaskResult> tasks = getTasks();
		for (DiagnoseTaskResult task : tasks){
			view.updateTableModel(task);
		}
		view.updateTableForce();
	}

	public void updateTableData(List<DiagnoseTaskResult> results){
		if (results == null)
			results = new ArrayList<DiagnoseTaskResult>();
		model.setCurrentResults(results);
		view.setTableModel(new TaskTableModel(results, new FixButtonActionListener(this)));
		view.setTableWidths(new int[] { 300, 50, 50, 300 });
	}

	private void updateTreeModel(){
		view.setTreeModel(new SchemaTreeModel(model.getDatabaseInstances(), model.getSchemasMap()));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		this.view.addTreeListener(new SchemasTreeListener(this));
		this.view.addDiagnoseButtonListener(new DiagnoseButtonListener(this));
	}

	@Override
	public DiagnosticsView getView(){
		return view;
	}

	@Override
	public void setView(Component c){
		view = (DiagnosticsView) c;
	}

	public void setView(DiagnosticsView dv){
		view = dv;
	}

	@Override
	public AbstractModel getModel(){
		return model;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (DiagnosticsModel) m;
	}

	public void setModel(DiagnosticsModel m){
		model = m;
	}

	@Override
	public void reloadData(){
		loadData();
		view.updateTree();
	}

	private void loadData(){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		DiagnosticsService service = ACSpringFactory.getInstance().getDiagnosticsService();
		for (DatabaseInstance cdb : SessionData.getInstance().getConnectedDatabaseInstances()){
			if (cdb.isConnected()){
				SessionData.getInstance().setCurrentDatabaseInstance(cdb);
				List<Schema> schemas = service.getSchemas();
				model.getSchemasMap().put(cdb, schemas);
			} else
				model.getSchemasMap().put(cdb, new ArrayList<Schema>());
		}
		SessionData.getInstance().setCurrentDatabaseInstance(db);
	}

	protected List<DiagnoseTaskResult> getTasks(){
		Schema schema = model.getCurrentSchema();
		if (schema == null)
			return new ArrayList<DiagnoseTaskResult>();
		DiagnosticsService service = ACSpringFactory.getInstance().getDiagnosticsService();
		List<DiagnoseTaskResult> tasks = service.getInitialTaskResults(schema);
		return tasks;
	}

	@Override
	public void clearData(){
	}

	@Override
	public boolean save(){
		return true;
	}

	@Override
	public void reloadCurrent(){
	}

}
