/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.thickclient.maps;

import java.awt.event.ActionEvent;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class AddMapJobListener extends ProgressActionListener{

	public AddMapJobListener(MapJobController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		MapJobController controller = (MapJobController) getController();
		controller.getView().clearErrors();
		controller.addNewMapJob();
	}
}
