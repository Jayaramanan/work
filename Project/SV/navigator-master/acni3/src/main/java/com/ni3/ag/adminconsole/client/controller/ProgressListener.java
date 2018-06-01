/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class ProgressListener{
	private static final Cursor WAIT_CURSOR = new Cursor(Cursor.WAIT_CURSOR);

	public void performWithWaitCursor(Component c, ActionEvent e){
		c.setCursor(WAIT_CURSOR);
		try{
			performAction(e);
		} finally{
			c.setCursor(null);
		}
	}

	public abstract void performAction(ActionEvent e);
}
