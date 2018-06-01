/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.client.view.AbstractView;

public class ControllerContainer{
	private List<AbstractController> controllers;

	private static ControllerContainer container = null;

	private ControllerContainer(){
	}

	public static ControllerContainer getInstance(){
		if (container == null){
			container = new ControllerContainer();
		}
		return container;
	}

	public void addController(AbstractController controller){
		if (controllers == null){
			controllers = new ArrayList<AbstractController>();
		}
		controllers.add(controller);
	}

	public AbstractController getController(AbstractView view){
		for (AbstractController controller : controllers){
			if (controller.getView() == view){
				return controller;
			}
		}
		return null;
	}
}
