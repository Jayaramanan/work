/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.shared.util.passwordencoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class MD5PasswordEncoder implements PasswordEncoder{

	public String encode(String login, String pass){
		if (pass == null || pass.length() == 0){
			return pass;
		}
		MessageDigest digest;
		try{
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(pass.getBytes("UTF-8"));
			byte[] hash = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte aHash : hash){
				String s = Integer.toHexString(0xFF & aHash);
				if (s.length() == 1)
					s = "0" + s;
				hexString.append(s);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e){
			Logger.getLogger(MD5PasswordEncoder.class).error("", e);
		} catch (UnsupportedEncodingException e){
			Logger.getLogger(MD5PasswordEncoder.class).error("", e);
		}
		return pass;
	}

	@Override
	public String generate(String pass){
		return encode(null, pass);
	}

}
