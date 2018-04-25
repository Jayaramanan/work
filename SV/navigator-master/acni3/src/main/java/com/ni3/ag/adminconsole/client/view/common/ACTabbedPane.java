/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ControllerContainer;
import com.ni3.ag.adminconsole.client.controller.ProgressListener;
import com.ni3.ag.adminconsole.client.view.AbstractView;

public class ACTabbedPane extends JTabbedPane{
	private static final long serialVersionUID = 8101924882459603707L;
	private int oldIndex = 0;
	private int newIndex = 0;
	private ProgressListener progressListener;

	public ACTabbedPane(){
		progressListener = new ProgressListener(){
			@Override
			public void performAction(ActionEvent e){
				performTabChange(newIndex);
			}
		};
	}

	@Override
	public void setSelectedIndex(int newIndex){
		this.newIndex = newIndex;
		progressListener.performWithWaitCursor(this, null);
	}

	private void performTabChange(int newIndex){
		if (oldIndex != newIndex){

			AbstractController controller = getSelectedController(this);
			if (controller == null || controller.canSwitch(false)){
				super.setSelectedIndex(newIndex);
				oldIndex = newIndex;
			}
		} else{
			super.setSelectedIndex(oldIndex);
		}
	}

	protected AbstractController getSelectedController(JTabbedPane pane){
		Object component = pane.getSelectedComponent();
		ControllerContainer controllerContainer = ControllerContainer.getInstance();
		if (component != null){
			if (component instanceof AbstractView){
				return controllerContainer.getController((AbstractView) component);
			} else{
				return (component instanceof JTabbedPane) ? getSelectedController((JTabbedPane) component) : null;
			}
		}

		return null;
	}
}
