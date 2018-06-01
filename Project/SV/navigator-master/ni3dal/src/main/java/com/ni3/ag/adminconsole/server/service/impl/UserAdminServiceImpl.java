/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.ni3.ag.adminconsole.domain.AttributeGroup;
import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartGroup;
import com.ni3.ag.adminconsole.domain.EditingOption;
import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.domain.GroupScope;
import com.ni3.ag.adminconsole.domain.GroupSetting;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectGroup;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.SchemaGroup;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSequenceState;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.license.LicenseData;
import com.ni3.ag.adminconsole.server.dao.AttributeGroupDAO;
import com.ni3.ag.adminconsole.server.dao.DeltaHeaderDAO;
import com.ni3.ag.adminconsole.server.dao.DeltaUserDAO;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectConnectionDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.adminconsole.server.dao.ObjectGroupDAO;
import com.ni3.ag.adminconsole.server.dao.OfflineJobDAO;
import com.ni3.ag.adminconsole.server.dao.SchemaDAO;
import com.ni3.ag.adminconsole.server.dao.SettingDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.lifecycle.GroupSettingsGenerator;
import com.ni3.ag.adminconsole.server.lifecycle.UserSettingsGenerator;
import com.ni3.ag.adminconsole.server.service.SequenceValueService;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.LicenseService;
import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;
import com.ni3.ag.adminconsole.util.OfflineObjectId;
import com.ni3.ag.adminconsole.util.PasswordGenerator;
import com.ni3.ag.adminconsole.util.SettingsUtil;
import com.ni3.ag.adminconsole.validation.ACException;

public class UserAdminServiceImpl implements UserAdminService{
	private static final Logger log = Logger.getLogger(UserAdminServiceImpl.class);

	private GroupDAO groupDAO;
	private UserDAO userDAO;
	private ObjectDefinitionDAO objectDefinitionDAO;
	private ObjectGroupDAO objectGroupDAO;
	private AttributeGroupDAO attributeGroupDAO;
	private ObjectConnectionDAO objectConnectionDAO;
	private SchemaDAO schemaDAO;
	private DataSource dataSource;
	private PasswordGenerator passwordGenerator;
	private PasswordEncoder passwordEncoder;
	private UserLanguageService userLangService;
	private LicenseService licenseService;
	private SequenceValueService sequenceValueService;
	private DeltaHeaderDAO deltaHeaderDAO;
	private DeltaUserDAO deltaUserDAO;
	private OfflineJobDAO offlineJobDAO;
	private SettingDAO settingDAO;

	private String from;

	private MailSender mailSender;

	public OfflineJobDAO getOfflineJobDAO(){
		return offlineJobDAO;
	}

	public void setOfflineJobDAO(OfflineJobDAO offlineJobDAO){
		this.offlineJobDAO = offlineJobDAO;
	}

	public void setDeltaHeaderDAO(DeltaHeaderDAO deltaHeaderDAO){
		this.deltaHeaderDAO = deltaHeaderDAO;
	}

	public void setDeltaUserDAO(DeltaUserDAO deltaUserDAO){
		this.deltaUserDAO = deltaUserDAO;
	}

	public SequenceValueService getSequenceValueService(){
		return sequenceValueService;
	}

	public void setSequenceValueService(SequenceValueService sequenceValueService){
		this.sequenceValueService = sequenceValueService;
	}

	public LicenseService getLicenseService(){
		return licenseService;
	}

	public void setLicenseService(LicenseService licenseService){
		this.licenseService = licenseService;
	}

	public MailSender getMailSender(){
		return mailSender;
	}

	public void setMailSender(MailSender mailSender){
		this.mailSender = mailSender;
	}

	public UserLanguageService getUserLangService(){
		return userLangService;
	}

	public void setUserLangService(UserLanguageService userLangService){
		this.userLangService = userLangService;
	}

	public String getFrom(){
		return from;
	}

	public void setFrom(String from){
		this.from = from;
	}

	public PasswordGenerator getPasswordGenerator(){
		return passwordGenerator;
	}

	public void setPasswordGenerator(PasswordGenerator passwordGenerator){
		this.passwordGenerator = passwordGenerator;
	}

