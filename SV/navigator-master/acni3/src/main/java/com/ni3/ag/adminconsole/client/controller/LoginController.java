/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.client.service.ACSpringFactory;
import com.ni3.ag.adminconsole.client.session.ObjectVisibilityStore;
import com.ni3.ag.adminconsole.client.session.SessionData;
import com.ni3.ag.adminconsole.client.view.LoginView;
import com.ni3.ag.adminconsole.client.view.Translation;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.remoting.UserSession;
import com.ni3.ag.adminconsole.shared.db.DatabaseInstance;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.impl.LoginModel;
import com.ni3.ag.adminconsole.shared.service.def.DatabaseVersionService;
import com.ni3.ag.adminconsole.shared.service.def.LoginService;
import com.ni3.ag.adminconsole.shared.service.def.PasswordEncoder;
import com.ni3.ag.adminconsole.validation.ACException;
import com.ni3.ag.adminconsole.validation.ACLoginException;
import com.ni3.ag.adminconsole.validation.rules.ServerErrorContainerWrapper;

public class LoginController{
	private static final Logger log = Logger.getLogger(LoginController.class);

	private LoginModel model;
	private LoginView view;
	private boolean success = false;
	private PasswordEncoder passwordEncoder;

	private LoginController(){
	}

	public void setModel(LoginModel model){
		this.model = model;
	}

	public void setView(LoginView view){
		this.view = view;
		view.addLoginListener(new LoginListner());
		view.addCancelListener(new CancelListener());
		view.addEscListener(new EscapeListener());
	}

	class LoginListner implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			populateDataToModel();
			if (login(model.getUserName(), model.getPassword())){
				success = true;
				view.setVisible(false);

				DatabaseInstance dbid = SessionData.getInstance().getCurrentDatabaseInstance();
				ObjectVisibilityStore.getInstance().refreshLicenses(dbid);

				if (!checkDatabaseVersion()){
					success = false;
				}
			} else{
				reset();
				populateDataToView();
				view.requestFocus();
			}
		}

	}

	class CancelListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			view.setVisible(false);
			view.clearErrors();
		}
	}

	class EscapeListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e){
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
				view.setVisible(false);
				view.clearErrors();
			}
		}

		@Override
		public void keyReleased(KeyEvent e){

		}

		@Override
		public void keyTyped(KeyEvent e){

		}

	}

	public boolean login(String userName, String password){
		LoginService loginService = ACSpringFactory.getInstance().getLoginService();

		log.info("Trying to log in as " + userName);
		log.debug("hash for password: " + passwordEncoder.encode(userName, password));
		UserSession userSessionData;
		try{
			userSessionData = loginService.login(userName, passwordEncoder.encode(userName, password));
		} catch (ACLoginException ex){
			view.renderErrors(ex);
			return false;
		}
		view.clearErrors();
		if(userSessionData == null)
			return false;

		SessionData sdata = SessionData.getInstance();
		sdata.setUser(userSessionData.getUser());
		sdata.setSessionId(userSessionData.getSessionId());

		log.debug("logon successful");
		log.debug("     Session id: " + sdata.getSessionId());
		log.debug("     User id: " + sdata.getUserId());

		sdata.resolveUserTabSwitchAction(userSessionData.getUser());
		return true;
	}

	public void reset(){
		model.setUserName("");
		model.setPassword("");
		success = false;
	}

	private void populateDataToModel(){
		model.setUserName(view.getUserName());
		model.setPassword(view.getPassword());
	}

	private void populateDataToView(){
		view.setUserName(model.getUserName());
		view.setPassword(model.getPassword());
		view.setTitle(Translation.get(TextID.MsgLoginTo, new String[] { SessionData.getInstance()
		        .getCurrentDatabaseInstanceId() }));
	}

	public void run(){
		reset();
		populateDataToView();
		view.setVisible(true);
		view.requestFocus();
	}

	public boolean isSuccess(){
		return success;
	}

	private boolean checkDatabaseVersion(){
		DatabaseVersionService databaseVersionService = ACSpringFactory.getInstance().getDatabaseVersionService();
		try{
			databaseVersionService.checkDatabaseVersion();
		} catch (ACException e1){
			ErrorEntry error = new ServerErrorContainerWrapper(e1).getErrors().get(0);
			String msg = Translation.get(error.getId(), error.getErrors());
			JOptionPane.showMessageDialog(view, msg, "", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder){
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordEncoder getPasswordEncoder(){
		return passwordEncoder;
	}

	public void reportError(ErrorEntry value){
		List<ErrorEntry> list = new ArrayList<ErrorEntry>();
		list.add(value);
		view.renderErrors(list);
	}
}
