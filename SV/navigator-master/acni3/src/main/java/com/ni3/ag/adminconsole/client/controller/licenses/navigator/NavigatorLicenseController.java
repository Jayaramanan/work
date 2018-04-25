/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.licenses.navigator;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.ModuleDescription;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.NavigatorLicenseTreeModel;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.NavigatorLicenseView;
import com.ni3.ag.adminconsole.client.view.licenses.navigator.UserEditionTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.license.NavigatorModule;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.NavigatorLicenseModel;
import com.ni3.ag.adminconsole.shared.service.def.NavigatorLicenseService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class NavigatorLicenseController extends AbstractController{
	private final static Logger log = Logger.getLogger(NavigatorLicenseController.class);

	private NavigatorLicenseView view;
	private NavigatorLicenseModel model;
	private ACValidationRule rule;
	private List<ErrorEntry> loadErrors;
	private Map<NavigatorModule, Integer> actualHighlightCountMap = new HashMap<NavigatorModule, Integer>();

	@Override
	public void clearData(){
		view.resetEditedFields();
	}

	public void initializeController(){
		loadModel();
		super.initializeController();
		if (loadErrors != null && !loadErrors.isEmpty())
			view.renderErrors(loadErrors);
		ACTree tree = (ACTree) view.getTree();
		tree.setCurrentController(this);
		rule = (ACValidationRule) ACSpringFactory.getInstance().getBean("navigatorLicenseCountValidationRule");
	}

	void loadModel(){
		DatabaseInstance currentDB = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(currentDB);
		loadErrors = new ArrayList<ErrorEntry>();
		if (currentDB != null && currentDB.isConnected()){
			NavigatorLicenseService licenseService = ACSpringFactory.getInstance().getNavigatorLicenseService();
			actualHighlightCountMap.clear();
			List<Group> groups = licenseService.getGroups();

			List<LicenseData> lData = licenseService.getNavigatorLicenseData();
			model.setLicenseData(lData);
			ObjectVisibilityStore instance = ObjectVisibilityStore.getInstance();
			instance.refreshLicenses(currentDB);
			model.setGroups(groups);

			if (lData != null)
				if (!isLicenseDataValid())
					loadErrors.add(new ErrorEntry(TextID.MsgInvalidLicense));
				else
					actualHighlightCountMap = licenseService.checkExpiringLicenseModules();
		}

	}

	private boolean isLicenseDataValid(){
		List<LicenseData> lData = model.getLicenseData();
		if (lData == null)
			return false;
		for (LicenseData ld : lData)
			if (ld.isValid())
				return true;
		return false;
	}

	@Override
	public AbstractModel getModel(){
		return model;
	}

	@Override
	public NavigatorLicenseView getView(){
		return view;
	}

	public void setView(NavigatorLicenseView view){
		this.view = view;
	}

	@Override
	public void setView(Component c){
		view = (NavigatorLicenseView) c;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component v){
		view.getTree().addTreeSelectionListener(new NavigatorLicenseTreeSelectionListener(this));
		view.getRefreshButton().addActionListener(new RefreshUserEditionListener(this));
		view.getUpdateButton().addActionListener(new UpdateUserEditionListener(this));
		view.addTableCellSelectionListener(new UserEditionTableCellSelectionListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){
	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		NavigatorLicenseTreeModel treeModel = new NavigatorLicenseTreeModel(this.model.getGroupMap(), SessionData
		        .getInstance().getConnectedDatabaseInstances());
		this.view.setNavigatorLicenseTreeModel(treeModel);
		UserEditionTableModel tableModel = new UserEditionTableModel(new ArrayList<User>(), new ArrayList<User>(),
		        new ArrayList<ModuleDescription>());
		this.view.setTableModel(tableModel);
	}

	@Override
	public void reloadCurrent(){
		if (model.isGroupSelected()){
			reloadCurrentGroup();
		} else if (model.isInstanceSelected()){
			reloadAllGroups();
		}
	}

	private void reloadCurrentGroup(){
		Group currentGroup = (Group) model.getCurrentObject();
		if (currentGroup == null){
			return;
		}
		NavigatorLicenseService service = ACSpringFactory.getInstance().getNavigatorLicenseService();
		Group newGroup = service.reloadGroup(currentGroup.getId());
		currentGroup.setUsers(newGroup.getUsers());
		for (User us : currentGroup.getUsers()){
			us.getGroups().clear();
			us.getGroups().add(currentGroup);
		}
	}

	private void reloadAllGroups(){
		NavigatorLicenseService service = ACSpringFactory.getInstance().getNavigatorLicenseService();
		model.setGroups(service.getGroups());
	}

	@Override
	public void reloadData(){
		loadModel();
		updateTreeModel();
		refreshTableModel(true);
		if (loadErrors != null && !loadErrors.isEmpty())
			view.renderErrors(loadErrors);
	}

	void updateTreeModel(){
		((NavigatorLicenseTreeModel) view.getTreeModel()).setData(this.model.getGroupMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		view.updateTree();
	}

	@Override
	public boolean save(){
		return applyChanges();
	}

	@Override
	public void setModel(AbstractModel m){
		this.model = (NavigatorLicenseModel) m;
	}

	public void refreshTableModel(boolean updateColumns){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		view.getTableModel().setData(new ArrayList<User>(), new ArrayList<User>(), new ArrayList<ModuleDescription>(),
		        updateColumns);

		if (loadErrors != null && !loadErrors.isEmpty()){
			view.renderErrors(loadErrors);
		} else if (dbInstance != null && dbInstance.isConnected() && model.isInstanceLoaded(dbInstance)){
			List<User> users = getCurrentUsers();
			List<ModuleDescription> editions = getEditions();
			view.getTableModel().setData(users, getAllUsers(), editions, updateColumns);
		}

		view.getTableModel().updateUsedLicenseCount();
		if (updateColumns){

			view.getTableModel().fireTableStructureChanged();
			view.updateTableHeaders();
		} else{
			view.getTableModel().fireTableDataChanged();
		}
	}

	private List<ModuleDescription> getEditions(){
		List<LicenseData> lDataList = model.getLicenseData();
		if (lDataList == null)
			return new ArrayList<ModuleDescription>();
		List<ModuleDescription> modules = new ArrayList<ModuleDescription>();
		for (NavigatorModule module : NavigatorModule.values()){
			String strModule = module.getValue().toString();
			int userCount = 0;
			boolean contains = false;
			for (LicenseData lData : lDataList)
				if (lData.containsKey(strModule) && lData.isValid()){
					contains = true;
					Integer count = (Integer) lData.get(strModule);
					if (count != null)
						userCount += count.intValue();
				}
			if (contains)
				modules.add(new ModuleDescription(module, userCount));
		}

		return modules;
	}

	private List<User> getCurrentUsers(){
		List<User> users = null;
		if (model.isGroupSelected()){
			Group group = (Group) model.getCurrentObject();
			users = group.getUsers();
		} else if (model.isInstanceSelected()){
			users = getAllUsers();
		}
		model.setCurrentTableData(users);
		return users;
	}

	private List<User> getAllUsers(){
		List<User> users = new ArrayList<User>();
		for (Group group : model.getGroups()){
			users.addAll(group.getUsers());
		}
		return users;
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getTreeModel());
			view.getTree().setSelectionPath(found);
			return false;
		}
		return true;
	}

	public boolean applyChanges(){
		log.debug("applying changes");
		DatabaseInstance dbInstance = model.getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.stopCellEditing();
		view.clearErrors();

		if (!rule.performCheck(model)){
			view.renderErrors(rule.getErrorEntries());
			return false;
		}

		List<Group> groups = null;
		if (model.isGroupSelected()){
			groups = new ArrayList<Group>();
			groups.add((Group) model.getCurrentObject());
		} else if (model.isInstanceSelected()){
			groups = model.getGroups();
		}

		NavigatorLicenseService service = ACSpringFactory.getInstance().getNavigatorLicenseService();
		service.updateGroups(groups);

		return true;
	}

	public void resetChanges(){

	}

	protected Integer getMaximumCellsMarkedForExpiryCount(String moduleName){
		NavigatorModule nModule = null;
		for (NavigatorModule module : NavigatorModule.values()){
			if (module.getValue().equals(moduleName)){
				nModule = module;
				break;
			}
		}
		if (nModule == null)
			return new Integer(0);
		return actualHighlightCountMap.get(nModule);
	}
}
