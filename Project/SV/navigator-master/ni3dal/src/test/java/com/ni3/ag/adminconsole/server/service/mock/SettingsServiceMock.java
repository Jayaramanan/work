/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.mock;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class SettingsServiceMock implements SettingsService{

	public List<Group> getGroups(){
		return generateGroupList();
	}

	private List<Group> generateGroupList(){
		ArrayList<Group> ar = new ArrayList<Group>();
		for (int i = 1; i <= 3; i++)
			ar.add(generateGroup(i));
		return ar;
	}

	private Group generateGroup(int id){
		Group g = new Group();
		g.setId(id);
		fillValues(g);
		g.setUsers(generateUsers(g));
		g.setGroupSettings(generateGroupSettings(g));
		return g;
	}

	private List<GroupSetting> generateGroupSettings(Group g){
		ArrayList<GroupSetting> ar = new ArrayList<GroupSetting>();
		for (int i = 1; i <= 10; i++){
			GroupSetting gs = new GroupSetting();
			gs.setGroup(g);
			gs.setProp("gr" + g.getId() + "prop" + i);
			gs.setSection("gr" + g.getId() + "sect" + i);
			gs.setValue("gr" + g.getId() + "val" + i);
			ar.add(gs);
		}
		return ar;
	}

	private List<User> generateUsers(Group g){
		ArrayList<User> ar = new ArrayList<User>();
		for (int i = 1; i <= 3; i++)
			ar.add(generateUser(g, i));
		return ar;
	}

	private User generateUser(Group g, int id){
		User u = new User();
		u.setId(id);
		u.setFirstName("firstName" + id);
		u.setLastName("lastName" + id);
		u.setUserName("user" + id);
		u.setPassword("pass" + id);
		u.setSettings(generateUserSettings(u));
		ArrayList<Group> grs = new ArrayList<Group>();
		grs.add(g);
		u.setGroups(grs);
		return u;
	}

	private List<UserSetting> generateUserSettings(User u){
		ArrayList<UserSetting> ar = new ArrayList<UserSetting>();
		for (int i = 1; i <= 5; i++){
			UserSetting us = new UserSetting();
			us.setUser(u);
			us.setSection("us" + u.getId() + "sect" + i);
			us.setProp("us" + u.getId() + "prop" + i);
			us.setValue("us" + u.getId() + "val" + i);
			ar.add(us);
		}
		return ar;
	}

	private void fillValues(Group g){
		g.setName("name" + g.getId());
		g.setNodeScope(new Character('c'));
		g.setEdgeScope(new Character('z'));
	}

	public List<ApplicationSetting> getApplicationSettings(){
		ArrayList<ApplicationSetting> ar = new ArrayList<ApplicationSetting>();
		for (int i = 0; i < 10; i++){
			ApplicationSetting as = new ApplicationSetting();
			as.setProp("appprop" + i);
			as.setSection("appsect" + i);
			as.setValue("appval" + i);
			ar.add(as);
		}
		return ar;
	}

	public void updateApplicationSettings(List<ApplicationSetting> applicationSettings,
	        List<ApplicationSetting> deletableApplicationSettings){
		// TODO Auto-generated method stub

	}

	public void updateGroupSettings(Group currentObject){
		// TODO Auto-generated method stub

	}

	public void updateUserSettings(User currentObject){
		// TODO Auto-generated method stub

	}

	@Override
	public List<Language> getLanguages(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Schema> getSchemas(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Group reloadGroup(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User reloadUser(Integer id){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getAllUsers(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Setting getApplicationSetting(String section, String prop){
		return new ApplicationSetting(section, prop, "value");
	}

	@Override
	public void updateApplicationSetting(String section, String prop, String value){
	}

    @Override
    public void updateUserSettings(List<User> currentUsers) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
