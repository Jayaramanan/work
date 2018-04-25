/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

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
import com.ni3.ag.adminconsole.client.view.thickclient.OfflineJobTableModel;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientTreeModel;
import com.ni3.ag.adminconsole.client.view.thickclient.ThickClientView;
import com.ni3.ag.adminconsole.domain.OfflineJob;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.jobs.JobType;
import com.ni3.ag.adminconsole.shared.jobs.OfflineJobStatus;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ThickClientModel;
import com.ni3.ag.adminconsole.shared.service.def.ThickClientJobService;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ThickClientController extends AbstractController{
	private static final Logger log = Logger.getLogger(ThickClientController.class);
	private ThickClientModel model;
	private ThickClientView view;

	private ACValidationRule updateJobValidationRule;

	private ThickClientController(){
	}

	@Override
	public void initializeController(){
		loadModel();
		super.initializeController();
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
		this.updateJobValidationRule = (ACValidationRule) ACSpringFactory.getInstance().getBean(
		        "OfflineClientExportJobValidationRule");
	}

	@Override
	public ThickClientModel getModel(){
		return model;
	}

	@Override
	public ThickClientView getView(){
		return view;
	}

	public void setModel(ThickClientModel m){
		model = m;
	}

	public void setView(ThickClientView c){
		view = c;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (ThickClientModel) m;
	}

	@Override
	public void setView(Component c){
		view = (ThickClientView) c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		ThickClientView aView = (ThickClientView) view;
		aView.getTree().addTreeSelectionListener(new ThickClientTreeSelectionListener(this));
		aView.addAddButtonActionListener(new AddOfflineJobListener(this));
		aView.addDeleteButtonActionListener(new DeleteOfflineJobListener(this));
		aView.addRefreshButtonActionListener(new RefreshOfflineJobListener(this));
		aView.addUpdateButtonActionListener(new UpdateOfflineJobListener(this));
		aView.addPreviewButtonActionListener(new PreviewOfflineJobListener(this));
		aView.addLaunchNowButtonActionListener(new LaunchOfflineJobNowListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		List<OfflineJob> jobs = getModel().getJobs();
		if (jobs == null){
			jobs = new ArrayList<OfflineJob>();
		}
		OfflineJobTableModel tableModel = new OfflineJobTableModel(jobs, getModel().getGroups());
		getView().setUserSelectionEditorGroups(getModel().getGroups());
		getView().setTableModel(tableModel);
	}

	private void updateTreeModel(){
		ThickClientTreeModel treeModel = new ThickClientTreeModel(SessionData.getInstance().getConnectedDatabaseInstances());
		getView().setTreeModel(treeModel);
	}

	public void refreshTableModel(){
		List<OfflineJob> jobs = model.getJobs();
		if (jobs == null){
			jobs = new ArrayList<OfflineJob>();
		}
		view.getTableModel().setData(jobs, getModel().getGroups());
		view.getTableModel().fireTableDataChanged();
		view.setUserSelectionEditorGroups(model.getGroups());
	}

	private void loadModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			ThickClientJobService service = ACSpringFactory.getInstance().getThickClientService();
			List<OfflineJob> jobs = service.getOfflineJobs();
			model.setJobs(jobs);
			model.setGroups(service.getGroupsWithOfflineUsers());
			model.clearDeletedJobs();
			log.debug("loaded jobs: size = " + jobs != null ? jobs.size() : 0);
		}
	}

	@Override
	public void clearData(){
		model.getJobMap().clear();
		model.getGroupMap().clear();
		model.clearDeletedJobs();
	}

	@Override
	public void reloadCurrent(){
		loadModel();
	}

	@Override
	public void reloadData(){
		loadModel();
		refreshTableModel();
		updateTreeModel();
	}

	@Override
	public boolean save(){
		return applyOfflineJobs();
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

	public void addNewOfflineJob(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return;
		}
		OfflineJob job = new OfflineJob();
		job.setStatus(OfflineJobStatus.Scheduled.getValue());
		job.setJobType(JobType.Export.getValue());
		job.setTriggeredBy(SessionData.getInstance().getUser());
		if (model.getJobs() == null){
			model.setJobs(new ArrayList<OfflineJob>());
		}
		model.getJobs().add(job);
		model.setCurrentJob(job);
		log.debug("Added new offline job");
		int index = model.getJobs().size() - 1;
		view.getTableModel().fireTableRowsInserted(index, index);
		view.setActiveTableRow(job);
	}

	public void deleteOfflineJob(OfflineJob jobToDelete){
		List<OfflineJob> jobs = model.getJobs();
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
			log.debug("Removed offline job: " + jobToDelete);
		}
		view.getTableModel().fireTableRowsDeleted(index, index);
		if (index >= 0 && jobs.size() > 0){
			OfflineJob next = (index < jobs.size()) ? jobs.get(index) : jobs.get(index - 1);
			view.setActiveTableRow(next);
		}

	}

	public boolean applyOfflineJobs(){
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected() || !testModulesPath()){
			return false;
		}

		view.stopCellEditing();
		view.clearErrors();

		if (!updateJobValidationRule.performCheck(model)){
			view.renderErrors(updateJobValidationRule.getErrorEntries());
			return false;
		}

		List<OfflineJob> jobsToUpdate = model.getJobs();
		List<OfflineJob> jobsToDelete = model.getDeletedJobs();
		ThickClientJobService service = ACSpringFactory.getInstance().getThickClientService();
		try{
			service.applyOfflineJobs(jobsToUpdate, jobsToDelete);
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
