/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.ni3.ag.adminconsole.client.view.common.StrongTableCellRenderer;

public abstract class LicenseBooleanCellRenderer extends JPanel implements StrongTableCellRenderer{
	private static final long serialVersionUID = -3637817359360845249L;

	protected JCheckBox checkBox;

	public LicenseBooleanCellRenderer(){
		this.setLayout(new BorderLayout());
		checkBox = new JCheckBox();
		this.add(checkBox, BorderLayout.CENTER);
		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
	}

}
