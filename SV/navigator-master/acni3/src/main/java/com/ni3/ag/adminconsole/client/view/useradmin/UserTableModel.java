/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTableModel;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserTableModel extends ACTableModel{
	private static final Logger log = Logger.getLogger(UserTableModel.class);
	private static final long serialVersionUID = 1L;
	private List<User> users = new ArrayList<User>();
	public static final int PASSWORD_COLUMN_INDEX = 3;

	public UserTableModel(){
		addColumn(Translation.get(TextID.FirstName), true, String.class, true);
		addColumn(Translation.get(TextID.LastName), true, String.class, true);
		addColumn(Translation.get(TextID.UserName), true, String.class, true);
		addColumn(Translation.get(TextID.Password), true, String.class, true);
		addColumn(Translation.get(TextID.Group), true, Group.class, false);
		addColumn(Translation.get(TextID.EMail), true, String.class, true);
		addColumn(Translation.get(TextID.Active), true, Boolean.class, false);
		addColumn(Translation.get(TextID.SID), true, String.class, true);
		addColumn(Translation.get(TextID.HasOfflineClient), true, Boolean.class, false);
		addColumn(Translation.get(TextID.ETLUser), true, String.class, false);
		addColumn(Translation.get(TextID.ETLPassword), true, String.class, false);
	};

	public UserTableModel(List<User> users){
		this();
		this.users = users;
	}

	public void setData(List<User> users){
		super.resetChanges();
		this.users = users;
	}

	public int getRowCount(){
		if (users == null)
			return 0;
		return users.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex){
		User user = users.get(rowIndex);
		switch (columnIndex){
			case 0:
				return user.getFirstName();
			case 1:
				return user.getLastName();
			case 2:
				return user.getUserName();
			case 3:
				return user.getPassword();
			case 4:
				return user.getGroups() != null && user.getGroups().size() > 0 ? user.getGroups().get(0) : null;
			case 5:
				return user.geteMail();
			case 6:
				return user.getActive();
			case 7:
				return user.getSID();
			case 8:
				return user.getHasOfflineClient();
			case 9:
				return user.getEtlUser();
			case 10:
				return user.getEtlPassword();
			default:
				return null;
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex){
		User user = users.get(rowIndex);
		ObjectVisibilityStore visStore = ObjectVisibilityStore.getInstance();
		boolean enabled = visStore.isDataCaptureEnabled();
		switch (columnIndex){
			case 8:
				return enabled && userHasValidGroup(user);
			default:
				return true;
		}
	}

	boolean userHasValidGroup(User user){
		if (user.getGroups() == null)
			return false;
		if (user.getGroups().isEmpty())
			return false;
		Group g = user.getGroups().get(0);
		if (g == null || g.getId() == -1)
			return false;
		return true;
	}

	private String getMD5(String string){
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

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		aValue = validateValue(aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
		User user = users.get(rowIndex);
		switch (columnIndex){
			case 0:
				user.setFirstName((String) aValue);
				break;
			case 1:
				user.setLastName((String) aValue);
				break;
			case 2:
				user.setUserName((String) aValue);
				break;
			case 3:
				user.setPassword((String) aValue);
				break;
			case 4:
				if (user.getGroups() == null){
					user.setGroups(new ArrayList<Group>());
				}
				user.getGroups().clear();
				Group g = (Group) aValue;
				if (g == null || g.getId() == -1){
					user.setHasOfflineClient(false);
				}
				if (g != null){
					user.getGroups().add(g);
					fireTableCellUpdated(rowIndex, 8);
				}
				break;
			case 5:
				user.seteMail((String) aValue);
				break;
			case 6:
				user.setActive((Boolean) aValue);
				break;
			case 7:
				String sid = null;
				if (aValue != null)
					sid = getMD5((String) aValue);
				user.setSID(sid);
				break;
			case 8:
				user.setHasOfflineClient((Boolean) aValue);
				break;
			case 9:
				user.setEtlUser((String) aValue);
				break;
			case 10:
				user.setEtlPassword((String) aValue);
				break;
			default:
				break;
		}
	}

	public User getSelectedUser(int rowIndex){
		if (rowIndex >= 0 && rowIndex < users.size()){
			return users.get(rowIndex);
		}
		return null;
	}

	public int indexOf(User newAttribute){
		return this.users.indexOf(newAttribute);
	}

}
