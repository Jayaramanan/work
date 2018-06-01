/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.ni3.ag.adminconsole.domain.Group;
import com.ni3.ag.adminconsole.domain.Language;
import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.ModuleUser;
import com.ni3.ag.adminconsole.domain.Setting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.dao.GroupDAO;
import com.ni3.ag.adminconsole.server.dao.ModuleDAO;
import com.ni3.ag.adminconsole.server.dao.UserDAO;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.server.service.util.FTPModuleLister;
import com.ni3.ag.adminconsole.server.service.util.FileNameValidator;
import com.ni3.ag.adminconsole.server.service.util.LocalModuleLister;
import com.ni3.ag.adminconsole.server.service.util.ModuleLister;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.service.def.UserLanguageService;
import com.ni3.ag.adminconsole.shared.service.def.VersioningService;
import com.ni3.ag.adminconsole.validation.ACException;

public class VersioningServiceImpl implements VersioningService{

	private final static Logger log = Logger.getLogger(VersioningServiceImpl.class);

	private GroupDAO groupDAO;
	private ModuleDAO moduleDAO;
	private UserLanguageService userLanguageService;
	private UserDAO userDAO;
	private JavaMailSender mailSender;
	private ACRoutingDataSource dataSource;

	private Properties props;
	private String settingsFileName;
	private String starterExecutableName;
	private String ni3Name;
	private String starterExecutableResourcePath;
	private String ni3ExecutableResourcePath;
	private String from;

	public void setDataSource(ACRoutingDataSource dataSource){
		this.dataSource = dataSource;
	}

	public void setFrom(String from){
		this.from = from;
	}

	public void setNi3Name(String ni3Name){
		this.ni3Name = ni3Name;
	}

	public void setNi3ExecutableResourcePath(String ni3ExecutableResourcePath){
		this.ni3ExecutableResourcePath = ni3ExecutableResourcePath;
	}

	public void setStarterExecutableResourcePath(String starterExecutableResourcePath){
		this.starterExecutableResourcePath = starterExecutableResourcePath;
	}

	public void setStarterExecutableName(String starterExecutableName){
		this.starterExecutableName = starterExecutableName;
	}

	public void setSettingsFileName(String settingsFileName){
		this.settingsFileName = settingsFileName;
	}

	public void setMailSender(JavaMailSender mailSender){
		this.mailSender = mailSender;
	}

	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

	public void setUserLanguageService(UserLanguageService userLanguageService){
		this.userLanguageService = userLanguageService;
	}

	public void setProps(Properties props){
		this.props = props;
	}

	public ModuleDAO getModuleDAO(){
		return moduleDAO;
	}

	public void setModuleDAO(ModuleDAO moduleDAO){
		this.moduleDAO = moduleDAO;
	}

	public GroupDAO getGroupDAO(){
		return groupDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO){
		this.groupDAO = groupDAO;
	}

	@Override
	public List<Group> getGroups(){
		List<Group> groups = groupDAO.getGroups();
		for (Group g : groups){
			Hibernate.initialize(g.getUsers());
			for (User u : g.getUsers()){
				Hibernate.initialize(u.getUserModules());
			}
		}
		return groups;
	}

	@Override
	public List<Module> getModules(){
		return moduleDAO.getModules();
	}

	@Override
	public void updateModules(List<Module> toUpdate, List<Module> toDelete){
		if (!toDelete.isEmpty())
			moduleDAO.deleteAll(toDelete);
		if (!toUpdate.isEmpty())
			moduleDAO.saveOrUpdateAll(toUpdate);
	}

	@Override
	public void updateUserModules(List<User> users){
		Logger log = Logger.getLogger(getClass());
		for (User u : users){
			log.info("User :" + u.getUserName());
			for (ModuleUser mu : u.getUserModules()){
				String s = mu.getCurrent() != null ? mu.getCurrent().getName() : null;
				s = s != null ? s : (mu.getTarget() != null ? mu.getTarget().getName() : null);
				log.info("\t" + s + " " + (mu.getCurrent() != null ? mu.getCurrent().getVersion() : "") + " -> "
				        + (mu.getTarget() != null ? mu.getTarget().getVersion() : ""));
			}
		}
		userDAO.saveOrUpdateAll(users);
	}

