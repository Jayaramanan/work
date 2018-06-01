/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.GraphCache;
import com.ni3.ag.navigator.server.cache.GraphNi3Engine;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.session.UserSessionStore;
import com.ni3.ag.navigator.shared.constants.RequestParam;

public class CacheInvalidationServlet extends Ni3Servlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CacheInvalidationServlet.class);

	public CacheInvalidationServlet(){
		super();
		log.info("Ni3 Application server - Cache invalidation servlet");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		doPost(request, response);
	}

	protected void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException{
		final PrintWriter out = response.getWriter();

		if (getParameter(request, RequestParam.ReloadSchema) != null){
			Integer schemaID;

			if (request.getParameter("Schema") != null){
				try{
					schemaID = Integer.decode(request.getParameter("Schema"));
				} catch (final NumberFormatException e){
					log.info("Invalid SchemaID in call of ReloadSchema. Action triggered by user "
							+ request.getParameter("User"));
					return;
				}

				NSpringFactory.getInstance().getSchemaLoaderService().invalidate(schemaID);
			} else{
				NSpringFactory.getInstance().getSchemaLoaderService().invalidateAll();
			}

			UserSessionStore.getInstance().invalidateAllUsers();
			NSpringFactory.getInstance().getUserGroupCache().reload();

			log.info("Invalidated application cache. Action triggered by user " + getParameter(request, RequestParam.User));
			out.println("Invalidated application cache. Action triggered by user "
					+ getParameter(request, RequestParam.User));
		}

		if (getParameter(request, RequestParam.ReloadGraph) != null){
			Integer schemaId = null;
			if (request.getParameter("Schema") != null)
				schemaId = Integer.decode(request.getParameter("Schema"));
			reloadGraph(schemaId);
		}
	}

	private void reloadGraph(Integer schemaId){
		// we need to clear graph object before creating new one
		// to allow GC to collect old data
		// because graph object uses a lots of memory
		log.info("Reloading graph");
		GraphCache.getInstance().clear(schemaId != null ? schemaId : -1);
		List<Schema> schemas = NSpringFactory.getInstance().getSchemaLoaderService().getAllSchemas();
		for (Schema sch : schemas){
			if (schemaId != null && sch.getId() != schemaId)
				continue;
			log.debug("reloading graph for schema " + sch.getId());
			GraphNi3Engine graph = NSpringFactory.getInstance().getGraphEngineFactory().newGraph(sch.getId());
			GraphCache.getInstance().setGraph(graph);
		}
		log.info("Graph reloaded");
	}

	@Override
	protected UserActivityType getActivityType(){
		// not used
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		// not used
		return null;
	}
}
