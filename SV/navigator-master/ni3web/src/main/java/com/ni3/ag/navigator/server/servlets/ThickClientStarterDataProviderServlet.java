/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.Module;
import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserActivityDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.services.ThickClientModuleService;
import com.ni3.ag.navigator.server.servlets.util.SimpleTLVEncoder;
import com.ni3.ag.navigator.shared.domain.User;

@SuppressWarnings("serial")
public class ThickClientStarterDataProviderServlet extends Ni3Servlet{
	private static final Logger log = Logger.getLogger(ThickClientStarterDataProviderServlet.class);
	private static final String ACTION_GET_CURRENT_VERSIONS = "GetVersions";
	private static final String ACTION_GET_MODULE = "GetModule";
	private static final String ACTION_COMMIT = "Commit";
	private static final String ACTION_GET_SID_BY_SSO = "GetSIDBySSO";
	private static final String ACTION_GET_LOGIN_PASSWORD = "GetLoginPassword";
	private static final String MODULES_PATH_PROPERTY = "com.ni3.ag.navigator.offfline.modules.path";

	private static String modulesPath;
	private HttpServletRequest request;

	static{
		loadOfflineProperties();
	}

	@Override
	public void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		this.request = request;
		// TODO rewrite comminucation using protobuff
		// TODO extract all interaction with DB to DAO layer
		log.debug("request received");
		final String sid = request.getParameter("SID");
		final String sso = request.getParameter("SSO");

		final String userName = request.getParameter("user");
		final String password = request.getParameter("password");
		final String action = request.getParameter("action");
		final String module = request.getParameter("module");
		final String version = request.getParameter("version");