	private String getModulePathForInstance(){
		InstanceDescriptor desc = dataSource.getCurrentInstanceDescriptor();
		final String modulePath = desc.getModulePath();
		if (modulePath == null || modulePath.isEmpty()){
			log.warn("Modules path is not configured for instance " + desc.getDBID());
		}
		return modulePath;
	}

	@Override
	public List<String> getFileNames(){
		String path = getModulePathForInstance();
		List<String> result = null;
		ModuleLister lister;
		if (path != null && !path.isEmpty()){
			if (path.startsWith("ftp://"))
				lister = new FTPModuleLister(path);
			else
				lister = new LocalModuleLister(path);
			result = lister.list();
			Collections.sort(result);
		}
		return result;
	}

	@Override
	public String uploadZipModule(byte[] bytes, String name) throws ACException{
		String ret = null;
		try{
			ret = copyFileToLocalDir(bytes, name);
		} catch (IOException e1){
			log.error("failed to store file on disk", e1);
			throw new ACException(TextID.MsgFailedToCreateOrSaveFile);
		}

		return ret;
	}

	@Override
	public boolean testModulesPath(){
		String path = getModulePathForInstance();
		ModuleLister lister = null;
		if (path != null && !path.isEmpty()){
			if (path.startsWith("ftp://"))
				lister = new FTPModuleLister(path);
			else
				lister = new LocalModuleLister(path);
		}
		return lister != null && lister.testPath();
	}

	private String copyFileToLocalDir(byte[] bytes, String fileName) throws IOException{
		String path = getModulePathForInstance();
		if (!path.endsWith(File.separator))
			path += File.separator;
		File f = new File(path + fileName);
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(bytes);
		fos.flush();
		fos.close();
		return path + fileName;
	}

	private String makeReplacement(String text, Object[] params){
		if (text != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				text = text.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return text;
	}

	@Override
	public void sendStarterModuleToUser(User targetUser, boolean useSSO) throws ACException{
		log.debug("sending starter module to user: " + targetUser.getUserName() + "(" + targetUser.getId() + ")");
		File tempSettingsFile = createTempSettingsFile(targetUser, useSSO);
		File starterFile = createTempStarterFile("starter", starterExecutableResourcePath, targetUser);
		File ni3File = createTempStarterFile("ni3", ni3ExecutableResourcePath, targetUser);

		log.debug("Create message");
		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(targetUser.geteMail());
			String body = makeReplacement(
			        userLanguageService.getLabelById(TextID.MsgThickClientMailBody, getLanguageForUser(targetUser)),
			        new String[] { targetUser.getFirstName(), targetUser.getLastName() }).replace("\\n", "\n");
			helper.setText(body);
			helper.setSubject(userLanguageService.getLabelById(TextID.MsgThickClientMailSubject,
			        getLanguageForUser(targetUser)));
			helper.setFrom(from);

			log.debug("create mail attachements");
			FileSystemResource file = new FileSystemResource(tempSettingsFile);
			helper.addAttachment(settingsFileName, file);
			file = new FileSystemResource(starterFile);
			helper.addAttachment(starterExecutableName, file);
			file = new FileSystemResource(ni3File);
			helper.addAttachment(ni3Name, file);
			log.debug("sending message...");
			mailSender.send(message);
			log.debug("done");
		} catch (MessagingException ex){
			log.error("error sending mail", ex);
			throw new ACException(TextID.MsgErrorSendStarterModuleToClient, new String[] { "" + ex + ":" + ex.getMessage() });
		} catch (MailSendException ex){
			log.error("error sending mail", ex);
			throw new ACException(TextID.MsgErrorSendStarterModuleToClient, new String[] { "" + ex + ":" + ex.getMessage() });
		} finally{
			tempSettingsFile.delete();
			starterFile.delete();
			ni3File.delete();
		}
	}

