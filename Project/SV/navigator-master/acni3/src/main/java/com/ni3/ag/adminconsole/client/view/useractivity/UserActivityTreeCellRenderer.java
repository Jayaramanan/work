/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.view.useractivity;

import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.applet.ACMain;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.client.view.common.ACTreeCellRenderer;
import com.ni3.ag.adminconsole.domain.UserActivity;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.util.TimeUtil;

public class UserActivityTreeCellRenderer extends ACTreeCellRenderer{

	private static final long serialVersionUID = 1L;
	private boolean showUser;
	private ImageIcon actionIcon;

	public UserActivityTreeCellRenderer(boolean showUser){
		this.showUser = showUser;
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus){
		ImageIcon currentIcon = null;
		Object dispValue = value;
		if (showUser){
			if (value instanceof UserActivity){
				UserActivity ua = (UserActivity) value;
				dispValue = ua.getUser().getUserName();

				if (ua.isLoginActivity() || ua.isSyncLoginActivity())
					dispValue = appendWithDuration(ua, dispValue);
				else if (ua.isSync())
					dispValue = appendWithSync(ua, dispValue);
				else if (ua.isGetModuleActivity())
					dispValue = appendWithModuleNameAndVersion(ua, dispValue);

				currentIcon = getUserIcon();
			} else if (value instanceof UserActivityType){
				UserActivityType uat = (UserActivityType) value;
				dispValue = Translation.get(uat.getValue());
				currentIcon = getActionIcon();
			}
		} else{
			if (value instanceof UserActivity){
				UserActivity ua = (UserActivity) value;
				dispValue = Translation.get(ua.getUserActivityType().getValue());

				if (ua.isLoginActivity() || ua.isSyncLoginActivity())
					dispValue = appendWithDuration(ua, dispValue);
				else if (ua.isSync())
					dispValue = appendWithSync(ua, dispValue);
				else if (ua.isGetModuleActivity())
					dispValue = appendWithModuleNameAndVersion(ua, dispValue);

				currentIcon = getActionIcon();
			}
		}

		Component c = super.getTreeCellRendererComponent(tree, dispValue, selected, expanded, leaf, row, hasFocus);
		if (currentIcon != null){
			setIcon(currentIcon);
		}
		return c;
	}

	private Object appendWithModuleNameAndVersion(UserActivity ua, Object val){
		String s = "" + val;
		if (ua.getRequest() == null)
			return s;
		String[] params = ua.getRequest().split("&");
		String module = null;
		String vers = null;
		for (String param : params){
			if (param.startsWith("module="))
				module = param.substring("module=".length());
			else if (param.startsWith("version="))
				vers = param.substring("version=".length());
		}
		try{
			s += " (" + URLDecoder.decode(module, "UTF-8") + ": " + URLDecoder.decode(vers, "UTF-8") + ")";
		} catch (UnsupportedEncodingException e){
			Logger.getLogger(getClass()).error("Error making activity string", e);
		}
		return s;
	}

	private Object appendWithSync(UserActivity ua, Object dispValue){
		return dispValue + " (" + Translation.get(TextID.Sync) + ")";
	}

	private Object appendWithDuration(UserActivity ua, Object dispValue){
		Long duration = ua.getSessionDuration();
		String time = "-";
		if (duration != null && duration > 0){
			time = TimeUtil.getFormattedTime(duration);
		}
		return dispValue + " (" + time + ")";
	}

	private ImageIcon getActionIcon(){
		if (actionIcon == null){
			actionIcon = new ImageIcon(ACMain.class.getResource("/images/Action16.png"));
		}
		return actionIcon;
	}
}