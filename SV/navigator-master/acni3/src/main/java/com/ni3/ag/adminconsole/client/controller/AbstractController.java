/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.AbstractView;
import com.ni3.ag.adminconsole.client.view.SavePromptDialog;
import com.ni3.ag.adminconsole.domain.ApplicationSetting;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.domain.UserSetting;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;

public abstract class AbstractController{

	private Logger log = Logger.getLogger(AbstractController.class);

	private boolean initialized = false;

	private SavePromptDialog saveDialog = null;

	protected abstract void populateDataToView(AbstractModel model, Component view);

	protected abstract void populateDataToModel(AbstractModel model, Component view);

	protected abstract void initializeListeners(AbstractModel model, Component view);

	public abstract Component getView();

	public abstract void setView(Component c);

	public abstract AbstractModel getModel();

	public abstract void setModel(AbstractModel m);

	public abstract void reloadData();

	public abstract void clearData();

	public abstract boolean save();

	public abstract void reloadCurrent();

	public boolean isInitialized(){
		return initialized;
	}

	public boolean canSwitch(boolean reloadCurrent){
		AbstractView view = ((AbstractView) getView());
		if (view.isChanged()){
			if (saveDialog == null){
				saveDialog = new SavePromptDialog();
			}
			String action = SessionData.getInstance().getTabSwitchAction();
			if (action == null || SavePromptDialog.ALWAYS_ASK.equals(action)){
				action = saveDialog.getSelectedAction();
			}
			if (saveDialog.getRememberDecision() && !action.equals(SavePromptDialog.CANCEL_ACTION)){
				saveUserDecision(action);
			}
			if (action.equals(SavePromptDialog.SAVE_ACTION)){
				boolean saved = save();
				if (reloadCurrent && saved){
					reloadCurrent();
				}
				log.debug("Data saved: " + saved + ", controller = " + this);
				return saved;
			} else if (action.equals(SavePromptDialog.DISCARD_ACTION) && reloadCurrent){
				reloadCurrent();
			} else if (action.equals(SavePromptDialog.CANCEL_ACTION)){
				return false;
			}
		}
		return true;
	}

	public void saveUserDecision(String action){
		SessionData.getInstance().setTabSwitchAction(action);
		User u = SessionData.getInstance().getUser();
		UserAdminService uss = ACSpringFactory.getInstance().getUserAdminService();
		u = uss.getUser(u.getId());
		boolean found = false;
		for (UserSetting us : u.getSettings()){
			if (us.getProp().equals(ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY)){
				us.setValue(action);
				found = true;
				break;
			}
		}
		if (!found)
			u.getSettings().add(
			        new UserSetting(u, ApplicationSetting.APPLET_SECTION, ApplicationSetting.TAB_SWITCH_ACTION_PROPERTY,
			                action));
		List<User> users = new ArrayList<User>();
		users.add(u);
		uss.updateUsers(users);
	}

	public void initializeController(){
		((AbstractView) getView()).initializeComponents();
		populateDataToView(getModel(), getView());
		initializeListeners(getModel(), getView());
		((AbstractView) getView()).setVisible(true);
		initialized = true;
	}
}
