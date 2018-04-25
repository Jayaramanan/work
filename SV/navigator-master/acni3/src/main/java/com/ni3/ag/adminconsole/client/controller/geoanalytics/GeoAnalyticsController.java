/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.geoanalytics;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GeoAnalyticsTreeModel;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GeoAnalyticsView;
import com.ni3.ag.adminconsole.client.view.geoanalytics.GisTerritoryTableModel;
import com.ni3.ag.adminconsole.domain.GisTerritory;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.GeoAnalyticsModel;
import com.ni3.ag.adminconsole.shared.service.def.GeoAnalyticsService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class GeoAnalyticsController extends AbstractController{

	private GeoAnalyticsModel model;
	private GeoAnalyticsView view;
	private ACValidationRule[] rules;

	private static final Logger log = Logger.getLogger(GeoAnalyticsController.class);

	private GeoAnalyticsController(){
	}

	@Override
	public void initializeController(){
		loadModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
		rules = new ACValidationRule[] { (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "gisTerritoryMandatoryFieldRule") };
	}

	@Override
	public GeoAnalyticsModel getModel(){
		return model;
	}

	@Override
	public GeoAnalyticsView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (GeoAnalyticsModel) m;
	}

	@Override
	public void setView(Component c){
		view = (GeoAnalyticsView) c;
	}

	public void setModel(GeoAnalyticsModel m){
		model = m;
	}

	public void setView(GeoAnalyticsView c){
		view = c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		GeoAnalyticsView aView = (GeoAnalyticsView) view;
		aView.getTree().setCurrentController(this);
		aView.addTreeSelectionListener(new GeoAnalyticsTreeSelectionListener(this));
		aView.addDeleteButtonActionListener(new DeleteGisTerritoryListener(this));
		aView.addAddButtonActionListener(new AddGisTerritoryListener(this));
		aView.addRefreshButtonActionListener(new RefreshGisTerritoryListener(this));
		aView.addUpdateButtonActionListener(new UpdateGisTerritoryListener(this));

	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		List<GisTerritory> territories = getModel().getGisTerritories();
		if (territories == null){
			territories = new ArrayList<GisTerritory>();
		}
		GisTerritoryTableModel tableModel = new GisTerritoryTableModel(territories);
		getView().setTableModel(tableModel);
	}

	private void updateTreeModel(){
		GeoAnalyticsTreeModel treeModel = new GeoAnalyticsTreeModel(SessionData.getInstance()
		        .getConnectedDatabaseInstances(), this.model.getSchemaMap());
		getView().setTreeModel(treeModel);
	}

	public void refreshTableModel(){
		List<GisTerritory> territories = model.getGisTerritories();
		if (territories == null){
			territories = new ArrayList<GisTerritory>();
		}

		view.getTableModel().setData(territories);
		view.getTableModel().fireTableDataChanged();
		view.setTerritoryComboData(territories);
	}

	private void loadModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			GeoAnalyticsService service = ACSpringFactory.getInstance().getGeoAnalyticsService();
			List<GisTerritory> territories = service.getGisTerritories();
			model.setGisTerritories(territories);
			model.setSchemas(service.getSchemas());
			model.clearDeletedTerritories();
			model.setCurrentTerritory(null);
			log.debug("loaded territories: size = " + territories != null ? territories.size() : 0);
		}
	}

	@Override
	public void clearData(){
		model.getGisTerritoryMap().clear();
		model.clearDeletedTerritories();
		model.getSchemaMap().clear();
	}

	@Override
	public void reloadCurrent(){
		view.clearErrors();
		loadModel();
	}

	@Override
	public void reloadData(){
		view.clearErrors();
		loadModel();
		updateTreeModel();
		refreshTableModel();
	}

	@Override
	public boolean save(){
		return applyTerritories();
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null)
			return false;
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			AbstractTreeModel treeModel = view.getTreeModel();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, treeModel);
			view.getTree().setSelectionPath(found);
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}

	public void addNewTerritory(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		GisTerritory territory = new GisTerritory();
		territory.setVersion(1);
		if (model.getGisTerritories() == null){
			model.setGisTerritories(new ArrayList<GisTerritory>());
		}
		model.getGisTerritories().add(territory);
		model.setCurrentTerritory(territory);
		territory.setSort(getNextSort());
		log.debug("Added new gis territory");
		int index = model.getGisTerritories().size() - 1;
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(territory);
	}

	private Integer getNextSort(){
		int next = 0;
		List<GisTerritory> territories = model.getGisTerritories();
		for (GisTerritory gt : territories){
			if (gt.getSort() != null && gt.getSort() >= next){
				next = gt.getSort() + 1;
			}
		}
		return null;
	}

	public void deleteTerritory(GisTerritory territoryToDelete){
		List<GisTerritory> territories = model.getGisTerritories();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || territories == null){
			return;
		}
		int index = -1;
		if (territoryToDelete != null){
			index = territories.indexOf(territoryToDelete);
			if (index >= 0 && territoryToDelete.getId() != null){
				model.getDeletedTerritories().add(territoryToDelete);
			}
			territories.remove(territoryToDelete);
			log.debug("Removed gis territory: " + territoryToDelete);
		}
		view.getTableModel().fireTableRowsDeleted(index, index);
		view.refreshTable();
		if (index >= 0 && territories.size() > 0){
			GisTerritory next = (index < territories.size()) ? territories.get(index) : territories.get(index - 1);
			view.setActiveTableRow(next);
		}
		view.setTerritoryComboData(model.getGisTerritories());
	}

	public boolean applyTerritories(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.stopCellEditing();
		view.clearErrors();

		for (ACValidationRule rule : rules)
			if (!rule.performCheck(model)){
				view.renderErrors(rule.getErrorEntries());
				return false;
			}

		List<GisTerritory> territoriesToUpdate = model.getGisTerritories();
		List<GisTerritory> territoriesToDelete = model.getDeletedTerritories();
		GeoAnalyticsService service = ACSpringFactory.getInstance().getGeoAnalyticsService();
		service.applyGisTerritories(territoriesToUpdate, territoriesToDelete);

		return true;
	}
}
