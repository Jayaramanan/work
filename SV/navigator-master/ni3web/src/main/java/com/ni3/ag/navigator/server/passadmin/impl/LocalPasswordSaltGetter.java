package com.ni3.ag.navigator.server.passadmin.impl;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordSaltGetter;
import org.apache.log4j.Logger;

public class LocalPasswordSaltGetter implements PasswordSaltGetter{
	private static final Logger log = Logger.getLogger(LocalPasswordSaltGetter.class);
	private static final int BCRYPT_PASSWORD_LEN = 60;
	private static final int BCRYPT_SALT_LEN = 29;

	@Override
	public String getSalt(String login){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		User user = userDAO.getByUsername(login);
		String pass = user.getPassword();
		if (pass.length() != BCRYPT_PASSWORD_LEN){
			log.warn("Requesting salt for password len: " + pass.length() + "  expected bcrypt pass len: "
			        + BCRYPT_PASSWORD_LEN);
			return null;
		}
		return pass.substring(0, BCRYPT_SALT_LEN);
	}
}
