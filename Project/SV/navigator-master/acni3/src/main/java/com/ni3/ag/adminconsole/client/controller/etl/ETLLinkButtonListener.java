/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.etl;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;

public class ETLLinkButtonListener extends ProgressActionListener{
	private static final Logger log = Logger.getLogger(ETLLinkButtonListener.class);
	private String urlStr = "http://etl.office.ni3.net/adeptia/control/login.jsp?clientBrowserWidth=10&imageField=Login&";

	public ETLLinkButtonListener(ETLController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		SessionData sData = SessionData.getInstance();
		DatabaseInstance db = sData.getCurrentDatabaseInstance();
		if (db == null)
			return;
		if (!db.isConnected())
			return;

		User user = sData.getUser();

		String url = urlStr + "user=" + user.getEtlUser() + "&password=" + user.getEtlPassword();
		showInBrowser(url);
	}

	private boolean showInBrowser(String url){

		String osName = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();
		try{
			if (osName.indexOf("win") >= 0){
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else if (osName.indexOf("mac") >= 0){
				rt.exec("open " + url);
			} else{ // assume Unix or Linux
				String[] browsers = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori",
				        "kazehakase", "mozilla" };
				String browser = null;
				for (String b : browsers)
					if (browser == null && rt.exec(new String[] { "which", b }).getInputStream().read() != -1)
						rt.exec(new String[] { browser = b, url });
				if (browser == null)
					throw new Exception(Arrays.toString(browsers));
			}
		} catch (Exception e){
			log.error("Cannot launch url in browser", e);
			return false;
		}
		return true;
	}

}
