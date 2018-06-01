/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.StaleStateException;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;

import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;

public class ACHttpInvokerServiceExporter extends HttpInvokerServiceExporter{

	/** available even for unauthorized users */
	private final static String[] FREE_METHOD_NAMES = new String[] { "getDeploymentVersion", "getExpectedVersion",
	        "getActualVersion", "getDatabaseInstanceNames", "getLanguages", "getProperties", "addDataSource",
	        "deleteDataSource", "getLicenseAccesses", "getInvalidationRequiredGroups", "getCommonProperties",
	        "isAnyInvalidationRequired", "databaseInstanceExists", "getSaltForUser" };

	private final static String LOGIN_METHOD_NAME = "login";

	private Logger log = Logger.getLogger(ACHttpInvokerServiceExporter.class);

	private Integer userId;
	private String sessionId;
	private String dbId;

	private HttpServletRequest currentRequest;

	@Override
	protected InputStream decorateInputStream(HttpServletRequest request, InputStream inputStream) throws IOException{
		return new GZIPInputStream(inputStream);
	}

	@Override
	protected OutputStream decorateOutputStream(HttpServletRequest request, HttpServletResponse response,
	        OutputStream outputStream) throws IOException{
		return new GZIPOutputStream(outputStream);
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		currentRequest = request;
		super.handleRequest(request, response);

	}

	private void updateSession(){
		SessionIdHolder.getInstance().setSessionId(sessionId);
	}

	private boolean isUserAuthorized(){
		UserSessionStore usm = UserSessionStore.getInstance();
		// search for this user in userSessionMapper
		String storedSession = usm.getSessionId(userId, dbId);
		return sessionId.equals(storedSession);
	}

	private boolean isNewConnection(){
		return sessionId == null && userId == null;
	}

	private void createNewSession(){
		HttpSession ses = currentRequest.getSession(true);
		SessionIdHolder.getInstance().setSessionId(ses.getId());
	}

	private boolean isMethodFree(String methodName){
		if ("databaseInstanceExists".equals(methodName))
			return true;
		for (String s : FREE_METHOD_NAMES){
			if (s.equals(methodName))
				return true;
		}
		return false;
	}

	@Override
	protected Object invoke(RemoteInvocation remoteInvokation, Object arg1) throws NoSuchMethodException,
	        IllegalAccessException, InvocationTargetException{
		Object ret = null;

		dbId = (String) remoteInvokation.getAttribute(TransferConstants.DB_INSTANCE_ID);
		sessionId = (String) remoteInvokation.getAttribute(TransferConstants.SESSION_ID);
		userId = (Integer) remoteInvokation.getAttribute(TransferConstants.USER_ID);
		log.debug("Got attributes from the client:");
		log.debug(TransferConstants.DB_INSTANCE_ID + " = " + "" + dbId);
		log.debug(TransferConstants.SESSION_ID + " = " + "" + sessionId);
		log.debug(TransferConstants.USER_ID + " = " + "" + userId);
		log.debug(" calling " + "" + remoteInvokation.getMethodName());
		ThreadLocalStorage threadLocalStorage = ThreadLocalStorage.getInstance();
		if (dbId != null)
			threadLocalStorage.setCurrentDatabaseInstanceId(dbId);

		String methodName = remoteInvokation.getMethodName();
		// if new connection and login method - create new session
		if (LOGIN_METHOD_NAME.equals(methodName)){
			if (isNewConnection())
				createNewSession();
			else
				updateSession();
		} else if (!isMethodFree(methodName) && !isUserAuthorized()){
			return new ErrorEntry(TextID.MsgPleaseRelogin);
		}

		try{
			ret = super.invoke(remoteInvokation, arg1);
		} catch (InvocationTargetException e){
			Throwable cause = e.getCause();
			Throwable causeCause = cause.getCause();
			if (cause instanceof ObjectNotFoundException
			        || cause instanceof StaleStateException
			        || (causeCause != null && (causeCause instanceof StaleStateException || causeCause instanceof ObjectNotFoundException))){
				ret = new ErrorEntry(TextID.MsgPleaseRefresh);
			} else{
				throw e;
			}

		}
		return ret;
	}
}
