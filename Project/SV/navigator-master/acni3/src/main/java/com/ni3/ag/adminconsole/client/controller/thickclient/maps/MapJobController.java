/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobTableModel;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobTreeModel;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.MapJobView;
import com.ni3.ag.adminconsole.client.view.thickclient.maps.URLEx;
import com.ni3.ag.adminconsole.domain.MapJob;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.jobs.MapJobStatus;
import com.ni3.ag.adminconsole.shared.jobs.MapJobType;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.MapJobModel;
import com.ni3.ag.adminconsole.shared.service.def.MapJobService;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class MapJobController extends AbstractController{

	private MapJobModel model;
	private MapJobView view;
	private ACValidationRule mandatoryRule;
	private boolean rasterServerReachable;

	private static final Logger log = Logger.getLogger(MapJobController.class);

	private MapJobController(){
	}

	@Override
	public void initializeController(){
		loadModel();
		super.initializeController();
		testIsRasterServerReachable();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
		mandatoryRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("mapJobMandatoryFieldsValidationRule");
	}

	@Override
	public MapJobModel getModel(){
		return model;
	}

	@Override
	public MapJobView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (MapJobModel) m;
	}

	@Override
	public void setView(Component c){
		view = (MapJobView) c;
	}

	public void setModel(MapJobModel m){
		model = m;
	}

	public void setView(MapJobView c){
		view = c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		MapJobView aView = (MapJobView) view;
		aView.getTree().addTreeSelectionListener(new MapJobTreeSelectionListener(this));
		aView.addAddButtonActionListener(new AddMapJobListener(this));
		aView.addDeleteButtonActionListener(new DeleteMapJobListener(this));
		aView.addRefreshButtonActionListener(new RefreshMapJobListener(this));
		aView.addUpdateButtonActionListener(new UpdateMapJobListener(this));
		aView.addTableSelectionListener(new MapJobTableSelectionListener(this));
		aView.addLaunchNowButtonActionListener(new LaunchNowButtonListener(this));
		aView.addViewMapDirActionListener(new ViewMapDirButtonListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		List<MapJob> jobs = this.model.getJobs();
		if (jobs == null){
			jobs = new ArrayList<MapJob>();
		}
		MapJobTableModel tableModel = new MapJobTableModel(jobs, this.model.getUserZooms());
		this.view.setTableModel(tableModel);
	}

	private void updateTreeModel(){
		MapJobTreeModel treeModel = new MapJobTreeModel(SessionData.getInstance().getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
	}

	public void refreshTableModel(){
		List<MapJob> jobs = this.model.getJobs();
		if (jobs == null){
			jobs = new ArrayList<MapJob>();
		}
		view.getTableModel().setData(jobs, this.model.getUserZooms());
		view.refreshTable();
		view.setUserComboData(model.getUsers());
	}

	private void loadModel(){
		SessionData sd = SessionData.getInstance();
		DatabaseInstance instance = sd.getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			MapJobService service = ACSpringFactory.getInstance().getMapJobService();
			List<MapJob> jobs = service.getMapJobs();
			model.setJobs(jobs);
			model.setUsers(service.getUsers());
			model.clearDeletedJobs();
			String mapIdStr = service.getSetting(sd.getUserId(), Setting.GIS_SECTION, Setting.DEFAULT_MAP_ID_PROPERTY);
			model.setMap(service.getMap(Integer.parseInt(mapIdStr)));
			String rasterServer = service.getRasterServerUrl();
			model.setRasterServer(rasterServer);
			log.debug("loaded jobs: size = " + ((jobs != null) ? jobs.size() : 0));
		}
	}

	public boolean isRasterServerReachable(){
		return rasterServerReachable;
	}

	private void testIsRasterServerReachable(){
		com.ni3.ag.adminconsole.domain.Map map = model.getMap();
		String rasterServer = model.getRasterServer();
		boolean unreachable = map == null || rasterServer == null || rasterServer.trim().isEmpty();
		if (!unreachable){
			URLEx url = new URLEx(rasterServer + "/GetArea?MapID=" + map.getId() + "&MapLimits=T");
			unreachable = url.getConnection() == null || !url.HTTPRead();
		}
		if (unreachable){
			view.setGisPanel(view.getEmptyPanel());
		} else{
			view.setGisPanel(view.getGisPanel());
		}
		rasterServerReachable = !unreachable;
	}

	@Override
	public void clearData(){
		model.getJobMap().clear();
		model.getUserMap().clear();
		model.clearDeletedJobs();
		view.clearErrors();
	}

	@Override
	public void reloadCurrent(){
		loadModel();
		testIsRasterServerReachable();
	}

	@Override
	public void reloadData(){
		loadModel();
		testIsRasterServerReachable();
		updateTreeModel();
		refreshTableModel();
	}

	@Override
	public boolean save(){
		return applyMapJobs();
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
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

	public void addNewMapJob(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		MapJob job = new MapJob();
		job.setStatus(MapJobStatus.Scheduled.getValue());
		job.setJobType(MapJobType.MapExtraction.getValue());
		job.setTriggeredBy(SessionData.getInstance().getUser());
		if (model.getJobs() == null){
			model.setJobs(new ArrayList<MapJob>());
		}
		model.getJobs().add(job);
		model.setCurrentJob(job);
		log.debug("Added new map job");
		int index = model.getJobs().size() - 1;
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(job);
	}

	public void deleteMapJob(MapJob jobToDelete){
		List<MapJob> jobs = model.getJobs();
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || jobs == null){
			return;
		}
		int index = -1;
		if (jobToDelete != null){
			index = jobs.indexOf(jobToDelete);
			if (index >= 0 && jobToDelete.getId() != null){
				model.getDeletedJobs().add(jobToDelete);
			}
			jobs.remove(jobToDelete);
			log.debug("Removed map job: " + jobToDelete);
		}
		view.getTableModel().fireTableRowsDeleted(index, index);
		if (index >= 0 && jobs.size() > 0){
			MapJob next = (index < jobs.size()) ? jobs.get(index) : jobs.get(index - 1);
			view.setActiveTableRow(next);
		}

	}

	public boolean checkMandatoryRule(){
		return mandatoryRule.performCheck(model);
	}

	public void renderMandatoryRuleErrors(){
		view.renderErrors(mandatoryRule.getErrorEntries());
	}

	public boolean applyMapJobs(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || !testModulesPath()){
			return false;
		}

		view.stopCellEditing();
		view.clearErrors();

		if (!checkMandatoryRule()){
			renderMandatoryRuleErrors();
			return false;
		}

		List<MapJob> jobsToUpdate = model.getJobs();
		List<MapJob> jobsToDelete = model.getDeletedJobs();
		MapJobService service = ACSpringFactory.getInstance().getMapJobService();
		try{
			service.applyMapJobs(jobsToUpdate, jobsToDelete);
		} catch (ACException e){
			view.renderErrors(e.getErrors());
		}

		return true;
	}

	public boolean testModulesPath(){
		final VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		final boolean testOk = service.testModulesPath();
		if (!testOk){
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgPathToOfflineModulesNotConfigured));
			view.renderErrors(errors);
		}
		return testOk;
	}
}
