/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.google.protobuf.ByteString;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.SSOCache;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.dao.SchemaDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.passadmin.PasswordReseter;
import com.ni3.ag.navigator.server.passadmin.impl.PasswordReseterImpl;
import com.ni3.ag.navigator.server.session.UserSessionStore;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.Login;
import com.ni3.ag.navigator.shared.proto.NRequest.Login.Action;
import com.ni3.ag.navigator.shared.proto.NResponse.Login.Builder;
import com.ni3.ag.navigator.shared.proto.NResponse.Login.Status;
import com.ni3.ag.navigator.shared.util.passwordencoder.PasswordSaltGetter;

public class LoginServlet extends Ni3Servlet{

	private static final Logger log = Logger.getLogger(LoginServlet.class);

	private static NSpringFactory daoFactory = NSpringFactory.getInstance();

	private static final long serialVersionUID = 1L;

	private Login loginRequest = null;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		final Builder loginResponse = NResponse.Login.newBuilder();
		loginRequest = NRequest.Login.parseFrom(getInputStream(request));

		final Action action = loginRequest.getAction();
		switch (action){
			case LOGIN_BY_PASSWORD: {
				final String sessionId = processLoginWithUsernamePassword(request, loginResponse,
						loginRequest.getUserName(), loginRequest.getPassword());
				if (sessionId != null){
					loginResponse.setSessionId(sessionId);
					loginResponse.setStatus(Status.SUCCESS);
				} else{
					loginResponse.setStatus(Status.INVALID_CREDENTIALS);
				}
				loginResponse.setInstance(getInstanceId());
				break;
			}
			case LOGIN_BY_SID: {
				final String sessionId = processLoginWithSID(request, loginResponse, loginRequest.getSid());
				if (sessionId != null){
					loginResponse.setSessionId(sessionId);
					loginResponse.setStatus(Status.SUCCESS);
				} else{
					loginResponse.setStatus(Status.INVALID_CREDENTIALS);
				}
				loginResponse.setInstance(getInstanceId());
				break;
			}
			case LOGIN_BY_SSO:
				final String sessionId = processLoginWithSSO(request, loginResponse, loginRequest.getSso());
				if (sessionId != null){
					loginResponse.setSessionId(sessionId);
					loginResponse.setStatus(Status.SUCCESS);
				} else{
					loginResponse.setStatus(Status.INVALID_CREDENTIALS);
				}
				loginResponse.setInstance(getInstanceId());
				break;
			case LOGOUT:
				processLogout(request);
				loginResponse.setStatus(Status.SUCCESS);
				break;
			case CHANGE_PASSWORD:
				if (processChangePassword(loginRequest.getUserId(), loginRequest.getPassword(), loginRequest
						.getNewPassword())){
					loginResponse.setStatus(Status.SUCCESS);
				} else{
					loginResponse.setStatus(Status.INVALID_CREDENTIALS);
				}
				break;
			case RESET_PASSWORD:
				if (processResetPassword(loginRequest.getEmail())){
					loginResponse.setStatus(Status.SUCCESS);
				} else{
					loginResponse.setStatus(Status.INVALID_CREDENTIALS);
				}
				break;
			case GET_SALT_FOR_USER: {
				PasswordSaltGetter passwordSaltGetter = NSpringFactory.getInstance().getPasswordSaltGetter();
				String userSalt = passwordSaltGetter.getSalt(loginRequest.getUserName());
				if (userSalt == null)
					loginResponse.setStatus(Status.ERROR_GET_SALT);
				else{
					loginResponse.setSalt(userSalt);
					loginResponse.setStatus(Status.SUCCESS);
				}
			}
				break;
			default:
				throw new UnsupportedOperationException("Invalid Action");
		}

