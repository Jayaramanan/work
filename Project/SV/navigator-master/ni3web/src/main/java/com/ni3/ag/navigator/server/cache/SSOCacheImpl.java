package com.ni3.ag.navigator.server.cache;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SSOCacheImpl implements SSOCache{
	private static final Logger log = Logger.getLogger(SSOCache.class);

	private static final String SSOClass = "com.ni3.ag.navigator.server.cache.SSOCache.SSOClass";
	private static final String SSOJar = "com.ni3.ag.navigator.server.cache.SSOCache.SSOJar";

	private static class SSOClass{
		public String SSOJar;
		public String SSOClass;
	}

	private SSOClass SSO;

	public String getSSOUsername(String token){
		if (SSO == null){
			Properties prop = com.ni3.ag.navigator.server.util.ServerSettings.loadPropertyFile();

			SSO = new SSOClass();

			SSO.SSOJar = prop.getProperty(SSOJar, "");
			SSO.SSOClass = prop.getProperty(SSOClass, "");
		}

		try{
			URLClassLoader clazzLoader;
			String filePath;
			URL url;

			filePath = SSO.SSOJar;
			url = new File(filePath).toURI().toURL();
			clazzLoader = new URLClassLoader(new URL[] { url });
			log.info("SSO jar URL: " + url);
			Object validate = clazzLoader.loadClass(SSO.SSOClass).newInstance();

			Method getUserData = validate.getClass().getMethod("getUserData", String.class);

			Object ret = getUserData.invoke(validate, token);

			return (String) (ret.getClass().getField("username").get(ret));
		} catch (ClassNotFoundException e1){
			log.error("cannot load class " + SSO.SSOClass + " from " + SSO.SSOJar, e1);
			return "***\nToken engine not running properly";
		} catch (Exception e){
			if (e.getCause() != null)
				return "***\n" + e.getCause().getMessage();
			else
				return "***\nInvalid token (unknown error)";
		}
	}

}