		if (log.isDebugEnabled()){
			log.debug("user=" + userName);
			log.debug("password=" + password);
			log.debug("action=" + action);
			log.debug("module=" + module);
			log.debug("version=" + version);
			log.debug("SID=" + sid);
			log.debug("SSO=" + sso);
		}
		if (((userName == null && password == null) && sid == null && sso == null) || action == null){
			log.warn("parameter missing or invalud value");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		boolean useSid = sid != null;
		boolean useSSO = sso != null;

		if (ACTION_GET_MODULE.equals(action) && module == null){
			log.warn("parameter `module` is missing or has invalid value");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if (ACTION_COMMIT.equals(action) && (module == null || version == null)){
			log.warn("parameter `module` or `version` is missing or has invalid value");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		log.debug("Resolving user");
		final User user = getUser(userName, password, sid, useSid, useSSO, sso);
		if (log.isDebugEnabled()){
			log.debug("Resolved user: " + (user == null ? null : user.getId()));
		}
		if (user == null){
			log.warn("No user found for such user/password pair");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		ThickClientModuleService service = NSpringFactory.getInstance().getThickClientModuleService();

		if (ACTION_GET_CURRENT_VERSIONS.equals(action)){
			service.processGetCurrentVersions(response, user);
		} else if (ACTION_GET_MODULE.equals(action)){
			Module result = processGetModule(user, module, response);
			if (result != null)
				logGetModuleActivity(request, user, result);
		} else if (ACTION_COMMIT.equals(action)){
			service.processCommitModule(user, module, version, response);
		} else if (ACTION_GET_SID_BY_SSO.equals(action)){
			processGetSIDBySSO(user, response);
		} else if (ACTION_GET_LOGIN_PASSWORD.equals(action)){
			processGetLoginPasswordBySID(user, response);
		}
	}

	private void processGetLoginPasswordBySID(User user, HttpServletResponse response) throws IOException{
		try{
			final OutputStream out = response.getOutputStream();
			final SimpleTLVEncoder encoder = new SimpleTLVEncoder(out);
			encoder.writeString(user.getUserName());
			encoder.writeString(user.getPassword());
		} catch (IOException e){
			log.error("Error send SID to client", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void processGetSIDBySSO(User user, HttpServletResponse response) throws IOException{
		try{
			final OutputStream out = response.getOutputStream();
			final SimpleTLVEncoder encoder = new SimpleTLVEncoder(out);
			encoder.writeString(user.getSID());
		} catch (IOException e){
			log.error("Error send SID to client", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("unchecked")
	private void logGetModuleActivity(HttpServletRequest request, User user, Module module)
			throws UnsupportedEncodingException{
		final UserActivityDAO dao = NSpringFactory.getInstance().getUserActivityDao();

		final Enumeration<String> headerNames = request.getHeaderNames();
		String temp = "";
		while (headerNames.hasMoreElements()){
			final String headerName = headerNames.nextElement();
			temp += headerName + "=" + request.getHeader(headerName) + ";";
		}

		String message = "action=" + ACTION_GET_MODULE + "&module=" + URLEncoder.encode(module.getName(), "UTF-8")
				+ "&version=" + URLEncoder.encode(module.getVersion(), "UTF-8");
		dao.save(user.getId(), UserActivityType.OfflineGetModule.getValueText(), message, request.getRemoteAddr(), temp);
	}

	private static void loadOfflineProperties(){
		Properties prop = new Properties();
		try{
			prop.load(ThickClientStarterDataProviderServlet.class.getResourceAsStream("/offline.properties"));
			modulesPath = prop.getProperty(MODULES_PATH_PROPERTY);
			if (modulesPath == null){
				modulesPath = new File(".").getCanonicalPath();
			}
			if (!modulesPath.endsWith(File.separator)){
				modulesPath += File.separator;
			}
		} catch (IOException e){
			log.error("cant load offline.properties", e);
		}
	}

	private Module processGetModule(User user, String module, HttpServletResponse response) throws IOException{
		try{
			log.debug("processGetModule");
			ThickClientModuleService service = NSpringFactory.getInstance().getThickClientModuleService();
			Module m = service.getModule(module, user, modulesPath);
			if (m == null){
				log.error("failed to get current module for user");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}

			Properties pp = new Properties();
			pp.load(new StringReader(m.getParams()));
			String locked = pp.getProperty(Module.DB_DUMP_PARAM_LOCKED);
			if ("1".equals(locked)){
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}

			if (m.getHash() == null || "".equals(m.getHash().trim())){
				m.setHash(makeHash(m.getPath()));
			}
			if (log.isDebugEnabled()){
				log.debug("Hash=" + m.getHash());
			}

			OutputStream out = response.getOutputStream();
			SimpleTLVEncoder encoder = new SimpleTLVEncoder(out);
			log.debug("writing name");
			encoder.writeString(m.getName());
			log.debug("writing vers");
			encoder.writeString(m.getVersion());
			log.debug("writing hash");
			encoder.writeString(m.getHash());
			log.debug("writing arc pass");
			encoder.writeString(m.getArchivePassword());
			log.debug("writing file data");
			encoder.writeFile(m.getPath());
			log.debug("writing done");
			return m;
		} catch (FileNotFoundException e){
			log.error("error open module file", e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (IOException e){
			log.error("error open module file", e);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (NoSuchAlgorithmException e){
			log.error("error making hash", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

	private String makeHash(String name) throws NoSuchAlgorithmException, IOException{
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(name);
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			byte[] buf = new byte[1024 * 20];
			while (fis.available() > 0){
				int count = fis.read(buf);
				digest.update(buf, 0, count);
			}
			byte[] hash = digest.digest();
			StringBuilder hexString = new StringBuilder();
			for (byte element : hash){
				String s = Integer.toHexString(0xFF & element);
				if (s.length() == 1){
					s = "0" + s;
				}
				hexString.append(s);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e){
			fis.close();
			throw e;
		}
	}

	private User getUser(final String user, final String password, String sid, boolean useSid, boolean useSSO, String sso){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		String userName = null;
		if (useSSO){
			userName = NSpringFactory.getInstance().getSsoCache().getSSOUsername(sso);
			if (userName == null || userName.startsWith("*"))
				return null;
		}
		if (useSSO)
			return userDAO.getByUsername(userName.toLowerCase());
		else if (useSid)
			return userDAO.getBySID(sid);
		else
			return userDAO.getByUsernamePassword(user.toLowerCase(), password);
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}
}
