/**
 * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved.
 */
package com.ni3.ag.navigator.server.filters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.server.session.UserSessionStore;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.domain.User;
import org.apache.log4j.Logger;

public abstract class AbstractSessionFilter implements Filter{

	/**
	 * This class is explicitly excluded from logging on DEBUG level. Please adjust log4j.properties in case you want to
	 * see DEBUG messages
	 */
	private final static Logger log = Logger.getLogger(AbstractSessionFilter.class);

	protected abstract void sendReloginResponse(ServletResponse response) throws IOException;

	protected abstract void sendInvalidateSchemaResponse(ServletResponse response) throws IOException;

	protected abstract boolean isFreeMethod(ServletRequest request);

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException{
		request.setCharacterEncoding("UTF-8");
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		final HttpSession session = httpRequest.getSession(false);
		dumpSession(session);
		String sessionId = null;
		Integer userId = null;
		if (session != null){
			sessionId = session.getId();
			userId = (Integer) session.getAttribute(RequestParam.UserID.name());
		}
		log.debug("Request userId: " + userId);
		if (userId == null){
			userId = -1;
		}
		if (log.isDebugEnabled()){
			log.debug("sessionId=" + sessionId);
			log.debug("userId=" + userId);
			// log.debug("Remote host=" + rHost);
		}

		if (isUserAuthorized(userId, sessionId) || isFreeMethod(request)){
			if (needsInvalidation(userId)){
				sendInvalidateSchemaResponse(response);
				resetInvalidation(userId);
			} else{
				UserDAO userDao = NSpringFactory.getInstance().getUserDao();
				User user = null;
				if (userId != -1)
					user = userDao.get(userId);
				ThreadLocalStorage threadLocal = NSpringFactory.getInstance().getThreadLocalStorage();
				threadLocal.setCurrentUser(user);

				chain.doFilter(request, response);

				threadLocal.removeCurrentUser();
			}
		} else{
			log.warn("Request without session or not authorized user " + httpRequest.getRequestURL());
			sendReloginResponse(response);
		}
	}

	private void dumpSession(HttpSession session){
		log.debug("----------------SESSION DATA------------------");
		if (session == null)
			log.debug("SESSION: is null");
		else{
			log.debug("\tSessionId: " + session.getId());
			log.debug("\tAttributes{");
			Enumeration<String> names = session.getAttributeNames();
			while (names.hasMoreElements()){
				String name = names.nextElement();
				log.debug("\t\t" + name + "=" + session.getAttribute(name));
			}
			log.debug("\t}");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
			log.debug("\tIsNew: " + session.isNew());
			log.debug("\tCreated: " + sdf.format(new Date(session.getCreationTime())));
			log.debug("\tLastAccessed: " + sdf.format(new Date(session.getLastAccessedTime())));
			log.debug("\tContext: " + session.getServletContext());
			log.debug("\tNowInterval: " + (System.currentTimeMillis() - session.getLastAccessedTime()));
			log.debug("\tMaxInactiveInterval: " + session.getMaxInactiveInterval());
			log.debug("\tSessionContext: " + session.getSessionContext());
		}
		log.debug("==============================================");
	}

	private void resetInvalidation(Integer userId){
		UserSessionStore.getInstance().resetInvalidationNeeded(userId);
	}

	private boolean needsInvalidation(Integer userId){
		return UserSessionStore.getInstance().needsInvalidation(userId);
	}

	private boolean isUserAuthorized(final Integer userId, final String sessionId){
		if (userId == null || sessionId == null){
			log.warn("Received a request with userID=" + userId + " sessionId=" + sessionId);
			return false;
		}

		final UserSessionStore usm = UserSessionStore.getInstance();
		final String storedSession = usm.getSessionId(userId);
		return sessionId.equals(storedSession);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException{
	}

	@Override
	public void destroy(){
	}
}
