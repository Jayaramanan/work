/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.client.model;

import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.MainPanel;
import com.ni3.ag.navigator.shared.domain.User;

public class SystemGlobals{
	private static User user = null;
	public static int GroupID = -1;
	public static String Instance = "default";

	public static MainPanel MainFrame;

	public static String MetaphorURL = "";
	public static String IconURL;
	// TODO load from arguments or settings

	public static Ni3 theApp;

	public static String ServerURL;

	public static String PureServerURL;

	public static String DateFormat = "MM/dd/yyyy";

	public static boolean isThickClient = false;
	public static boolean isSiebelIntegrationModeEnabled = false;

	private static Boolean marathonTesting = null;

	public static String getUserHomeDir(){
		return System.getProperty("user.home");
	}

	public static User getUser(){
		return user;
	}

	public static int getUserId(){
		return user != null ? user.getId() : -1;
	}

	public static void setUser(User user){
		SystemGlobals.user = user;
	}

	public static boolean isMarathonTesting(){
		if (marathonTesting == null){
			marathonTesting = UserSettings.getBooleanAppletProperty("MarathonTesting", false);
		}
		return marathonTesting;
	}
	
	public static String getMetaphorSet(){
		return MainFrame != null ? MainFrame.Doc.getMetaphorSet() : null;
	}
}
