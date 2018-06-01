/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

public class SystemIdentification{

	private static final Logger log = Logger.getLogger(SystemIdentification.class);

	public static String getSystemId(){
		String result = "";
		try{
			File file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new java.io.FileWriter(file);

			String vbs = "Set objWMIService = GetObject(\"winmgmts:{impersonationLevel=delegate}!\\\\.\\root\\cimv2\")\n"
			        + "Set colItems = objWMIService.ExecQuery(\"SELECT ProcessorId, SystemName FROM Win32_Processor\")\n"
			        + "For Each objItem In colItems\n" + "WScript.Echo objItem.ProcessorId\n"
			        + "WScript.Echo objItem.SystemName\n" + "Next\n"
			        + "Set colItems = objWMIService.ExecQuery(\"SELECT SerialNumber from Win32_OperatingSystem\")\n"
			        + "For Each objItem In colItems\n" + "WScript.Echo objItem.SerialNumber\n" + "Next\n";

			fw.write(vbs.toString());
			fw.close();
			Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null){
				result += line;
			}
			input.close();

		} catch (Exception e){
			log.error(e);
		}
		return getMD5(result.trim());
	}

	private static String getMD5(String string){
		Logger.getLogger(SystemIdentification.class).info("Plain system identificator: " + string);
		try{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(string.getBytes("UTF-8"));
			byte messageDigest[] = md5.digest();

			StringBuilder hexString = new StringBuilder();
			for (int i = 0; i < messageDigest.length; i++){
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			}
			Logger.getLogger(SystemIdentification.class).info("encoded system Id: " + hexString.toString());
			return hexString.toString();
		} catch (NoSuchAlgorithmException e){
			Logger.getLogger(SystemIdentification.class).error("", e);
		} catch (UnsupportedEncodingException e){
			Logger.getLogger(SystemIdentification.class).error("", e);
		}
		return "";
	}
}
