/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.ObjectDAO;
import com.ni3.ag.navigator.server.dao.ObjectDefinitionDAO;
import com.ni3.ag.navigator.server.datasource.AttributeDataSource;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.shared.constants.RequestParam;

public class SrcIdToIdConvertionServlet extends Ni3Servlet{
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(SrcIdToIdConvertionServlet.class);

	public void doInternalPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	        IOException{
		if (request.getRequestURI().contains("srcidtoid")){
			processSrcIdToId(request, response);
		}
		if (request.getRequestURI().contains("idtosrcid")){
			processIdToSrcId(request, response);
		}
	}

	private void processSrcIdToId(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();

		try{
			String srcId = getParameter(request, RequestParam.SRCID);
			if (srcId == null){
				out.println("-1");
				return;
			}
			int schemaId = getIntParam(request, RequestParam.SCHID);
			SchemaLoaderService schemaLoaderService = NSpringFactory.getInstance().getSchemaLoaderService();
			Schema schema = schemaLoaderService.getSchema(schemaId);
			for(ObjectDefinition entity : schema.getDefinitions()){
				for(Attribute attribute : entity.getAttributes()){
					if("srcid".equalsIgnoreCase(attribute.getName())){
						AttributeDataSource attributeDataSource = (AttributeDataSource) NSpringFactory.getInstance().getBean(attribute.getDataSource());
						Collection<Integer> ids = attributeDataSource.search(attribute, new AdvancedCriteria.Section.Condition(attribute.getId(), "=", srcId, false));
						if(!ids.isEmpty()){
							out.println(ids.iterator().next());
							return;
						}
					}
				}
			}
			out.println("-1");
		} catch (NumberFormatException e){
			log.error("Invalid source id");
			out.println("-2");
		} finally{
			log.debug("closing output");
			out.flush();
			out.close();
		}
	}

	private void processIdToSrcId(HttpServletRequest request, HttpServletResponse response) throws IOException{
		PrintWriter out = response.getWriter();

		try{
			String id = getParameter(request, RequestParam.SRCID);
			if (id == null){
				out.println("-1");
				return;
			}

			ObjectDefinitionDAO odDAO = NSpringFactory.getInstance().getObjectDefinitionDAO();
			List<ObjectDefinition> ods = odDAO.getObjectDefinitions();
			final ObjectDAO objectDAO = NSpringFactory.getInstance().getObjectDAO();
			for (ObjectDefinition od : ods){
				String result = objectDAO.getSrcIdById(Integer.parseInt(id), od);
				if (!"-1".equals(result)){
					out.println(result);
					return;
				}
			}
			out.println("-1");
		} catch (NumberFormatException e){
			log.error("Invalid source id");
			out.println("-2");
		} catch (SQLException e){
			log.error(e);
		} finally{
			log.debug("closing output");
			out.flush();
			out.close();
		}
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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException{
		if (req.getRequestURI().contains("srcidtoid")){
			processSrcIdToId(req, response);
		}
		if (req.getRequestURI().contains("idtosrcid")){
			processIdToSrcId(req, response);
		}
	}

}
