/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.passadmin.impl;

import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.util.passwordencoder.BlowFishPasswordEncoder;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.util.PasswordGenerator;
import com.ni3.ag.adminconsole.util.SimplePasswordGenerator;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.passadmin.PasswordReseter;
import com.ni3.ag.navigator.server.passadmin.PasswordSender;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordEncoder;
import com.ni3.ag.navigator.shared.util.passwordencoder.SimplePasswordEncoder;

public class PasswordReseterImpl implements PasswordReseter{
	private static final Logger log = Logger.getLogger(PasswordReseterImpl.class);
	private static final String PASSWORD_ENCODER_PROP_FILE = "/Ni3Web.properties";
	private final PasswordGenerator passwordGenerator;
	private PasswordEncoder passwordEncoder;
	private final PasswordSender passwordSender;

	private static NSpringFactory daoFactory = NSpringFactory.getInstance();

	public PasswordReseterImpl() throws Exception{
		passwordGenerator = new SimplePasswordGenerator();
		((SimplePasswordGenerator) passwordGenerator).setLength(8);
		loadPasswordEncoder();
		passwordSender = NSpringFactory.getInstance().getPasswordSender();;
	}

	private void loadPasswordEncoder() throws Exception{
		final Properties properties = new Properties();
		String className = null;
		try{
			properties.load(getClass().getResourceAsStream(PASSWORD_ENCODER_PROP_FILE));
			final String propertyToSearch = PasswordEncoder.PASSWORD_ENCODER_PROPERTY;
			className = properties.getProperty(propertyToSearch, PasswordEncoder.DEFAULT_PASSWORD_ENCODER);

			passwordEncoder = (PasswordEncoder) Class.forName(className).newInstance();
			if (passwordEncoder instanceof BlowFishPasswordEncoder)
				((BlowFishPasswordEncoder) passwordEncoder).setPasswordSaltGetter(NSpringFactory.getInstance()
				        .getPasswordSaltGetter());
		} catch (final ClassNotFoundException e){
			log.error("Class not found: " + className + ", error: " + e);
			throw e;
		} catch (final InstantiationException e){
			log.error(e);
			throw e;
		} catch (final IllegalAccessException e){
			throw e;
		} catch (final IOException e){
			log.error(e);
			throw e;
		}
		if (passwordEncoder == null){
			passwordEncoder = new SimplePasswordEncoder();
		}

	}

	@Override
	public boolean resetPassword(final String eMail){
		boolean result = false;

		final UserDAO userDao = daoFactory.getUserDao();

		final List<User> list = userDao.findByEmail(eMail);

		if (list.size() > 0){
			User user = list.get(0);

			final String newPass = passwordGenerator.generatePassword();
			final String encoded = passwordEncoder.generate(newPass);

			if (updatePassword(user.getId(), encoded)){
				result = passwordSender.sendEmail(user, newPass);
			}
		}

		return result;
	}

	private boolean updatePassword(final Integer id, final String encoded){
		final UserDAO userDao = daoFactory.getUserDao();

		final User user = userDao.get(id);
		user.setPassword(encoded);
		userDao.update(user);

		return true;
	}

}
