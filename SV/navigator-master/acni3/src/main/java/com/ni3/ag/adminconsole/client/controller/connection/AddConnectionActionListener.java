/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller.connection;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.controller.ProgressActionListener;

public class AddConnectionActionListener extends ProgressActionListener{

	private Logger log = Logger.getLogger(AddConnectionActionListener.class);

	public AddConnectionActionListener(ObjectConnectionController controller){
		super(controller);
	}

	@Override
	public void performAction(ActionEvent e){
		log.debug("action performed");
		ObjectConnectionController controller = (ObjectConnectionController) getController();
		controller.getView().clearErrors();
		controller.addNewConnection();
	}
}
