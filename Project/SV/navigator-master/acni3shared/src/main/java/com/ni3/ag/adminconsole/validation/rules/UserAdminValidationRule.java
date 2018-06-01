/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import static com.ni3.ag.adminconsole.shared.language.TextID.MsgUserFieldsEmpty;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserAdminValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	UserAdminValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){
		errors = new ArrayList<ErrorEntry>();

		UserAdminModel userModel = (UserAdminModel) model;

		if(!userModel.isAllUserModel() && userModel.getCurrentGroup() == null)
			return true;

		List<User> users = userModel.isAllUserModel() ? getAllUsers(userModel) : userModel.getCurrentGroup().getUsers();

		for (User user : users){
			if(!checkForInvalidUserNames(user, errors))
				return false;
			if(!checkForEmptyFields(user, errors))
				return false;
			if(!checkDuplicates(user, errors, userModel))
				return false;
		}
		return true;
	}

	private boolean checkDuplicates(User user, List<ErrorEntry> errors, UserAdminModel userModel){
		List<User> allUsers = getAllUsers(userModel);
		for (User user1 : allUsers){
			if(user1 == user)
				continue;
			if (user.getUserName().equalsIgnoreCase(user1.getUserName())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateUsers, new String[] { user.getUserName() }));
				return false;
			}

			String email = user.geteMail();
			if (email.equals(user1.geteMail())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateUserEmails, new String[] { email }));
				return false;
			}

			String sid = user.getSID();
			if (sid.equals(user1.getSID())){
				errors.add(new ErrorEntry(TextID.MsgDuplicateSIDs, new String[] { user.getUserName(),
						user1.getUserName() }));
				return false;
			}
		}
		return true;
	}

	private boolean checkForEmptyFields(User user, List<ErrorEntry> errors){
		if (user.getFirstName() == null || user.getFirstName().isEmpty() || user.getLastName() == null
				|| user.getLastName().isEmpty() || user.getUserName() == null || user.getUserName().isEmpty() || user.getPassword() == null
				|| user.getPassword().isEmpty() || user.geteMail() == null || user.geteMail().isEmpty()
				|| user.getSID() == null || user.getSID().isEmpty()){
			errors.add(new ErrorEntry(MsgUserFieldsEmpty));
			return false;
		}
		return true;
	}

	private boolean checkForInvalidUserNames(User user, List<ErrorEntry> errors){
		String username = user.getUserName();
		if (username != null && username.contains(" ")){
			errors.add(new ErrorEntry(TextID.MsgNoSpacesAllowedInUserName, new String[]{username}));
			return false;
		}
		return true;
	}

	private List<User> getAllUsers(UserAdminModel userModel){
		List<User> users = new ArrayList<User>();
		for (Group group : userModel.getGroups()){
			if (group.getUsers() != null && group.getUsers().size() > 0){
				users.addAll(group.getUsers());
			}
		}
		Group unassigned = userModel.getUnassignedGroup();
		if (unassigned != null)
			users.addAll(unassigned.getUsers());
		return users;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

}
