/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.SettingsService;

public class DeleteSchemaValidationRuleTest extends TestCase{

	private DeleteSchemaValidationRule rule;
	private SchemaAdminModel model;
	private SettingsService service;
	private List<Group> groups;
	private Group group;
	private List<ApplicationSetting> settings;
	private Setting setting;
	private List<User> users;
	private User user;
	private Schema schema;

	public void setUp(){
		rule = new DeleteSchemaValidationRule();
		model = new SchemaAdminModel();
		groups = new ArrayList<Group>();
		group = new Group();
		groups.add(group);
		users = new ArrayList<User>();
		user = new User();
		users.add(user);
		settings = new ArrayList<ApplicationSetting>();

		schema = new Schema();
		schema.setId(11);

		service = new SettingsService(){
			public List<Group> getGroups(){
				return groups;
			}

			public List<ApplicationSetting> getApplicationSettings(){
				return settings;
			}

			public List<User> getAllUsers(){
				return users;
			}

			public void updateUserSettings(User currentObject){
			}

			public void updateGroupSettings(Group currentObject){
			}

			public void updateApplicationSettings(List<ApplicationSetting> as, List<ApplicationSetting> das){
			}

			public User reloadUser(Integer id){
				return null;
			}

			public Group reloadGroup(Integer id){
				return null;
			}

			public List<Schema> getSchemas(){
				return null;
			}

			public List<Language> getLanguages(){
				return null;
			}

			@Override
            public Setting getApplicationSetting(String section, String prop){
	            // TODO Auto-generated method stub
	            return null;
            }

			@Override
            public void updateApplicationSetting(String section, String prop, String value){
	            // TODO Auto-generated method stub
	            
            }

            @Override
            public void updateUserSettings(List<User> currentUsers) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
		rule.setSettingsService(service);
	}

	public void testIsContainSchema(){
		settings.add(new ApplicationSetting("Applet", "Scheme", "11"));
		assertTrue(rule.isContainSchema(settings, schema));
	}

	public void testIsContainSchemaNotContain(){
		assertFalse(rule.isContainSchema(settings, schema));
		settings.add(new ApplicationSetting("Applet", "Schema", "22"));
		assertFalse(rule.isContainSchema(settings, schema));
	}

	public void testCheckUserSettingsSchemaInUse(){
		user.setSettings(new ArrayList<UserSetting>());
		user.getSettings().add(new UserSetting(user, "Applet", "Schema", "11"));
		rule.checkUserSettings(schema);
	}

	public void testCheckUserSettingsSchemaNotInUse(){
		settings.add(new ApplicationSetting("Applet", "Schema", "22"));
		rule.checkUserSettings(schema);
	}

}
