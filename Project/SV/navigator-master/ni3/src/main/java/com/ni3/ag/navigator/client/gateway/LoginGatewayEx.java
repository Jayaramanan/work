package com.ni3.ag.navigator.client.gateway;

import com.ni3.ag.navigator.shared.gateway.LoginGateway;
import com.ni3.ag.navigator.shared.login.LoginResult;

public interface LoginGatewayEx extends LoginGateway{
	/**
	 * @param currentPassword
	 * @param newPassword
	 * @return
	 */
	boolean changePassword(String currentPassword, String newPassword);

	/**
	 * @param eMail
	 * @return
	 */
	boolean resetPassword(String eMail);

	/**
	 * @param sid
	 * @param SSO
	 * @return
	 */
	LoginResult loginWithSID(String sid);

	/**
	 * @param SSO
	 * @return
	 */
	LoginResult loginWithSSO(String SSO);

	/**
	 * @return true if connection to server was successful
	 */
	boolean hasConnectionToServer();

	/**
	 * @param userName
	 * @param password
	 * @return
	 */
	LoginResult loginWithUserNamePassword(String userName, String password);

	/**
	 * @return
	 */
	boolean logout();

	String getSaltForUser(String login);
}
