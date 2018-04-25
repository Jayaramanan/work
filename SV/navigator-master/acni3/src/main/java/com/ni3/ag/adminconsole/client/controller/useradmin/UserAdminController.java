/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import static com.ni3.ag.adminconsole.client.view.Translation.get;
import static com.ni3.ag.adminconsole.shared.language.TextID.NewUser;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTree;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.ScopePanel;
import com.ni3.ag.adminconsole.client.view.useradmin.SeqRangeTableModel;
import com.ni3.ag.adminconsole.client.view.useradmin.ThickClientPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.ThickClientTableModel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminLeftPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminTreeModel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.UserPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.UserTableModel;
import com.ni3.ag.adminconsole.client.view.useradmin.charts.ChartPanel;
import com.ni3.ag.adminconsole.client.view.useradmin.charts.ChartPrivilegesTreeTableModel;
import com.ni3.ag.adminconsole.client.view.useradmin.privileges.GroupPrivilegesTreeTableModel;
import com.ni3.ag.adminconsole.client.view.useradmin.privileges.PrivilegesPanel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModelDataRequestListener;
import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserAdminController extends AbstractController{
	private UserAdminModel model;

	private UserAdminView view;

	private static final Logger log = Logger.getLogger(UserAdminController.class);

	private UpdateUserActionListener updateUserListener;
	private UpdateGroupPrivilegesActionListener updateObjectListener;
	private UpdateGroupScopeActionListener updateScopeListener;
	private UpdateChartGroupsListener updateChartsListener;
	private ConfigLockedCheckBoxListener configLockedListener;

	private PasswordEncoder passwordEncoder;

	public void setPasswordEncoder(PasswordEncoder passwordEncoder){
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordEncoder getPasswordEncoder(){
		return passwordEncoder;
	}

	@Override
	public void initializeController(){
		model.setListener(new UserAdminModelDataRequestListenerImpl());
		loadModel();
		super.initializeController();
		view.getUserPanel().setTableModel(new UserTableModel(null));
		ACTree tree = (ACTree) view.getLeftPanel().getTree();
		tree.setCurrentController(this);
	}

	@Override
	public UserAdminModel getModel(){
		return model;
	}

	@Override
	public UserAdminView getView(){
		return view;
	}

	@Override
	public void setModel(AbstractModel m){
		model = (UserAdminModel) m;
	}

	@Override
	public void setView(Component c){
		view = (UserAdminView) c;
	}

	public void setModel(UserAdminModel m){
		model = m;
	}

	public void setView(UserAdminView v){
		view = v;
	}

	@Override
	protected void initializeListeners(AbstractModel model, Component view){
		UserAdminLeftPanel leftPanel = getView().getLeftPanel();
		leftPanel.addTreeSelectionListener(new UserAdminTreeSelectionListener(this));
		leftPanel.addAddGroupButtonListener(new AddGroupButtonListener(this));
		leftPanel.addDeleteGroupButtonListener(new DeleteGroupButtonListener(this));
        leftPanel.addCopyGroupButtonListener(new CopyGroupButtonListener(this));
        UserPanel userPanel = getView().getUserPanel();
		userPanel.addAddButtonActionListener(new AddUserActionListener(this));
		userPanel.addDeleteButtonActionListener(new DeleteUserActionListener(this));
		userPanel.addRefreshButtonActionListener(new RefreshUserActionListener(this));
		updateUserListener = new UpdateUserActionListener(this);
		userPanel.addUpdateButtonActionListener(updateUserListener);
		userPanel.addCopyButtonActionListener(new CopyUserActionListener(this));
		userPanel.addResetPasswordButtonListener(new ResetPasswordButtonListener(this));
		PrivilegesPanel privilegesPanel = getView().getPrivilegesPanel();
		updateObjectListener = new UpdateGroupPrivilegesActionListener(this);
		privilegesPanel.addUpdateButtonActionListener(updateObjectListener);
		privilegesPanel.addRefreshButtonActionListener(new RefreshGroupPrivilegesActionListener(this));
		privilegesPanel.addPrivilegesButtonActionListener(new GrantAllPrivilegesActionListener(this));
		privilegesPanel.addTreeExpansionListener(new PrivilegesTreeExpansionListener(this));
		configLockedListener = new ConfigLockedCheckBoxListener(this);
		privilegesPanel.addConfigLockedCheckboxListener(configLockedListener);
		updateScopeListener = new UpdateGroupScopeActionListener(this);
		ScopePanel scopePanel = getView().getScopePanel();
		scopePanel.addUpdateGroupScopeButtonListener(updateScopeListener);
		scopePanel.addRefreshGroupScopeButtonListener(new RefreshGroupScopeActionListener(this));
		ThickClientPanel thickClientPanel = getView().getThickClientPanel();
		thickClientPanel.addCancelButtonActionListener(new RefreshDatasourceActionListener(this));
		thickClientPanel.setThickClientTableSelectionListener(new ThickClientTableSelectionListener(this));
		updateChartsListener = new UpdateChartGroupsListener(this);
		ChartPanel chartPanel = getView().getChartPanel();
		chartPanel.addUpdateButtonActionListener(updateChartsListener);
		chartPanel.addRefreshButtonActionListener(new RefreshChartGroupsListener(this));
	}

	@Override
	protected void populateDataToModel(AbstractModel model, Component view){

	}

	@Override
	protected void populateDataToView(AbstractModel model, Component view){
		UserAdminTreeModel treeModel = new UserAdminTreeModel(getModel().getGroupMap(), SessionData.getInstance()
		        .getConnectedDatabaseInstances());
		getView().getLeftPanel().setTreeModel(treeModel);
	}

	public void loadModel(){
		DatabaseInstance instance = SessionData.getInstance().getCurrentDatabaseInstance();
		model.setCurrentDatabaseInstance(instance);
		if (instance != null && instance.isConnected()){
			UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
			List<Group> groups = service.getGroups();
			model.setGroups(groups == null ? new ArrayList<Group>() : groups);
			model.setDeletedUsers(new ArrayList<User>());
			model.setCurrentGroup(null);
			model.setCurrentPanel(null);

			Group unassignedGroup = new Group();
			unassignedGroup.setId(-1);
			unassignedGroup.setName(Translation.get(TextID.Unassigned));
			List<User> unassignedUsers = service.getUnassignedUsers();
			unassignedGroup.setUsers(unassignedUsers);
			model.setUnassignedGroup(unassignedGroup);
			model.getGroups().add(unassignedGroup);
			model.resetLoaded(UserAdminModel.SCHEMAS);
			model.resetLoaded(UserAdminModel.USER_RANGES);

			model.setPasswordFormat(service.getPasswordFormat(SessionData.getInstance().getUserId()));
		}
	}

	public void reloadData(){
		model.resetLoadedMap();
		loadModel();
		populateDataToView(model, view);
	}

	private void reloadGroupPrivileges(){
		Group currentGroup = (Group) model.getCurrentGroup();
		if (currentGroup == null){
			return;
		}
		model.resetLoaded(UserAdminModel.SCHEMAS);
	}

	private void reloadChartPrivileges(){
		Group currentGroup = (Group) model.getCurrentGroup();
		if (currentGroup == null){
			return;
		}
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		Group newGroup = service.reloadGroup(currentGroup.getId());
		currentGroup.setChartGroups(newGroup.getChartGroups());
	}

	private void reloadAllUsers(){
		List<Group> groups = model.getGroups();
		if (groups == null){
			return;
		}
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		List<Group> newGroups = service.getGroups();
		for (int i = 0; i < groups.size(); i++){
			if (groups.get(i).getId() == null){
				continue;
			}
			for (int k = 0; k < newGroups.size(); k++){
				if (newGroups.get(k).equals(groups.get(i))){
					groups.get(i).setUsers(newGroups.get(k).getUsers());
					for (User user : groups.get(i).getUsers()){
						user.getGroups().clear();
						user.getGroups().add(groups.get(i));
					}
				}
			}
		}
		List<User> unassignedUsers = service.getUnassignedUsers();
		model.getUnassignedGroup().setUsers(unassignedUsers);
	}

	public void refreshPanelData(String panel){
		model.setCurrentPanel(panel);

		if (panel == null){
			panel = UserAdminView.EMPTY;
		}

		if (panel.equals(UserAdminView.ALL_USERS)){
			UserPanel userPanel = view.getUserPanel();
			userPanel.setTableModelData(getAllUsers());
			userPanel.setCurrentMode(UserPanel.ALL_USER_MODE);
			userPanel.setGroupReferenceData(getModel().getGroups());
			panel = Translation.get(TextID.GroupMembers);
			userPanel.refreshTable();
		} else{
			Group currentGroup = model.getCurrentGroup();
			if (panel.equals(Translation.get(TextID.GroupMembers))){
				UserPanel userPanel = view.getUserPanel();
				userPanel.setTableModelData(currentGroup.getUsers());
				userPanel.setCurrentMode(UserPanel.GROUP_USER_MODE);
				userPanel.setGroupReferenceData(getModel().getGroups());
				userPanel.refreshTable();
			} else if (panel.equals(Translation.get(TextID.GroupPrivileges))){
				boolean showLockedColumns = getShowLockedColumns();
				updatePrivilegesTable(showLockedColumns);
			} else if (panel.equals(Translation.get(TextID.GroupScope))){
				populateGroupScopeToView();
			} else if (panel.equals(Translation.get(TextID.OfflineClient))){
				ThickClientPanel tcPanel = view.getThickClientPanel();
				List<User> users = model.getCurrentGroup().getUsers();
				List<User> offlineClientUsers = getOfflineClientUsers(users);
				tcPanel.setTableModel(new ThickClientTableModel(offlineClientUsers));
				populateDataToSequenceRangeTable(tcPanel.getSelectedUser());
			} else if (panel.equals(Translation.get(TextID.Charts))){
				ChartPanel cPanel = view.getChartPanel();
				List<Schema> schemas = model.getSchemas();
				ChartPrivilegesTreeTableModel treeModel = new ChartPrivilegesTreeTableModel(schemas);
				cPanel.setTreeTableModel(treeModel, model.getCurrentGroup());
			}
		}

		view.showCurrentPanel(panel);
	}

	void updatePrivilegesTable(boolean showLockedColumns){
		PrivilegesPanel privilegesPanel = view.getPrivilegesPanel();
		GroupPrivilegesTreeTableModel treeTableModel = privilegesPanel.getGroupPrivilegesTreeModel();

		if (treeTableModel == null){
			treeTableModel = new GroupPrivilegesTreeTableModel(model.getSchemas());
		} else{
			treeTableModel.setSchemas(model.getSchemas());
		}
		privilegesPanel.setGroupPrivilegesTreeModel(treeTableModel, model.getCurrentGroup(), showLockedColumns);
		privilegesPanel.updateTreeTable();
		configLockedListener.setEnabled(false);
		privilegesPanel.setConfigLockedObject(showLockedColumns);
		configLockedListener.setEnabled(true);
	}

	private boolean getShowLockedColumns(){
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		boolean showLockedColumns = service.getConfigLockedObjectsSetting(model.getCurrentGroup().getId());
		model.setCurrentShowLockedColumns(showLockedColumns);
		return showLockedColumns;
	}

	public void populateDataToSequenceRangeTable(User usr){
		Map<Integer, List<UserSequenceState>> ranges = model.getUserRanges();
		if (ranges == null || usr == null){
			view.getThickClientPanel().setSeqRangeTableModel(new SeqRangeTableModel());
			return;
		}
		if (!ranges.containsKey(usr.getId())){
			view.getThickClientPanel().setSeqRangeTableModel(new SeqRangeTableModel());
			return;
		}
		List<UserSequenceState> states = ranges.get(usr.getId());
		view.getThickClientPanel().setSeqRangeTableModel(new SeqRangeTableModel(states));
	}

	protected List<User> getOfflineClientUsers(List<User> users){
		List<User> ret = new ArrayList<User>();;
		if (users == null || users.isEmpty())
			return ret;
		for (User user : users){
			if (user.getHasOfflineClient())
				ret.add(user);
		}
		return ret;
	}

	public void populateGroupScopeToView(){
		ScopePanel scopePanel = view.getScopePanel();
		Group currentGroup = model.getCurrentGroup();
		boolean useNodeScope = currentGroup.getNodeScope() != null && currentGroup.getNodeScope().equals('S');
		boolean useEdgeScope = currentGroup.getEdgeScope() != null && currentGroup.getEdgeScope().equals('S');
		String nodeScope = (currentGroup.getGroupScope() != null) ? currentGroup.getGroupScope().getNodeScope() : "";
		String edgeScope = (currentGroup.getGroupScope() != null) ? currentGroup.getGroupScope().getEdgeScope() : "";
		scopePanel.setData(useNodeScope, nodeScope, useEdgeScope, edgeScope);
	}

	public List<User> getAllUsers(){
		List<User> users = new ArrayList<User>();
		if (model.getGroups() != null){
			for (Group group : model.getGroups()){
				if (group.getUsers() != null && group.getUsers().size() > 0){
					users.addAll(group.getUsers());
				}
			}
		}
		return users;
	}

	public void addNewUser(){
		if (model.getCurrentGroup() == null && !getView().getUserPanel().isAllUserMode()){
			return;
		}

		User user = new User();

		Group currentGroup = model.getCurrentGroup();
		if (currentGroup != null){
			List<Group> groups = new ArrayList<Group>();
			if (currentGroup != null)
				groups.add(currentGroup);

			user.setGroups(groups);

			if (currentGroup.getUsers() == null){
				currentGroup.setUsers(new ArrayList<User>());
			}
			currentGroup.getUsers().add(user);
		} else{
			model.getUnassignedGroup().getUsers().add(user);
		}

		if (getView().getUserPanel().isAllUserMode()){
			getView().getUserPanel().getTableModel().setData(getAllUsers());
		}

		UserTableModel tableModel = view.getUserPanel().getTableModel();
		int index = tableModel.getRowCount() - 1;
		tableModel.fireTableRowsInserted(index, index);
		view.getUserPanel().setActiveTableRow(user);
	}

	public void deleteUser(User userToDelete){
		int index = -1;
		List<User> users = null;
		if (userToDelete == null){
			return;
		}
		if (userToDelete.getId() != null && userToDelete.getId() > 0){// user exists in DB
			getModel().getDeletedUsers().add(userToDelete);
		}
		if (userToDelete.getGroups() != null && userToDelete.getGroups().size() > 0
		        && userToDelete.getGroups().get(0) != null){
			users = userToDelete.getGroups().get(0).getUsers();
			index = users.indexOf(userToDelete);
			userToDelete.getGroups().get(0).getUsers().remove(userToDelete);
		} else{
			model.getUnassignedGroup().getUsers().remove(userToDelete);
		}
		if (getView().getUserPanel().isAllUserMode()){
			getView().getUserPanel().getTableModel().setData(getAllUsers());
		}
		log.debug("Removed user: " + userToDelete);

		view.getUserPanel().getTableModel().fireTableRowsDeleted(index, index);

		if (index >= 0 && users != null && users.size() > 0){
			User next = (index < users.size()) ? users.get(index) : users.get(users.size() - 1);
			view.getUserPanel().setActiveTableRow(next);
		}
	}

	protected String getMD5(String string){
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(string.getBytes("UTF-8"));
			byte messageDigest[] = md5.digest();

			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++){
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e){
			log.error("cant encode string to md5", e);
		} catch (UnsupportedEncodingException e){
			log.error("cant encode string to md5", e);
		}
		return null;
	}

	public User copyUser(User currentUser){
		User newUser = new User();
		String newUserName = get(NewUser);
		newUser.setFirstName(newUserName);
		newUser.setLastName(newUserName);
		newUser.setUserName(newUserName);
		if (currentUser.getGroups() != null && currentUser.getGroups().size() > 0){
			newUser.setGroups(new ArrayList<Group>());
			newUser.getGroups().add(currentUser.getGroups().get(0));
			currentUser.getGroups().get(0).getUsers().add(newUser);
		} else{
			if (model.getUnassignedGroup().getUsers() == null){
				model.getUnassignedGroup().setUsers(new ArrayList<User>());
			}
			model.getUnassignedGroup().getUsers().add(newUser);
		}

		newUser.setSettings(new ArrayList<UserSetting>());

		List<UserSetting> oldSettings = currentUser.getSettings();
		if (oldSettings != null){
			for (UserSetting oldSetting : oldSettings){
				UserSetting newSetting = new UserSetting();
				newSetting.setProp(oldSetting.getProp());
				newSetting.setSection(oldSetting.getSection());
				newSetting.setValue(oldSetting.getValue());
				newSetting.setUser(newUser);
				newUser.getSettings().add(newSetting);
			}
		}
		UserTableModel tableModel = view.getUserPanel().getTableModel();
		if (view.getUserPanel().isAllUserMode()){
			tableModel.setData(getAllUsers());
		}

		int index = tableModel.indexOf(newUser);
		tableModel.fireTableRowsInserted(index, index);
		view.getUserPanel().setActiveTableRow(newUser);

		return newUser;
	}

	public boolean checkInstanceLoaded(){
		DatabaseInstance dbInstance = SessionData.getInstance().getCurrentDatabaseInstance();
		if (dbInstance.isConnected() && !model.isInstanceLoaded(dbInstance)){
			reloadData();
			TreePath found = new TreeModelSupport().findPathForEqualObject(dbInstance, view.getLeftPanel().getTreeModel());
			view.getLeftPanel().setSelectionTreePath(found);
			log.debug("Loaded data for instance: " + dbInstance);
			return false;
		}
		return true;
	}

	@Override
	public void clearData(){
		model.clearSchemas();
		model.setCurrentGroup(null);
	}

	@Override
	public boolean save(){
		String currentPanel = model.getCurrentPanel();
		boolean result = true;
		if (currentPanel != null){
			if (currentPanel.equals(Translation.get(TextID.GroupMembers)) || currentPanel.equals(UserAdminView.ALL_USERS)){
				result = updateUserListener.save();
			} else if (currentPanel.equals(Translation.get(TextID.GroupPrivileges))){
				result = updateObjectListener.save();
			} else if (currentPanel.equals(Translation.get(TextID.GroupScope))){
				result = updateScopeListener.save();
			} else if (currentPanel.equals(Translation.get(TextID.Charts))){
				result = updateChartsListener.save();
			}
		}
		log.debug("Data saved: " + result);
		return result;
	}

	@Override
	public void reloadCurrent(){
		String currentPanel = model.getCurrentPanel();
		if (currentPanel != null){
			if (currentPanel.equals(Translation.get(TextID.GroupPrivileges))){
				reloadGroupPrivileges();
			} else if (currentPanel.equals(Translation.get(TextID.GroupMembers))
			        || currentPanel.equals(UserAdminView.ALL_USERS)){
				reloadAllUsers();
			} else if (currentPanel.equals(Translation.get(TextID.Charts))){
				reloadChartPrivileges();
			}
		}
	}

	private class UserAdminModelDataRequestListenerImpl implements UserAdminModelDataRequestListener{

		@Override
		public void dataRequested(Integer id){
			UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
			switch (id){
				case UserAdminModel.SCHEMAS:
					model.setSchemas(service.getSchemas());
					break;
				case UserAdminModel.USER_RANGES:
					try{
						model.setUserRanges(service.getUserRanges());
					} catch (ACException e){
						view.renderErrors(e.getErrors());
						model.setUserRanges(new HashMap<Integer, List<UserSequenceState>>());
					}
					break;
			}
		}
	}
}
