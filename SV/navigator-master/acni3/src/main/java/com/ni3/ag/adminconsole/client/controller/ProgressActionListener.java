/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class ProgressActionListener extends ProgressListener implements ActionListener{
	private AbstractController controller;
	private static long finishTime = 0;

	public ProgressActionListener(AbstractController controller){
		this.controller = controller;
	}

	public AbstractController getController(){
		return controller;
	}

	public void setController(AbstractController controller){
		this.controller = controller;
	}

	@Override
	public void actionPerformed(ActionEvent e){
		long currentTime = EventQueue.getMostRecentEventTime();
		if (finishTime < currentTime){
			performWithWaitCursor(controller.getView(), e);
			finishTime = System.currentTimeMillis();
		}
	}

}
