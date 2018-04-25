/** * Copyright (c) 2009-2011 Ni3 AG. All rights reserved. */
package com.ni3.ag.adminconsole.shared.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder{
	private static final Logger log = Logger.getLogger(MD5PasswordEncoder.class);

	@Override
	public String generate(String src){
		return encode(null, src);
	}

	@Override
	public String encode(String login, String pass){
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
			log.error("Error make password digest: ", e);
		} catch (UnsupportedEncodingException e){
			log.error("Error make password digest: ", e);
		}
		return pass;
	}

}
