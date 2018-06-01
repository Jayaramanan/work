/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.adminconsole.util.Base64;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.DeltaHeaderDAO;
import com.ni3.ag.navigator.server.dao.UserActivityDAO;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.shared.constants.RequestParam;
import com.ni3.ag.navigator.shared.proto.NResponse;

@SuppressWarnings("serial")
public abstract class Ni3Servlet extends HttpServlet{
	protected static final String SKIP_ACTIVITY = "SKIP_ACTIVITY";
	protected static final String ID_LOG_PARAM = "Id";
	protected static final String ICONNAME_LOG_PARAM = "Iconname";
	protected static final String FROMID_LOG_PARAM = "FromId";
	protected static final String TOID_LOG_PARAM = "ToId";

	private static final Logger log = Logger.getLogger(Ni3Servlet.class);

	protected boolean getBooleanParam(final HttpServletRequest request, final RequestParam param){
		final String p = request.getParameter(param.name());

		if (p == null || "null".equals(p)){
			return false;
		}

		try{
			return Boolean.parseBoolean(p);
		} catch (final Exception e){
			return false;
		}
	}

	protected int getIntParam(final HttpServletRequest request, final RequestParam param){
		return getIntParam(request, param.name());
	}

	protected int getIntParam(final HttpServletRequest request, final RequestParam param, int defaultValue){
		return getIntParam(request, param.name(), defaultValue);
	}

	protected int getIntParam(final HttpServletRequest request, final String paramName){
		return getIntParam(request, paramName, 0);
	}

	protected int getIntParam(final HttpServletRequest request, final String paramName, final int defaultValue){
		final String p = request.getParameter(paramName);

		if (p == null || "null".equals(p)){
			return defaultValue;
		}

		try{
			return Integer.parseInt(p);
		} catch (final Exception e){
			return defaultValue;
		}
	}

	public static String listParam(final HttpServletRequest request, final RequestParam param){
		final String p = request.getParameter(param.name());
		return p == null ? null : p.replaceAll("[^0-9,-]", "");
	}

	// TODO create javadoc for what this method is for
	protected abstract void doInternalPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

	// TODO create javadoc for what this method is for
	protected abstract UserActivityType getActivityType();

	// TODO create javadoc for what this method is for
	protected abstract List<LogParam> getActivityParams();

	/**
	 * This method should be implemented by all servlets that handle actions that need to be synchronized to offline
	 * clients
	 * <p/>
	 * This method is made not abstract to avoid extra code in servlets that are not related to offline client
	 * synchronization
	 * 
	 * @return Object representing the change produced by the performed action
	 */
	protected DeltaHeader getTransactionDeltaForRequest(){
		// default implementation
		return DeltaHeader.DO_NOTHING;
	}

	void createUserActivityLog(final HttpServletRequest request){
		final UserActivityType activityType = getActivityType();
		if (activityType != null){
			final String message = getFullMessage(request);
			final Integer userId = getUserId(request);
			final String remoteAddr = request.getRemoteAddr();
			final UserActivityDAO uaDao = NSpringFactory.getInstance().getUserActivityDao();
			final String header = getHeader(request);

			uaDao.save(userId, activityType.getValueText(), message, remoteAddr, header);
		}
	}

	private String getFullMessage(HttpServletRequest request){
		final String sessionId = getSessionId(request);
		String message = RequestParam.SessionId.name() + "=" + sessionId + ";";
		List<LogParam> params = getActivityParams();
		if (params != null){
			for (LogParam param : params){
				message += param.name + "=" + param.value;
				message += ";";
			}
		}
		return message;
	}

	private String getSessionId(HttpServletRequest request){
		final HttpSession session = request.getSession(false);
		return session != null ? session.getId() : null;
	}

	protected Integer getUserId(HttpServletRequest request){
		final HttpSession session = request.getSession(false);
		Integer userId = null;
		if (session != null)
			userId = (Integer) session.getAttribute(RequestParam.UserID.name());
		if (userId == null){
			userId = -1;
		}
		return userId;
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException{
		try{
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			String origin = request != null ? request.getHeader("origin") : null;
			if (origin != null && !origin.isEmpty()){
				response.setHeader("Access-Control-Allow-Origin", origin); // hack, http server should be defined here
				response.setHeader("Access-Control-Allow-Credentials", "true");
			}
		} catch (final Exception e){
			log.error(e.getMessage(), e);
		}
		doInternalPost(request, response);

		createUserActivityLog(request);

		createTransactionLog();

		doAfterPost(request);
	}

	private void createTransactionLog(){
		DeltaHeader delta = getTransactionDeltaForRequest();
		DeltaHeaderDAO deltaHeaderDAO = NSpringFactory.getInstance().getDeltaHeaderDAO();
		deltaHeaderDAO.save(delta);
	}

	protected void doAfterPost(HttpServletRequest request){
	}

	String getParameter(final HttpServletRequest request, RequestParam param){
		return request.getParameter(param.name());
	}

	@SuppressWarnings("unchecked")
	private String getHeader(HttpServletRequest request){
		final Enumeration<String> headerNames = request.getHeaderNames();
		String header = "";
		while (headerNames.hasMoreElements()){
			final String headerName = headerNames.nextElement();
			header += headerName + "=" + request.getHeader(headerName) + ";";
		}
		return header;
	}

	protected InputStream getInputStream(HttpServletRequest request) throws IOException{
		InputStream result;
		String customIeHeader = request.getHeader("Custom-IE");
		if (customIeHeader == null){
			result = request.getInputStream();
		}else{
			String byteStr = request.getParameter("byteStr");
			byte [] bytes = Base64.decode(byteStr);
			result = new ByteArrayInputStream(bytes);
		}
		return result;
	}

	protected void sendResponse(HttpServletRequest request, HttpServletResponse response, NResponse.Envelope.Builder envelope)
			throws IOException{
		String customIeHeader = request.getHeader("Custom-IE");
		if (customIeHeader == null){
			envelope.build().writeTo(response.getOutputStream());
		} else{
			final byte[] byteArray = envelope.build().toByteArray();
			String b64 = Base64.encodeBytes(byteArray);
			response.getWriter().write(b64);
		}
	}

	protected class LogParam{
		private String name;
		private Object value;

		public LogParam(String name, Object value){
			super();
			this.name = name;
			this.value = value;
		}

	}
}
