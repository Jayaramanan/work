/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ViewMapDirButtonListener extends ProgressActionListener{

	private MapJobController controller;

	public ViewMapDirButtonListener(MapJobController controller){
		super(controller);
		this.controller = controller;
	}

	@Override
	public void performAction(ActionEvent e){
		DatabaseInstance di = SessionData.getInstance().getCurrentDatabaseInstance();
		String path = di.getMapPath();
		if (path == null){
			List<ErrorEntry> errors = new ArrayList<ErrorEntry>();
			errors.add(new ErrorEntry(TextID.MsgPathToMapsNotConfigured));
			controller.getView().renderErrors(errors);
		} else
			showInBrowser(path);
	}

	private boolean showInBrowser(String url){
		String os = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();
		try{
			if (os.indexOf("win") >= 0){
				rt.exec("cmd /c start " + url);
			}
		} catch (IOException e){
			return false;
		}
		return true;
	}

}
