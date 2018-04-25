/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.reports;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class RefreshReportButtonListener extends ProgressActionListener{

	public RefreshReportButtonListener(AbstractController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		getController().reloadCurrent();
	}

}
