/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.common;

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.ni3.ag.adminconsole.client.controller.AbstractController;
import com.ni3.ag.adminconsole.client.controller.ProgressListener;

public class ACTree extends JTree{
	private static final long serialVersionUID = 7380819744288744085L;
	private AbstractController controller;
	private ProgressListener progressListener;
	private TreePath path;

	public void setCurrentController(AbstractController controller){
		this.controller = controller;
		progressListener = new ProgressListener(){
			@Override
			public void performAction(ActionEvent e){
				performTreeSelection(path);
			}
		};
	}

	@Override
	public void setSelectionPath(TreePath path){
		this.path = path;
		progressListener.performWithWaitCursor(controller.getView(), null);
	}

	private void performTreeSelection(TreePath path){
		if (getSelectionPath() != null && path != null && !path.equals(getSelectionPath())){
			if (controller != null && !controller.canSwitch(true)){
				return;
			}
		}
		super.setSelectionPath(path);
	}
}
