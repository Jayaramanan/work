/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.useradmin;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.test.ACTestCase;
import com.ni3.ag.adminconsole.domain.User;

public class UpdateUserActionListenerTest extends ACTestCase{

	private final static Logger log = Logger.getLogger(UpdateUserActionListenerTest.class);

	User user;
	UpdateUserActionListener ls;

	@Override
	protected void setUp() throws Exception{
		user = new User();
		ls = new UpdateUserActionListener(new UserAdminController());
	}

	public void testCheckSIDNull(){
		user.setUserName("name");
		user.setSID(null);
		ls.checkSID(user);
		assertEquals(getMD5("name4Ni3"), user.getSID());
	}

	public void testCheckSIDDifferent(){
		user.setUserName("name");
		user.setSID("sid");
		ls.checkSID(user);
		assertEquals(getMD5("name4Ni3"), user.getSID());
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

}
