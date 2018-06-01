/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.passadmin.impl;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.SmtpAuthenticator;
import com.ni3.ag.navigator.server.passadmin.PasswordSender;
import com.ni3.ag.navigator.shared.domain.User;

public class PasswordSenderImpl extends JdbcDaoSupport implements PasswordSender{
	private static final Logger log = Logger.getLogger(PasswordSenderImpl.class);
	private static final String MAIL_PROP_FILE = "/mail.properties";
	private static final String SMTP_PROPERTY = "com.ni3.ag.adminconsole.mail.smtp";
	private static final String PORT_PROPERTY = "com.ni3.ag.adminconsole.mail.port";
	private static final String FROM_PROPERTY = "com.ni3.ag.adminconsole.mail.from";
	private static final String USERNAME_PROPERTY = "com.ni3.ag.adminconsole.mail.username";
	private static final String PASSWORD_PROPERTY = "com.ni3.ag.adminconsole.mail.password";

	private String smtp;
	private Integer port;
	private String from;
	private String userName;
	private String password;

	public PasswordSenderImpl() throws IOException{
		loadMailProperties();
	}

	private void loadMailProperties() throws IOException{
		Properties properties = new Properties();
		try{
			properties.load(getClass().getResourceAsStream(MAIL_PROP_FILE));
		} catch (IOException e){
			log.error("mail.properties file not found");
			throw e;
		}
		smtp = properties.getProperty(SMTP_PROPERTY);
		port = Integer.parseInt(properties.getProperty(PORT_PROPERTY, "465"));
		from = properties.getProperty(FROM_PROPERTY);
		userName = properties.getProperty(USERNAME_PROPERTY);
		password = properties.getProperty(PASSWORD_PROPERTY);

	}

	@Override
	public boolean sendEmail(User user, String newPass){
		Integer langID = getLangByUser(user);

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.socketFactory.port", port);
		props.put("mail.smtp.socketFactory.fallback", "false");

		Session session = Session.getInstance(props, new SmtpAuthenticator(userName, password));
		MimeMessage message = new MimeMessage(session);
		try{
			message.setFrom(new InternetAddress(from));

			InternetAddress to_address = new InternetAddress(user.geteMail());
			message.addRecipient(javax.mail.Message.RecipientType.TO, to_address);

			final String subjectTemplate = getTranslation(TextID.MsgEmailPasswordResetSubject, langID);
			String subject = makeReplacement(subjectTemplate, new Object[] { user.getUserName() });
			message.setSubject(subject);

			Object[] params = new Object[] { user.getFirstName(), user.getLastName(), user.getUserName(), newPass };
			final String textTemplate = getTranslation(TextID.MsgNavEMailPasswordResetText, langID);
			String text = makeReplacement(textTemplate, params);
			message.setText(text);

			Transport transport = session.getTransport("smtp");
			transport.connect(smtp, -1, null, null);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
			if (log.isDebugEnabled()){
				log.debug("Password sent to email: " + user.geteMail());
			}
			return true;
		} catch (Exception e){
			log.error(e);
			return false;
		}
	}

	private String getTranslation(TextID textID, Integer langID){
		String result = textID.toString();
		String sql = "select value from sys_user_language where prop = ? and languageid = ?";
		final String trl = (String) getJdbcTemplate().queryForObject(sql, new Object[] { textID.toString(), langID },
		        String.class);
		if (trl != null){
			result = trl;
		}
		return result;
	}

	private String makeReplacement(String text, Object[] params){
		if (text != null && params != null && params.length > 0){
			for (int i = 0; i < params.length; i++){
				text = text.replace("{" + (i + 1) + "}", (params[i] == null ? "" : params[i].toString()));
			}
		}
		return text;
	}

	public Integer getLangByUser(User u){
		Integer langId = 1;
		String sql = "select value from sys_user_settings where prop = 'Language' and userid = ?";
		final String langStr = (String) getJdbcTemplate().queryForObject(sql, new Object[] { u.getId() }, String.class);
		if (langStr != null){
			try{
				langId = Integer.parseInt(langStr);
			} catch (NumberFormatException e){
				log.warn("Wrong number format for value:" + langStr);
			}
			String sql1 = "select id from cht_language where id = ?";
			int id = getJdbcTemplate().queryForInt(sql1, new Object[] { langId });
			if (id == langId){
				langId = id;
			}
		}
		return langId;
	}

}
