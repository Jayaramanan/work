/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class AddOfflineJobListener extends ProgressActionListener{

	public AddOfflineJobListener(ThickClientController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		ThickClientController controller = (ThickClientController) getController();
		controller.getView().clearErrors();
		controller.addNewOfflineJob();
	}
}
