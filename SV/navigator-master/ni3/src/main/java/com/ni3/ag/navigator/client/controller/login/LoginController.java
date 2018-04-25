/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.client.controller.login;

import java.awt.*;
import javax.swing.*;

import com.ni3.ag.navigator.client.controller.LicenseValidator;
import com.ni3.ag.navigator.client.domain.UserSettings;
import com.ni3.ag.navigator.client.gateway.LoginGatewayEx;
import com.ni3.ag.navigator.client.gateway.impl.HttpLoginGatewayImpl;
import com.ni3.ag.navigator.client.gui.DlgLogin;
import com.ni3.ag.navigator.client.gui.DlgPasswordChange;
import com.ni3.ag.navigator.client.gui.Ni3;
import com.ni3.ag.navigator.client.gui.common.Ni3OptionPane;
import com.ni3.ag.navigator.client.model.SystemGlobals;
import com.ni3.ag.navigator.client.util.Str;
import com.ni3.ag.navigator.client.util.passwordencoder.PasswordUtil;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.login.LoginResult;
import com.ni3.ag.navigator.shared.login.LoginStatus;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordEncoder;
import org.apache.log4j.Logger;

public class LoginController{
	private static final Logger log = Logger.getLogger(LoginController.class);

	public boolean login(final String username, final String password, final String SID, final String SSO){
		return login(username, password, SID, SSO, false);
	}

	public boolean login(final String username, final String password, final String SID, String SSO, final boolean relogin){
		boolean success = false;
		if (!relogin && (SSO != null && !"null".equals(SSO))){
			success = loginWithSSO(SSO);
		}

		if (!success && !relogin && (SID != null && !"null".equals(SID))){
			success = loginWithSID(SID);
		}

		if (!success){
			success = loginWithUsernamePassword(username, password, relogin);
		}

		if (!success){
			if (!Ni3.AppletMode){
				Ni3.callOnClosing();
				System.exit(0);
			}
		}
		return success;
	}

	private boolean loginWithUsernamePassword(final String username, final String password, final boolean relogin){
		boolean success = false;
		final LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
		final DlgLogin dlg = new DlgLogin(Ni3.mainF instanceof Frame ? (Frame)Ni3.mainF : null, true);

		dlg.setUsernamePassword(username, password, relogin);
		dlg.setLocation(new Point(20, 20));

		LoginResult result = null;
		do{
			if (!relogin && password != null && username != null && result == null){
				dlg.setOkPressed(true); // login without showing dialog
			} else{
				dlg.setVisible(true);
			}

			if (!dlg.isOkPressed()){
				log.debug("Login cancelled");
				success = false;
				break;
			}

			if (loginGw.hasConnectionToServer()){
				final PasswordEncoder encoder = PasswordUtil.getInstance().getPasswordEncoder();

				final String user = dlg.getUsername();
				final String pass = encoder.encode(dlg.getUsername(), dlg.getPassword());

				log.debug("Login with username = " + user);
				result = loginGw.loginWithUserNamePassword(user, pass);
				if (!result.isOk()){
					showErrorMessage(result.getLoginStatus());
				}
			} else{
				showErrorMessage(LoginStatus.NoConnectionToServer);
			}
			success = (result != null && result.isOk() && isCorrectLicense(true));
		} while (!success);

		dlg.dispose();

		if (success){
			fillUserData(result);
		}
		return success;
	}

	private boolean loginWithSID(final String SID){
		boolean success = false;
		final LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
		if (loginGw.hasConnectionToServer()){
			log.debug("Login with SID");
			final LoginResult result = loginGw.loginWithSID(SID);
			if (result != null && result.isOk()){
				fillUserData(result);
				if (isCorrectLicense(false)){
					success = true;
				}
			}
		}
		return success;
	}

	private boolean loginWithSSO(String SSO){
		boolean success = false;
		final LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
		if (loginGw.hasConnectionToServer()){
			SSO = Str.unescape(SSO);
			log.debug("Login with SSO");
			final LoginResult result = loginGw.loginWithSSO(SSO);
			if (result != null && result.isOk()){
				fillUserData(result);
				if (isCorrectLicense(true)){
					success = true;
				}
			} else{
				showErrorMessage(LoginStatus.CannotLoginWithSSO);
			}
		}
		return success;
	}

	private void fillUserData(final LoginResult result){
		final User user = result.getUser();
		SystemGlobals.setUser(user);
		SystemGlobals.GroupID = result.getGroupId();
		if (result.getInstance() != null){
			SystemGlobals.Instance = result.getInstance();
		}
	}

	public void showErrorMessage(final LoginStatus loginStatus){
		switch (loginStatus){
			case InvalidLogin:
				log.warn("Invalid username or password");
				showErrorMessage("Invalid username or password");
				break;
			case NoConnectionToServer:
				log.error("No connection to server: " + SystemGlobals.ServerURL);
				showErrorMessage("Cannot connect to the server");
				break;
			case NoConnectionToDB:
				log.error("Cannot connect to the database from server");
				showErrorMessage("Cannot connect to the database");
				break;
			case WrongCurrentPassword:
				log.error("Cannot connect to the database from server");
				showErrorMessage("Cannot reset password");
				break;
			case CannotLoginWithSSO:
				log.error("Cannot login with SSO");
				showErrorMessage("Cannot login with SSO");
				break;
			default:
				break;
		}
	}

	private void showErrorMessage(final String message){
		Ni3OptionPane.showMessageDialog(Ni3.mainF, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private boolean isCorrectLicense(final boolean showError){
		final LicenseValidator validator = LicenseValidator.getInstance();
		validator.reloadLicense();
		String result = validator.isCorrectLicense();
		if (result != null || !validator.isApplicationEnabled()
				|| (SystemGlobals.isThickClient && !validator.isOfflineClientEnabled())){
			result = (result != null ? result : "Application is not accessible due to license restrictions");
			log.warn("Error = " + result);
			log.warn("Application accessible(base module) = " + validator.isApplicationEnabled());
			log.warn("Offline client = " + SystemGlobals.isThickClient);
			log.warn("Offline client module accessible = " + validator.isOfflineClientEnabled());

			if (showError){
				showErrorMessage(result);
			}

			SystemGlobals.setUser(null);
			return false;
		}
		log.debug("License is correct");
		return true;
	}

	public void changePassword(){
		DlgPasswordChange dlg = new DlgPasswordChange();
		dlg.setLocation(new Point(20, 20));
		dlg.cancel = true;
		dlg.setVisible(true);

		if (!dlg.cancel){
			final PasswordEncoder encoder = PasswordUtil.getInstance().getPasswordEncoder();
			final String encodedCurrentPass = encoder.encode(SystemGlobals.getUser().getUserName(), dlg.password);
			final String encodedNewPass = encoder.generate(dlg.passwordnew);

			final LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
			if (!loginGw.changePassword(encodedCurrentPass, encodedNewPass)){
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgWrongCurrentPassword"), UserSettings
						.getWord("IncorectPasswordTitle"), JOptionPane.WARNING_MESSAGE);
				return;
			} else{
				Ni3OptionPane.showMessageDialog(Ni3.mainF, UserSettings.getWord("MsgPasswordChangedSuccessfully"), "",
						JOptionPane.INFORMATION_MESSAGE);
			}

		}
		dlg.dispose();
	}

	public boolean resetPassword(String email){
		LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
		return loginGw.resetPassword(email);
	}

	public void logout(){
		final LoginGatewayEx loginGw = new HttpLoginGatewayImpl();
		loginGw.logout();
	}
}
