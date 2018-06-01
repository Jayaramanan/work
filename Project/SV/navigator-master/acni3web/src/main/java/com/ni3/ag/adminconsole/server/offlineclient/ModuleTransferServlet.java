/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.offlineclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ni3.ag.adminconsole.remoting.ThreadLocalStorage;
import com.ni3.ag.adminconsole.server.TransferUtils;
import com.ni3.ag.adminconsole.server.TransferUtilsImpl;
import com.ni3.ag.adminconsole.server.datasource.ACRoutingDataSource;
import com.ni3.ag.adminconsole.server.datasource.InstanceDescriptor;
import com.ni3.ag.adminconsole.validation.ACException;

public class ModuleTransferServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ModuleTransferServlet.class);
	private static final String ACTION_GET_MODULE = "DownloadModule";
	private static final String ACTION_PING = "Ping";
	private static final String ACTION_CHECK_FILE_EXISTANCE = "CheckFileExistance";
	private ACRoutingDataSource dataSource;

	private TransferUtils transferUtils = new TransferUtilsImpl();

	public void init(){
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		dataSource = (ACRoutingDataSource) context.getBean("routingDatasource");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		setDBID(request.getHeader("DBID"));
		String module = request.getHeader("module");
		InputStream is = request.getInputStream();
		try{
			processUploadModule(is, module);
		} catch (ACException e){
			is.close();
			throw new ServletException(e);
		}
		is.close();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		setDBID(request.getParameter("DBID"));
		String modulePath = getModulePathForInstance();
		log.info("Module path for instance: " + modulePath);
		if (modulePath == null){
			log.warn("Offline module path is invalid: " + modulePath);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		log.debug("request received");
		String action = request.getParameter("action");
		String module = request.getParameter("module");

		if (!ACTION_GET_MODULE.equals(action) && !ACTION_PING.equals(action) && !ACTION_CHECK_FILE_EXISTANCE.equals(action)){
			log.warn("invalid action for GET method. only get module or ping allowed");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		if (ACTION_PING.equals(action)){
			if (!transferUtils.ping(modulePath)){
				log.warn("modules path is not reachable");
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			return;
		}

		if (module == null){
			log.warn("parameter `module` is missing or has invalid value");
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		if (ACTION_CHECK_FILE_EXISTANCE.equals(action)){
			boolean exists = transferUtils.fileExists(modulePath, module);
			log.debug("File `" + module + "` exists: " + exists);
			response.getOutputStream().println(exists);
			response.getOutputStream().flush();
			return;
		}
		action = URLDecoder.decode(action, "UTF-8");
		module = URLDecoder.decode(module, "UTF-8");
		log.debug("action=" + action);
		log.debug("module=" + module);

		try{
			processGetModule(response, module);
		} catch (ACException e){
			throw new ServletException(e);
		}
	}

	private String getModulePathForInstance(){
		InstanceDescriptor desc = dataSource.getCurrentInstanceDescriptor();
		return desc.getModulePath();
	}

	private void processUploadModule(InputStream is, String name) throws ACException{
		log.debug("processUploadModule");
		String modulePath = getModulePathForInstance();
		transferUtils.uploadFile(modulePath, is, name);
	}

	private void processGetModule(HttpServletResponse response, String module) throws IOException, ACException{
		log.debug("processGetModule");
		String modulePath = getModulePathForInstance();
		Long length = transferUtils.getDownloadableFileLength(modulePath, module);
		response.setContentLength(length.intValue());
		transferUtils.downloadFile(modulePath, response.getOutputStream(), module);
	}

	private void setDBID(String dbid){
		if (dbid != null && !dbid.isEmpty()){
			ThreadLocalStorage.getInstance().setCurrentDatabaseInstanceId(dbid);
		}
	}
}
