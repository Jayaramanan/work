/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ni3.ag.adminconsole.client.view.AbstractView;

public class TabChangeListener implements ChangeListener{

	public TabChangeListener(){
	}

	public void stateChanged(ChangeEvent e){
		if (e.getSource() != null){
			AbstractController controller = getSelectedController(e.getSource());
			if (controller != null){
				if (!controller.isInitialized()){
					controller.initializeController();
				} else{
					controller.clearData();
					controller.reloadData();
				}
				((AbstractView) controller.getView()).restoreSelection();
			}
		}
	}

	protected AbstractController getSelectedController(Object source){
		if (source != null && source instanceof JTabbedPane){
			JTabbedPane pane = (JTabbedPane) source;
			Object component = pane.getSelectedComponent();
			ControllerContainer controllerContainer = ControllerContainer.getInstance();
			if (component != null){
				if (component instanceof AbstractView){
					return controllerContainer.getController((AbstractView) component);
				} else{
					return getSelectedController(component);
				}
			}
		}
		return null;
	}

}