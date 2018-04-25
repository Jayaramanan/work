/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.vers;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.UserModuleTableModel;
import com.ni3.ag.adminconsole.client.view.thickclient.vers.VersioningView;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.VersioningModel;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;

public class VersioningController extends AbstractController{
	private static final Logger log = Logger.getLogger(VersioningController.class);
	private VersioningModel model;
	private VersioningView view;
	private SaveModuleButtonListener saveModuleListener;
	private SaveUserModuleButtonListner saveUserModuleListener;
	private String modulesTransferServletUrl;

	public void setModulesTransferServletUrl(String modulesTransferServletUrl){
		this.modulesTransferServletUrl = modulesTransferServletUrl;
	}

	public String getModulesTransferServletUrl(){
		return modulesTransferServletUrl;
	}

	@Override
	public void initializeController(){
		loadVersionsData();
		reloadPaths();
		super.initializeController();
	}

	private void loadVersionsData(){
		DatabaseInstance db = SessionData.getInstance().getCurrentDatabaseInstance();
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		if (db != null && db.isConnected()){
			model.setGroups(filterGroups(service.getGroups()));
			model.setModules(service.getModules());
		} else{
			model.setGroups(new ArrayList<Group>());
			model.setModules(new ArrayList<Module>());
		}
	}

	protected List<Group> filterGroups(List<Group> groups){
		List<Group> filtered = new ArrayList<Group>();
		for (Group g : groups){
			List<User> users = g.getUsers();
			g.setUsers(new ArrayList<User>());
			for (User u : users){
				if (u.getHasOfflineClient())
					g.getUsers().add(u);
			}
			if (g.getUsers().size() > 0)
				filtered.add(g);
		}
		return filtered;
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		updateTreeModel();
		updateModuleTable();
		getView().setUserModuleTableModel(new UserModuleTableModel(getAllUsers(), getModel().getModules()));
	}

	private void updateTreeModel(){
		List<DatabaseInstance> dbInstances = SessionData.getInstance().getConnectedDatabaseInstances();
		VersioningGroupsTreeModel treeModel = new VersioningGroupsTreeModel(getModel().getGroupMap(), dbInstances);
		getView().setGroupTreeModel(treeModel);
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		saveModuleListener = new SaveModuleButtonListener(this);
		saveUserModuleListener = new SaveUserModuleButtonListner(this);
		this.view.setGroupTreeListener(new VersioningGroupTreeListener(this));
		this.view.getGroupTree().setCurrentController(this);
		this.view.setAddModuleButtonListener(new AddModuleButtonListener(this));
		this.view.setDeleteModuleButtonListener(new DeleteModuleButtonListener(this));
		this.view.setRefreshModuleButtonListener(new RefreshModuleButtonListener(this));
		this.view.setSaveModuleButtonListener(saveModuleListener);
		this.view.setUserModuleRefreshButtonListener(new RefreshUserModuleButtonListener(this));
		this.view.setUserModuleSaveButtonListener(saveUserModuleListener);
		this.view.addUploadButtonListener(new UploadModuleButtonListener(this));
		this.view.addDownloadButtonListener(new DownloadModuleButtonListener(this));
		this.view.addSendButtonListener(new SendButtonListener(this, false));
		this.view.addSendSSOButtonListener(new SendButtonListener(this, true));
	}

	@Override
	public VersioningView getView(){
		return view;
	}

	@Override
	public void setView(Component c){
		view = (VersioningView) c;
	}

	public void setView(VersioningView view){
		this.view = view;
	}

	@Override
	public VersioningModel getModel(){
		return model;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (VersioningModel) m;
	}

	public void setModel(VersioningModel model){
		this.model = model;
	}

	@Override
	public void reloadData(){
		model.setCurrentDatabaseInstance(SessionData.getInstance().getCurrentDatabaseInstance());
		loadVersionsData();
		reloadPaths();
		updateTreeModel();
		updateModuleTable();
	}

	@Override
	public void clearData(){
		model.getGroupMap().clear();
		model.getModuleMap().clear();
		model.getPathMap().clear();
	}

	@Override
	public boolean save(){
		return saveModuleListener.save() && saveUserModuleListener.save();
	}

	@Override
	public void reloadCurrent(){
		reloadData();
	}

	public void updateViewPaths(){
		List<String> paths = this.model.getPaths();
		getView().updatePaths(paths != null ? paths : new ArrayList<String>());
	}

	public void updateModuleTable(){
		List<Module> modules = this.model.getModules();
		List<String> paths = this.model.getPaths();
		getView().setModuleTableModelData(modules, model.getCurrentGroups(), paths);
		getView().resetEditedFields();
		checkModulesPath();
	}

	public void updateUserModuleTable(){
		UserModuleTableModel tModel = view.getUserModuleTableModel();
		Group current = model.getCurrentGroup();
		if (current == null)
			tModel.setData(getAllUsers(), model.getModules());
		else
			tModel.setData(current.getUsers(), model.getModules());
		tModel.fireTableDataChanged();
	}

	public boolean checkModulesPath(){
		boolean result = true;
		if (model.getPaths() == null){
			view.renderError(TextID.MsgPathToOfflineModulesNotConfigured);
			result = false;
		}
		return result;
	}

	public List<User> getAllUsers(){
		List<User> result = new ArrayList<User>();
		List<Group> groups = model.getGroups();
		if (groups == null)
			return result;
		for (Group g : groups)
			if (g.getUsers() != null)
				result.addAll(g.getUsers());
		return result;
	}

	public void reloadModules(){
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		model.setModules(service.getModules());
	}

	public void reloadUserModules(){
		VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		model.setGroups(filterGroups(service.getGroups()));
	}

	public void reloadPaths(){
		final VersioningService service = ACSpringFactory.getInstance().getVersioningService();
		final boolean testOk = service.testModulesPath();
		if (!testOk){
			model.setPaths(null);
		} else{
			final List<String> fileNames = service.getFileNames();
			model.setPaths(fileNames);
		}
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			view.restoreSelection();
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}
}
