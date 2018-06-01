/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.licenses;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.license.LicenseData;

public class LicenseTreeCellRenderer extends ACTreeCellRenderer{

	private static final long serialVersionUID = 1L;
	private ImageIcon activeIcon, expiredIcon, notStartedIcon, invalidIcon;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus){
		Object dispValue = value;
		Icon icon = null;
		if (value instanceof LicenseData){
			LicenseData lData = (LicenseData) value;
			dispValue = lData.getLicense().getProduct();

			switch (lData.getStatus()){
				case Active:
					icon = getActiveLicenseIcon();
					break;
				case Expired:
					icon = getExpiredLicenseIcon();
					break;
				case NotStarted:
					icon = getNotStartedLicenseIcon();
					break;
				case Invalid:
					icon = getInvalidLicenseIcon();
					break;
			}

		}

		Component c = super.getTreeCellRendererComponent(tree, dispValue, selected, expanded, leaf, row, hasFocus);
		if (icon != null)
			setIcon(icon);
		return c;
	}

	private ImageIcon getActiveLicenseIcon(){
		if (activeIcon == null){
			activeIcon = new ImageIcon(ACMain.class.getResource("/images/ActiveLicense16.png"));
		}
		return activeIcon;
	}

	private ImageIcon getExpiredLicenseIcon(){
		if (expiredIcon == null){
			expiredIcon = new ImageIcon(ACMain.class.getResource("/images/ExpiredLicense16.png"));
		}
		return expiredIcon;
	}

	private ImageIcon getNotStartedLicenseIcon(){
		if (notStartedIcon == null){
			notStartedIcon = new ImageIcon(ACMain.class.getResource("/images/NotStartedLicense16.png"));
		}
		return notStartedIcon;
	}

	private ImageIcon getInvalidLicenseIcon(){
		if (invalidIcon == null){
			invalidIcon = new ImageIcon(ACMain.class.getResource("/images/InvalidLicense16.png"));
		}
		return invalidIcon;
	}
}
