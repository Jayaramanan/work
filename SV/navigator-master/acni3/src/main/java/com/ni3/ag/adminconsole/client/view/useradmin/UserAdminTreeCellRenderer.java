/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useradmin;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;


import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class UserAdminTreeCellRenderer extends ACTreeCellRenderer{

	private static final long serialVersionUID = 1L;
	private ImageIcon groupMembersIcon, groupPrivilegesIcon, groupScopeIcon, offlineClientIcon, chartsIcon;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus){
		Object dispValue = value;
		Icon icon = null;
		if (value instanceof String){
			String item = (String) value;
			if (item.equals(Translation.get(TextID.GroupMembers))){
				icon = getGroupMembersIcon();
			} else if (item.equals(Translation.get(TextID.GroupPrivileges))){
				icon = getGroupPrivilegesIcon();
			} else if (item.equals(Translation.get(TextID.GroupScope))){
				icon = getGroupScopeIcon();
			} else if (item.equals(Translation.get(TextID.OfflineClient))){
				icon = getOfflineClientIcon();
			} else if (item.equals(Translation.get(TextID.Charts))){
				icon = getChartsIcon();
			}

		}

		Component c = super.getTreeCellRendererComponent(tree, dispValue, selected, expanded, leaf, row, hasFocus);
		if (icon != null)
			setIcon(icon);
		return c;
	}

	private ImageIcon getGroupMembersIcon(){
		if (groupMembersIcon == null){
			groupMembersIcon = new ImageIcon(ACMain.class.getResource("/images/User16.png"));
		}
		return groupMembersIcon;
	}

	private ImageIcon getChartsIcon(){
		if (chartsIcon == null){
			chartsIcon = new ImageIcon(ACMain.class.getResource("/images/Chart16.png"));
		}
		return chartsIcon;
	}

	private ImageIcon getGroupPrivilegesIcon(){
		if (groupPrivilegesIcon == null){
			groupPrivilegesIcon = new ImageIcon(ACMain.class.getResource("/images/Access16.png"));
		}
		return groupPrivilegesIcon;
	}

	private ImageIcon getOfflineClientIcon(){
		if (offlineClientIcon == null){
			offlineClientIcon = new ImageIcon(ACMain.class.getResource("/images/Computer16.png"));
		}
		return offlineClientIcon;
	}

	private ImageIcon getGroupScopeIcon(){
		if (groupScopeIcon == null){
			groupScopeIcon = new ImageIcon(ACMain.class.getResource("/images/Scope16.png"));
		}
		return groupScopeIcon;
	}
}
