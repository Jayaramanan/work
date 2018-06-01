/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.springframework.remoting.RemoteAccessException;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.gui.MainPanel2;
import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.view.extend.AbstractTreeModel;
import com.ni3.ag.adminconsole.client.view.extend.TreeModelSupport;
import com.ni3.ag.adminconsole.client.view.useradmin.UserAdminView;
import com.ni3.ag.adminconsole.client.view.useradmin.UserTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.DataGroup;
import com.ni3.ag.adminconsole.shared.service.def.SchemaAdminService;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACValidationRule;


public class UpdateUserActionListener extends ProgressActionListener{
	private final static Logger log = Logger.getLogger(UpdateUserActionListener.class);
	private static final String SID_APPEND = "4Ni3";

	private UserAdminController controller;

	private ACValidationRule userAdminRule;
	private ACValidationRule userPasswordRule;

	public UpdateUserActionListener(UserAdminController controller){
		super(controller);
		this.controller = controller;
		this.userAdminRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("userAdminValidationRule");
		this.userPasswordRule = (ACValidationRule) ACSpringFactory.getInstance().getBean("userPasswordValidationRule");
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");
		UserAdminView view = controller.getView();
		User selectedUser = view.getUserPanel().getSelectedUser();
		TreePath selectedPath = view.getLeftPanel().getSelectionTreePath();

		if (!save()){
			return;
		}

		controller.reloadData();
		view.resetEditedFields();

		AbstractTreeModel treeModel = view.getLeftPanel().getTreeModel();
		TreePath newPath = new TreeModelSupport().findPathByNodes(selectedPath.getPath(), treeModel);

		if (newPath != null){
			view.getLeftPanel().setSelectionTreePath(newPath);
			view.getUserPanel().setActiveTableRow(selectedUser);
		}
	}

	protected void checkSID(User user){
		String usernameHashStr = controller.getMD5(user.getUserName());
		if (user.getSID() == null || !usernameHashStr.equals(user.getSID())){
			String newSID = user.getUserName() + SID_APPEND;
			String newUsernameHashStr = controller.getMD5(newSID);
			user.setSID(newUsernameHashStr);
		}
	}

	public boolean save(){
		log.debug("action performed");
		UserAdminView view = controller.getView();

		DatabaseInstance dbInstance = controller.getModel().getCurrentDatabaseInstance();
		if (dbInstance == null || !dbInstance.isConnected()){
			return true;
		}

		view.getUserPanel().stopCellEditing();
		view.clearErrors();

		controller.getModel().setAllUserMode(controller.getView().getUserPanel().isAllUserMode());
		List<User> usersToUpdate = null;
		if (controller.getModel().isAllUserModel()){
			usersToUpdate = controller.getAllUsers();
		} else{
			Group currentGroup = controller.getModel().getCurrentGroup();
			if (currentGroup == null){
				return true;
			}
			usersToUpdate = currentGroup.getUsers();
		}
		List<User> usersToDelete = controller.getModel().getDeletedUsers();
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();
		SchemaAdminService schemaService = ACSpringFactory.getInstance().getSchemaAdminService();
		if (usersToUpdate != null && usersToUpdate.size() > 0){
			UserTableModel tModel = view.getUserPanel().getTableModel();
			for (int i = 0; i < usersToUpdate.size(); i++){
				if (tModel.isChanged(i, UserTableModel.PASSWORD_COLUMN_INDEX)){
					User u = usersToUpdate.get(i);
					log.info("Password changed for user: " + u.getFirstName() + " " + u.getLastName());
					controller.getModel().setUserToChangePassword(u);
					if (!userPasswordRule.performCheck(controller.getModel())){
						controller.getView().renderErrors(userPasswordRule.getErrorEntries());
						return false;
					}
					u.setPassword(controller.getPasswordEncoder().generate(u.getPassword()));
				}
			}

			if (!userAdminRule.performCheck(controller.getModel())){
				controller.getView().renderErrors(userAdminRule.getErrorEntries());
				return false;
			}

			for (User user : usersToUpdate){
				if (user.getGroups() != null && user.getGroups().contains(controller.getModel().getUnassignedGroup())){
					user.getGroups().remove(controller.getModel().getUnassignedGroup());
				}
			}
			service.updateUsers(usersToUpdate);
			schemaService.setInvalidationRequired(DataGroup.Users, true);
			MainPanel2.setInvalidationNeeded(TextID.Users, true);
		}

		boolean deleteOk = true;
		if (usersToDelete != null && !usersToDelete.isEmpty()){
			deleteOk = deleteUsers(usersToDelete);

			controller.getModel().getDeletedUsers().clear();
		}
		if (!deleteOk)
			return false;

		return true;
	}

	private boolean deleteUsers(List<User> usersToDelete){
		UserAdminService service = ACSpringFactory.getInstance().getUserAdminService();

		boolean deleteOk = true;
		try{
			service.deleteUsers(usersToDelete);
		} catch (RemoteAccessException e){
			ACException ae = new ACException(TextID.MsgCantDeleteUser);
			controller.getView().renderErrors(ae.getErrors());
			deleteOk = false;
			for (User u : usersToDelete){
				List<Group> groups = u.getGroups();
				if (!groups.isEmpty())
					groups.get(0).getUsers().add(u);
			}
			controller.refreshPanelData(controller.getModel().getCurrentPanel());
		}
		return deleteOk;
	}
}