	public PasswordEncoder getPasswordEncoder(){
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder){
		this.passwordEncoder = passwordEncoder;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	public UserDAO getUserDAO(){
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public ObjectDefinitionDAO getObjectDefinitionDAO(){
		return objectDefinitionDAO;
	}

	public void setObjectDefinitionDAO(ObjectDefinitionDAO objectDefinitionDAO){
		this.objectDefinitionDAO = objectDefinitionDAO;
	}

	public void setObjectGroupDAO(ObjectGroupDAO objectGroupDAO){
		this.objectGroupDAO = objectGroupDAO;
	}

	public AttributeGroupDAO getAttributeGroupDAO(){
		return attributeGroupDAO;
	}

	public void setAttributeGroupDAO(AttributeGroupDAO attributeGroupDAO){
		this.attributeGroupDAO = attributeGroupDAO;
	}

	public ObjectConnectionDAO getObjectConnectionDAO(){
		return objectConnectionDAO;
	}

	public void setObjectConnectionDAO(ObjectConnectionDAO objectConnectionDAO){
		this.objectConnectionDAO = objectConnectionDAO;
	}

	public SchemaDAO getSchemaDAO(){
		return schemaDAO;
	}

	public void setSchemaDAO(SchemaDAO schemaDAO){
		this.schemaDAO = schemaDAO;
	}

	public void setSettingDAO(SettingDAO settingDAO){
		this.settingDAO = settingDAO;
	}

	@Override
	public List<Group> getGroups(){
		List<Group> groups = getGroupDAO().getGroups();
		for (Group group : groups){
			List<User> users = group.getUsers();
			Hibernate.initialize(users);
			for (User user : users){
				Hibernate.initialize(user.getGroups());
				Hibernate.initialize(user.getSettings());
			}

			Hibernate.initialize(group.getGroupScope());
		}
		return groups;
	}

	public DataSource getDataSource(){
		return dataSource;
	}

	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
	}

	@Override
	public void updateUsers(List<User> users){
		for (User u : users){
			if (u.getId() == null)// new user
				u.setSettings(UserSettingsGenerator.generateSettings(u));
		}
		getUserDAO().saveOrUpdateAll(users);

	}

	@Override
	public void deleteUsers(List<User> users){
		log.debug("Deleting " + users.size() + " users");
		List<User> loadedUsers = new ArrayList<User>();
		for (User u : users){
			User loaded = userDAO.getById(u.getId());
			if (loaded != null){
				loadedUsers.add(loaded);
			}
		}
		getUserDAO().deleteAll(loadedUsers);
	}

	@Override
	public Group addGroup(Group group){
		List<Schema> schemas = schemaDAO.getSchemas();
		group.setObjectGroups(new ArrayList<ObjectGroup>());
		group.setAttributeGroups(new ArrayList<AttributeGroup>());
		group.setNodeScope('A');
		group.setEdgeScope('A');

		for (Schema schema : schemas){
			SchemaGroup sg = new SchemaGroup(schema, group);
			sg.setCanRead(false);
			schema.getSchemaGroups().add(sg);
			for (ObjectDefinition od : schema.getObjectDefinitions()){
				ObjectGroup oug = new ObjectGroup(od, group);
				oug.setCanCreate(false);
				oug.setCanRead(false);
				oug.setCanDelete(false);
				oug.setCanUpdate(false);
				group.getObjectGroups().add(oug);
				for (ObjectAttribute attr : od.getObjectAttributes()){
					AttributeGroup ag = new AttributeGroup(attr, group);
					ag.setCanRead(false);
					ag.setEditingOption(EditingOption.NotVisible);
					group.getAttributeGroups().add(ag);
				}
			}
		}
		group.setGroupSettings(GroupSettingsGenerator.generateSettings(group));

		group = getGroupDAO().addGroup(group);
		return group;
	}

	@Override
	public void deleteGroup(Group group){
		group = getGroupDAO().getGroup(group.getId()); // reload

		List<User> users = group.getUsers();
		if (users != null && users.size() > 0){
			for (User user : users){
				user.getGroups().remove(group);
			}
		}

		List<AttributeGroup> attributeGroups = group.getAttributeGroups();
		if (attributeGroups != null){
			for (AttributeGroup ag : attributeGroups){
				ag.getObjectAttribute().getAttributeGroups().remove(ag);
			}
		}

		List<GroupPrefilter> pAttrGroups = group.getPredefAttributeGroups();
		if (pAttrGroups != null){
			for (GroupPrefilter pg : pAttrGroups){
				pg.getPredefinedAttribute().getPredefAttributeGroups().remove(pg);
			}
		}

		getGroupDAO().deleteGroup(group);
	}

	@Override
	public void updateGroup(Group group){
		getGroupDAO().saveOrUpdate(group);
	}

	@Override
	public Group copyGroup(Group sourceGroup, String newName) throws ACException{
		sourceGroup = getGroupDAO().getGroup(sourceGroup.getId());

		Group group;
		try{
			group = sourceGroup.cloneDeep(newName);
		} catch (CloneNotSupportedException e){
			log.error("Can not copy user group", e);
			throw new ACException(TextID.MsgCantCopyGroup, new String[] { e.getMessage() });
		}

		group = getGroupDAO().addGroup(group);

		final GroupScope sourceGroupScope = sourceGroup.getGroupScope();
		if (sourceGroupScope != null){
			group.setGroupScope(sourceGroupScope.cloneFor(group));
		}
		return group;
	}

	@Override
	public List<User> getUnassignedUsers(){
		List<User> users = getUserDAO().getUnassignedUsers();
		for (User user : users){
			Hibernate.initialize(user.getGroups());
			Hibernate.initialize(user.getSettings());
		}
		return users;
	}

	@Override
	public List<ObjectDefinition> getObjects(){
		return getObjectDefinitionDAO().getObjectDefinitions();
	}

	@Override
	public void updateAttributeGroups(List<AttributeGroup> attributeGroups){
		getAttributeGroupDAO().updateAttributeGroups(attributeGroups);
	}

	@Override
	public void updateObjectGroups(List<ObjectGroup> objectGroups){
		objectGroupDAO.updateObjectGroups(objectGroups);
	}

	@Override
	public List<ObjectConnection> getConnections(){
		List<ObjectConnection> ocs = getObjectConnectionDAO().getObjectConnections();
		Hibernate.initialize(ocs);
		for (ObjectConnection oc : ocs){
			Hibernate.initialize(oc.getFromObject());
			Hibernate.initialize(oc.getToObject());
			Hibernate.initialize(oc.getObject());
		}
		return ocs;
	}

	@Override
	public User getUser(Integer id){
		User user = getUserDAO().getById(id);
		Hibernate.initialize(user.getSettings());
		Hibernate.initialize(user.getGroups());
		return user;
	}

	@Override
	public Integer addUser(User user){
		return getUserDAO().addUser(user);
	}

	@Override
	public Group reloadGroup(Integer id){
		Group group = groupDAO.getGroup(id);
		Hibernate.initialize(group.getUsers());
		for (ChartGroup cg : group.getChartGroups()){
			Hibernate.initialize(cg.getChart());
		}
		return group;
	}

	@Override
	public List<Group> reloadGroupUsers(List<Group> groups){
		List<Group> newGroups = new ArrayList<Group>();
		for (Group group : groups){
			if (group.getId() == null){
				continue;
			}
			Group newGroup = groupDAO.getGroup(group.getId());
			Hibernate.initialize(newGroup.getUsers());
			newGroups.add(newGroup);
		}
		return newGroups;
	}

	@Override
	public Group getGroup(Integer id){
		Group g = groupDAO.getGroup(id);
		Hibernate.initialize(g.getGroupSettings());
		return g;
	}

	@Override
	public List<ObjectDefinition> getObjectsByUser(User u){
		return objectDefinitionDAO.getObjectDefinitionsByUser(u);
	}

	@Override
	public List<Schema> getSchemasByUser(User u){
		return schemaDAO.getSchemasByUser(u);
	}

	@Override
	public void deleteGroupScope(GroupScope scope){
		getGroupDAO().deleteGroupScope(scope);
	}

	@Override
	public User resetPassword(User userToReset) throws ACException{
		User u = userDAO.getById(userToReset.getId());
		String newPass = passwordGenerator.generatePassword();
		String encoded = passwordEncoder.generate(newPass);
		u.setPassword(encoded);
		u.setSID(encoded);
		u = userDAO.saveOrUpdate(u);
		ACRoutingDataSource ds = (ACRoutingDataSource) dataSource;
		String dbid = ds.getCurrentDatabaseInstanceId();
		sendEmail(u, newPass, dbid);
		return u;
	}

	private void sendEmail(User u, String newPass, String dbid) throws ACException{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(u.geteMail());
		Language lang = getLangByUser(u);
		Object[] subjParams = new Object[] { dbid };
		String subject = makeReplacement(userLangService.getLabelById(TextID.MsgEmailPasswordResetSubject, lang), subjParams);
		message.setSubject(subject);

		Object[] params = new Object[] { u.getFirstName(), u.getLastName(), dbid, u.getUserName(), newPass };
		String text = makeReplacement(userLangService.getLabelById(TextID.MsgEMailPasswordResetText, lang), params);
		message.setText(text);
		try{
			log.info("Message is ready to be sent " + u.geteMail());
			mailSender.send(message);
			log.info("Message is sent");
		} catch (MailException e){
			log.error("Message is not sent. Error: ", e);
			throw new ACException(TextID.MsgCannotSendMail);
		}
	}

	private String makeReplacement(String text, Object[] params){
		if (text != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				text = text.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return text;
	}

	private Language getLangByUser(User u){
		Language l = new Language();
		l.setId(1);
		Hibernate.initialize(u.getSettings());
		for (UserSetting s : u.getSettings()){
			if (s.getProp().equals(Setting.LANGUAGE_PROPERTY)){
				l.setId(Integer.valueOf(s.getValue()));
				return l;
			}
		}
		return l;
	}

	@Override
	public java.util.Map<Integer, List<UserSequenceState>> getUserRanges() throws ACException{
		List<User> users = userDAO.getUsers();
		java.util.Map<Integer, List<UserSequenceState>> rangesMap = new HashMap<Integer, List<UserSequenceState>>();
		for (User u : users){
			if (!u.getHasOfflineClient())
				continue;

			List<UserSequenceState> usList = new ArrayList<UserSequenceState>();
			for (String name : User.SEQUENCES){
				UserSequenceState uss = new UserSequenceState();
				uss.setRangeStart(new OfflineObjectId(u.getId(), 0, true).getResult());
				uss.setRangeEnd(new OfflineObjectId(u.getId(), OfflineObjectId.ID_MASK, true).getResult());
				uss.setCurrent(sequenceValueService.getCurrentValForSequence(name, u));
				uss.setSequenceName(name);
				usList.add(uss);
			}
			rangesMap.put(u.getId(), usList);
		}
		return rangesMap;
	}

	@Override
	public Integer getDeltasByUser(User u){
		return deltaHeaderDAO.getCountByUser(u);
	}

	@Override
	public Integer getOutDeltasByUser(User u){
		return deltaUserDAO.getCountByUser(u);
	}

	@Override
	public Integer getOfflineJobsByUser(User u){
		return offlineJobDAO.getJobCountByUser(u);
	}

	@Override
	public List<Schema> getSchemas(){
		List<Schema> schemas = schemaDAO.getSchemas();
		for (Schema schema : schemas){
			Hibernate.initialize(schema.getSchemaGroups());
			Hibernate.initialize(schema.getCreatedBy());
			List<ObjectDefinition> objectDefinitions = schema.getObjectDefinitions();
			for (ObjectDefinition objectDefinition : objectDefinitions){
				Hibernate.initialize(objectDefinition.getCreatedBy());
				Hibernate.initialize(objectDefinition.getObjectGroups());
				for (ObjectAttribute attribute : objectDefinition.getObjectAttributes()){
					Hibernate.initialize(attribute.getAttributeGroups());
					for (PredefinedAttribute pa : attribute.getPredefinedAttributes()){
						Hibernate.initialize(pa.getPredefAttributeGroups());
					}
				}
			}
			for (Chart chart : schema.getCharts()){
				Hibernate.initialize(chart.getChartGroups());
			}

		}
		return schemas;
	}

	@Override
	public void updateSchemas(List<Schema> schemas){
		schemaDAO.saveOrUpdateAll(schemas);
	}

	@Override
	public Integer getMaximumUsersWithEtlAccess(){
		List<LicenseData> licenses = licenseService.getLicenseDataByProductName(LicenseData.ACNi3WEB_PRODUCT);
		int max = 0;
		for (LicenseData ldata : licenses){
			Integer etlUserCount = (Integer) ldata.get(LicenseData.ACETL_MODULE);
			if (etlUserCount == null)
				continue;
			if (etlUserCount > max)
				max = etlUserCount;
		}
		return max;
	}

	@Override
	public String getPasswordFormat(int userId){
		return settingDAO.getSetting(userId, Setting.APPLET_SECTION, Setting.PASSWORD_COMPLEXITY_SETTING);
	}

	@Override
	public boolean getConfigLockedObjectsSetting(int groupId){
		Setting setting = settingDAO.getGroupSetting(groupId, Setting.APPLET_SECTION,
		        Setting.CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY);
		if (setting == null){
			setting = settingDAO.getApplicationSetting(Setting.APPLET_SECTION,
			        Setting.CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY);
		}
		return setting != null && SettingsUtil.isTrueValue(setting.getValue());
	}

	@Override
	public void updateConfigLockedObjectsSetting(Integer groupId, boolean showLockedColumns){
		Group group = groupDAO.getGroup(groupId);
		GroupSetting found = null;
		for (GroupSetting gs : group.getGroupSettings()){
			if (gs.getProp() != null && gs.getProp().equals(Setting.CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY)){
				found = gs;
				break;
			}
		}
		if (found == null){
			found = new GroupSetting(group, Setting.APPLET_SECTION, Setting.CONFIG_LOCKED_OBJECT_PRIVILEGES_PROPERTY, null);
			group.getGroupSettings().add(found);
		}
		found.setValue(String.valueOf(showLockedColumns));
		groupDAO.saveOrUpdate(group);
	}
}