		final ByteString payload = loginResponse.build().toByteString();
		final NResponse.Envelope.Builder envelope = NResponse.Envelope.newBuilder();
		envelope.setStatus(NResponse.Envelope.Status.SUCCESS);
		envelope.setPayload(payload);
		sendResponse(request, response, envelope);
	}

	private String getInstanceId(){
		String instance = null;
		final SchemaDAO schemaDAO = daoFactory.getSchemaDAO();
		final List<Schema> schemas = schemaDAO.getSchemas();
		if (!schemas.isEmpty()){
			final Schema schema = schemas.get(0);

			try{
				final MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
				final String string = schema.getName() + ':' + String.valueOf(schema.getCreation().getTime());
				final byte[] digest = messageDigest.digest(string.getBytes());

				final Formatter formatter = new Formatter();
				for (final byte b : digest){
					formatter.format("%02x", b);
				}
				instance = formatter.toString();
			} catch (NoSuchAlgorithmException e){
				log.error("Can'c generate instance id", e);
			}
		}
		return instance;
	}

	void fillUserInResponse(final Builder loginResponse, final User user, final Group group){
		final NResponse.User.Builder builder = NResponse.User.newBuilder();
		builder.setUserId(user.getId());
		builder.setUserName(user.getUserName());
		builder.setFirstName(user.getFirstName());
		builder.setLastName(user.getLastName());
		builder.setPassword(user.getPassword());
		builder.setSid(user.getSID());
		loginResponse.setUser(builder.build());
		loginResponse.setGroupId(group.getId());
	}

	boolean processChangePassword(final int userId, final String oldPassword, final String newPassword){
		final UserDAO userDao = daoFactory.getUserDao();

		boolean passwordChanged = false;

		final User user = userDao.get(userId);
		if (user != null && user.getPassword().equals(oldPassword)){
			user.setPassword(newPassword);
			userDao.update(user);
			passwordChanged = true;
		}

		return passwordChanged;
	}

	String processLoginWithSID(final HttpServletRequest request, final Builder loginResponse, final String sid){
		User user;
		String sessionId = null;

		if (log.isDebugEnabled()){
			log.debug("Trying to login with SID = " + sid);
		}

		final UserDAO userDao = daoFactory.getUserDao();
		final GroupDAO groupDAO = daoFactory.getGroupDao();
		user = userDao.getBySID(sid);
		if (user != null && Boolean.TRUE.equals(user.isActive())){
			Group g = groupDAO.getByUser(user.getId());
			sessionId = registerUserSession(request, true, user);
			fillUserInResponse(loginResponse, user, g);
		} else{
			log.warn("Incorrect SID or inactive user");
		}
		return sessionId;
	}

	String processLoginWithSSO(final HttpServletRequest request, final Builder loginResponse, final String SSO){
		User user;
		String sessionId = null;

		if (log.isDebugEnabled()){
			log.debug("Trying to login with SSO = " + SSO);
		}

		final UserDAO userDao = daoFactory.getUserDao();
		final GroupDAO groupDAO = daoFactory.getGroupDao();
		SSOCache ssoCache = NSpringFactory.getInstance().getSsoCache();
		final String username = ssoCache.getSSOUsername(SSO);
		user = userDao.getByUsername(username);

		if (user != null && Boolean.TRUE.equals(user.isActive())){
			Group g = groupDAO.getByUser(user.getId());
			sessionId = registerUserSession(request, true, user);
			fillUserInResponse(loginResponse, user, g);
		} else{
			log.warn("Incorrect SSO token or inactive user");
		}
		return sessionId;
	}

	String processLoginWithUsernamePassword(final HttpServletRequest request, final Builder loginResponse,
			final String username, final String password){
		String sessionId = null;
		if (log.isDebugEnabled()){
			log.debug("Trying to login with username = " + username);
		}

		final UserDAO userDao = daoFactory.getUserDao();
		final GroupDAO groupDAO = daoFactory.getGroupDao();
		final User user = userDao.getByUsernamePassword(username, password);
		if (user != null && Boolean.TRUE.equals(user.isActive())){
			Group g = groupDAO.getByUser(user.getId());
			sessionId = registerUserSession(request, false, user);
			fillUserInResponse(loginResponse, user, g);
		} else{
			log.warn("Incorrect username or password");
		}
		return sessionId;
	}

	void processLogout(final HttpServletRequest request){
		Integer userId = getUserId(request);
		log.debug("Got logout request with UserID=" + userId);
	}

	boolean processResetPassword(final String email){
		boolean passwordReset = false;
		PasswordReseter pr;
		try{
			pr = new PasswordReseterImpl();
			passwordReset = pr.resetPassword(email);
		} catch (final Throwable th){
			log.error("Cannot reset password", th);
		}
		return passwordReset;
	}

	String registerUserSession(final HttpServletRequest request, final boolean isSID, final User user){
		final HttpSession session = request.getSession(true);
		session.setAttribute(RequestParam.UserID.name(), user.getId());
		final String sessionId = session.getId();
		if (log.isDebugEnabled()){
			log.debug("Created new session: " + sessionId);
		}

		if (log.isDebugEnabled()){
			log.debug("Stored session, userid = " + user.getId() + ",sessionId = " + sessionId);
		}
		UserSessionStore.getInstance().put(user.getId(), sessionId);
		return sessionId;
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		final Action action = loginRequest != null ? loginRequest.getAction() : null;
		if (action == null)
			return activity;
		switch (action){
			case LOGIN_BY_PASSWORD:
				if (loginRequest.getSync()){
					activity = UserActivityType.Synchronization;
				} else{
					activity = UserActivityType.PasswordLogin;
				}
				break;
			case LOGIN_BY_SID:
				activity = UserActivityType.SIDLogin;
				break;
			case LOGIN_BY_SSO:
				activity = UserActivityType.SSOLogin;
				break;
			case LOGOUT:
				activity = UserActivityType.Logout;
				break;
			case CHANGE_PASSWORD:
				activity = UserActivityType.ChangePassword;
				break;
			case RESET_PASSWORD:
				activity = UserActivityType.ResetPassword;
				break;
			default:
				break;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		final Action action = loginRequest != null ? loginRequest.getAction() : null;
		if (action != null){
			switch (action){
				case LOGIN_BY_PASSWORD:
					params.add(new LogParam("Username", loginRequest.getUserName()));
					break;
				case LOGIN_BY_SID:
				case LOGIN_BY_SSO:
				case LOGOUT:
				case CHANGE_PASSWORD:
					break;
				case RESET_PASSWORD:
					params.add(new LogParam("Email", loginRequest.getEmail()));
					break;
				default:
					break;
			}
		}
		return params;
	}

	@Override
	protected void doAfterPost(HttpServletRequest request){
		final Action action = loginRequest != null ? loginRequest.getAction() : null;
		if (Action.LOGOUT.equals(action)){
			final UserSessionStore uss = UserSessionStore.getInstance();
			final Integer userId = getUserId(request);
			request.getSession().invalidate();
			uss.remove(userId);
		}
		loginRequest = null;
	}

	@Override
	protected Integer getUserId(HttpServletRequest request){
		final Action action = loginRequest != null ? loginRequest.getAction() : null;
		if (Action.RESET_PASSWORD.equals(action)){
			final UserDAO userDao = daoFactory.getUserDao();
			final List<User> users = userDao.findByEmail(loginRequest.getEmail());
			if (users != null && users.size() == 1){
				return users.get(0).getId();
			}
		}
		return super.getUserId(request);
	}
}
