/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.util.passwordencoder;

import com.ni3.ag.navigator.client.util.URLEx;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.util.passwordencoder.BlowFishPasswordEncoder;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordEncoder;
import com.ni3.ag.navigator.shared.util.passwordencoder.SimplePasswordEncoder;

public class PasswordUtil{
	private static PasswordUtil instance;
	static{
		instance = new PasswordUtil();
	}

	private PasswordEncoder passwordEncoder;

	public static synchronized PasswordUtil getInstance(){
		return instance;
	}

	private PasswordUtil(){
	}

	public PasswordEncoder getPasswordEncoder(){
		if (passwordEncoder == null){
			passwordEncoder = getEncoder();
		}
		return passwordEncoder != null ? passwordEncoder : getDefaultEncoder();
	}

	private PasswordEncoder getEncoder(){
		URLEx url = new URLEx(ServletName.SettingsProvider);
		url.addParam(RequestParam.propertyName, PasswordEncoder.PASSWORD_ENCODER_PROPERTY);
		url.closeOutput(null);
		String line = null;
		line = url.readLine();
		url.close();

		PasswordEncoder inst = null;
		if (line != null && !line.isEmpty()){
			inst = getEncoderInstance(line);
		}
		return inst;
	}

	private PasswordEncoder getDefaultEncoder(){
		return getEncoderInstance(PasswordEncoder.DEFAULT_PASSWORD_ENCODER);
	}

	private PasswordEncoder getEncoderInstance(String line){
		PasswordEncoder inst = null;
		try{
			inst = (PasswordEncoder) Class.forName(line).newInstance();
			// TODO not the best implementation - temporary solution
			if (inst instanceof BlowFishPasswordEncoder)
				((BlowFishPasswordEncoder) inst).setPasswordSaltGetter(new RemotePasswordSaltGetterImpl());
		} catch (InstantiationException e){
			e.printStackTrace();
		} catch (IllegalAccessException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		if (inst == null)
			inst = new SimplePasswordEncoder();
		return inst;
	}

}
