/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.servlets.json;

import java.io.IOException;
import java.io.PrintWriter;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.dao.SchemaDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.Group;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.servlets.LoginServlet;
import com.ni3.ag.navigator.server.servlets.Ni3Servlet;
import com.ni3.ag.navigator.server.session.UserSessionStore;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.domain.User;

public class LoginServletJson extends Ni3Servlet{

	private enum Status{
		SUCCESS(1), INVALID_CREDENTIALS(2);
		int val;

		Status(int val){
			this.val = val;
		}

		int getValue(){
			return val;
		}
	}

	private static final Logger log = Logger.getLogger(LoginServlet.class);

	private static NSpringFactory daoFactory = NSpringFactory.getInstance();

	private static final long serialVersionUID = 1L;

	private JsonObject loginRequest;

	@Override
	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		JsonParser parser = new JsonParser();
		loginRequest = (JsonObject) parser.parse(request.getReader());
		String action = loginRequest.get("action").getAsString();

		JsonObject loginResponse = new JsonObject();
		if ("LOGIN_BY_PASSWORD".equals(action)){
			final String sessionId = processLoginWithUsernamePassword(request, loginResponse, loginRequest.get("userName")
					.getAsString(), loginRequest.get("password").getAsString());
			if (sessionId != null){
				loginResponse.addProperty("sessionId", sessionId);
				loginResponse.addProperty("status", Status.SUCCESS.getValue());
			} else{
				loginResponse.addProperty("status", Status.INVALID_CREDENTIALS.getValue());
			}
		} else if ("LOGOUT".equals(action)){
			processLogout(request);
		}

		String responseStr = loginResponse.toString();
		log.debug(responseStr);
		final PrintWriter writer = response.getWriter();
		writer.write(responseStr);
		writer.flush();
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

	void fillUserInResponse(final JsonObject loginResponse, final User user, final Group group){
		JsonObject jsonUser = new JsonObject();
		jsonUser.addProperty("id", user.getId());
		jsonUser.addProperty("userName", user.getUserName());
		jsonUser.addProperty("firstName", user.getFirstName());
		jsonUser.addProperty("lastName", user.getLastName());
		loginResponse.add("user", jsonUser);
		loginResponse.addProperty("groupId", group.getId());
	}

	String processLoginWithUsernamePassword(HttpServletRequest request, JsonObject loginResponse, String username,
			String password){
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
		String action = loginRequest.get("action").getAsString();
		if ("LOGIN_BY_PASSWORD".equals(action)){
			activity = UserActivityType.PasswordLogin;
		} else if ("LOGOUT".equals(action)){
			activity = UserActivityType.Logout;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		String action = loginRequest.get("action").getAsString();
		if ("LOGIN_BY_PASSWORD".equals(action)){
			params.add(new LogParam("Username", loginRequest.get("userName").getAsString()));
		}
		return params;

	}

	@Override
	protected void doAfterPost(HttpServletRequest request){
		String action = loginRequest.get("action").getAsString();
		if ("LOGOUT".equals(action)){
			final UserSessionStore uss = UserSessionStore.getInstance();
			final Integer userId = getUserId(request);
			request.getSession().invalidate();
			uss.remove(userId);

		}
		loginRequest = null;
	}
}