	private File createTempStarterFile(String suffix, String source, User targetUser) throws ACException{
		InputStream ris = null;
		FileOutputStream fos = null;
		try{
			File ni3ExecutableFile = File.createTempFile(FileNameValidator.getNameWOSpecialChars(targetUser.getUserName())
			        + System.currentTimeMillis(), suffix);
			fos = new FileOutputStream(ni3ExecutableFile);

			ris = getClass().getClassLoader().getResourceAsStream(source);
			byte[] buf = new byte[1024];
			while (ris.available() > 0){
				int count = ris.read(buf, 0, 1024);
				fos.write(buf, 0, count);
			}
			return ni3ExecutableFile;
		} catch (IOException e){
			log.error("Error create temp file with user settings", e);
			throw new ACException(TextID.MsgErrorSendStarterModuleToClient, new String[] { "" + e + ":" + e.getMessage() });
		} finally{
			if (fos != null)
				try{
					fos.close();
				} catch (IOException e){
					log.error("failed to close FileOutputStream", e);
				}
			if (ris != null)
				try{
					ris.close();
				} catch (IOException e){
					log.error("failed to close FileOutputStream", e);
				}
		}
	}

	private File createTempSettingsFile(User targetUser, boolean useSSO) throws ACException{
		log.debug("generating settings");
		String dbid = ThreadLocalStorage.getInstance().getCurrentDatabaseInstanceId();
		log.debug("current DBID: " + dbid);
		int index = -1;
		int i = 1;
		log.debug("searching for configs");
		while (true){
			String name = props.getProperty("com.ni3.ag.adminconsole.instance" + i + ".dbid");
			log.debug("dbid for index(" + i + ") = " + name);
			if (name == null)
				break;
			if (name.equals(dbid)){
				index = i;
				break;
			}
			i++;
		}
		if (index == -1){
			log.error("error resolve settings for instance " + dbid + " please fill or correct offline.properties file");
			throw new ACException(TextID.MsgErrorSendStarterModuleToClient,
			        new String[] { "offline.properties does not contain configs for current instance (" + dbid + ")" });
		}
		String propertyNameBase = "com.ni3.ag.adminconsole.instance" + index;
		StringBuffer sb = new StringBuffer();
		for (Object o : props.keySet()){
			String s = (String) o;
			if (!s.startsWith(propertyNameBase))
				continue;
			if (s.startsWith(propertyNameBase + ".dbid"))
				continue;
			log.debug("found property: " + s);
			sb.append(s.replace(propertyNameBase + ".", "")).append("=").append(props.get(o)).append("\r\n");
		}
		if (useSSO)
			sb.append("offline.client.sid=").append(targetUser.getSID()).append("\r\n");
		log.debug("create temp file to store settings");
		FileOutputStream fos = null;
		try{
			File tempSettingsFile = File.createTempFile(FileNameValidator.getNameWOSpecialChars(targetUser.getUserName())
			        + System.currentTimeMillis(), "properties");
			fos = new FileOutputStream(tempSettingsFile);
			log.debug("store user settings:\n" + sb.toString());
			fos.write(sb.toString().getBytes("UTF-8"));
			return tempSettingsFile;
		} catch (IOException e){
			log.error("Error create temp file with user settings", e);
			throw new ACException(TextID.MsgErrorSendStarterModuleToClient, new String[] { "" + e + ":" + e.getMessage() });
		} finally{
			if (fos != null)
				try{
					fos.close();
				} catch (IOException e){
					log.error("failed to close FileOutputStream", e);
				}
		}
	}

	private Language getLanguageForUser(User targetUser){
		targetUser = userDAO.getById(targetUser.getId());
		Hibernate.initialize(targetUser.getSettings());
		Language l = new Language();
		l.setId(1);
		for (UserSetting s : targetUser.getSettings()){
			if (s.getProp().equals(Setting.LANGUAGE_PROPERTY)){
				l.setId(Integer.valueOf(s.getValue()));
				return l;
			}
		}
		return l;
	}

}
