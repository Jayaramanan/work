/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.search.AdvancedCriteria;
import com.ni3.ag.navigator.server.search.ListIdCriteria;
import com.ni3.ag.navigator.server.search.SimpleCriteria;
import com.ni3.ag.navigator.server.services.MetaphorService;
import com.ni3.ag.navigator.server.services.SearchService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;
import com.ni3.ag.navigator.shared.domain.NodeMetaphor;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

@SuppressWarnings("serial")
public class SearchServlet extends Ni3Servlet{
	private static final Logger log = Logger.getLogger(SearchServlet.class);

	private static final int SIMPLE_SEARCH_DEFAULT_LIMIT = 5000;
	private NRequest.Search request;

	@Override
	public void doInternalPost(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse)
			throws ServletException, IOException{

		request = NRequest.Search.parseFrom(getInputStream(httpRequest));
		NResponse.Envelope.Builder response = NResponse.Envelope.newBuilder();
		boolean result = false;
		switch (request.getAction()){
			case PERFORM_SIMPLE_SEARCH:
				result = handleSimpleSearch(request, response);
				break;
			case PERFORM_ADVANCED_SEARCH:
				result = handleAdvancedSearch(request, response);
				break;
			case PERFORM_GET_LIST:
				result = handleGetList(request, response);
				break;
			case PERFORM_GET_LIST_CONTEXT:
				result = handleGetListContext(request, response);
				break;
			case PERFORM_GET_LIST_UNKNOWN:
				result = handleGetListUnknown(request, response);
				break;
		}
		response.setStatus(result ? NResponse.Envelope.Status.SUCCESS : NResponse.Envelope.Status.FAILED);
		sendResponse(httpRequest, httpResponse, response);
	}

	private boolean handleGetListUnknown(NRequest.Search request, NResponse.Envelope.Builder response){
		SearchService searchService = NSpringFactory.getInstance().getSearchService();
		ListIdCriteria criteria = new ListIdCriteria();
		List<NRequest.Missing> protoMissing = request.getMissingList();
		if (protoMissing.isEmpty())
			return false;
		criteria.setSchema(request.getSchemaId());
		criteria.add(0, protoMissing.get(0).getIdList());
		criteria.setWithDeleted(request.getIncludeDeleted());
		Collection<DBObject> result = searchService.performGetListUnknown(criteria);
		if (result == null)
			return false;

		fillMetaphors(result, request.getSchemaId());
		packResults(response, result);
		return true;
	}

	private boolean handleGetListContext(NRequest.Search request, NResponse.Envelope.Builder response){
		SearchService searchService = NSpringFactory.getInstance().getSearchService();

		ListIdCriteria criteria = new ListIdCriteria();
		List<NRequest.Missing> requestedList = request.getMissingList();
		if (requestedList.size() != 1){
			log.error("Error - requested id list size invalid expected: 1 actual " + requestedList.size());
			return false;
		}
		List<Integer> ids = requestedList.get(0).getIdList();
		criteria.add(requestedList.get(0).getEntityId(), ids);
		criteria.setSchema(request.getSchemaId());
		criteria.setContextData(new ListIdCriteria.ContextData(request.getContextId(), request.getContextKey()));
		Collection<DBObject> result = searchService.performGetList(criteria);
		if (result == null)
			return false;

		fillMetaphors(result, request.getSchemaId());
		packResults(response, result);
		return true;
	}

	private boolean handleGetList(NRequest.Search request, NResponse.Envelope.Builder response){
		SearchService searchService = NSpringFactory.getInstance().getSearchService();
		ListIdCriteria criteria = new ListIdCriteria();
		criteria.setSchema(request.getSchemaId());
		for (NRequest.Missing protoMissing : request.getMissingList()){
			List<Integer> ids = new ArrayList<Integer>();
			for (Integer id : protoMissing.getIdList())
				ids.add(id);
			criteria.add(protoMissing.getEntityId(), ids);
		}
		if (criteria.isEmpty())
			return true;
		Collection<DBObject> result = searchService.performGetList(criteria);
		if (result == null)
			return false;

		fillMetaphors(result, request.getSchemaId());
		packResults(response, result);
		return true;
	}

	private boolean handleAdvancedSearch(NRequest.Search request, NResponse.Envelope.Builder response){
		log.debug("handleAdvancedSearch: " + request);
		SearchService searchService = NSpringFactory.getInstance().getSearchService();
		AdvancedCriteria advancedCriteria = getAdvancedSearchCriteria(request);
		Collection<DBObject> result = searchService.performAdvancedSearch(advancedCriteria);
		if (result == null)
			return false;

		fillMetaphors(result, request.getSchemaId());
		packResults(response, result);
		return true;
	}

	private boolean handleSimpleSearch(NRequest.Search request, NResponse.Envelope.Builder response){
		log.debug("handleSimpleSearch: " + request);
		SimpleCriteria simpleCriteria = getSimpleSearchCriteria(request);
		log.debug("Search criteria " + simpleCriteria);
		SearchService searchService = NSpringFactory.getInstance().getSearchService();
		Collection<DBObject> result = searchService.performSimpleSearch(simpleCriteria);
		if (result == null)
			return false;

		fillMetaphors(result, request.getSchemaId());
		packResults(response, result);
		return true;
	}

	private void packResults(NResponse.Envelope.Builder response, Collection<DBObject> result){
		NResponse.SimpleSearch.Builder builder = NResponse.SimpleSearch.newBuilder();
		for (DBObject obj : result){
			NResponse.DBObject.Builder protoObject = NResponse.DBObject.newBuilder();
			protoObject.setId(obj.getId());
			protoObject.setEntityId(obj.getEntityId());
			if (obj.getMetaphor() != null){
				protoObject.setMetaphor(createProtoMetaphor(obj.getMetaphor()));
			}

			Map<Integer, String> data = obj.getData();
			for (int attrId : data.keySet())
				protoObject.addDataPair(NResponse.DataPair.newBuilder().setAttributeId(attrId).setValue(data.get(attrId)));
			builder.addObject(protoObject);
		}
		response.setPayload(builder.build().toByteString());
	}

	private NResponse.NodeMetaphor createProtoMetaphor(NodeMetaphor metaphor){
		NResponse.NodeMetaphor.Builder protoNm = NResponse.NodeMetaphor.newBuilder();
		if (metaphor.getAssignedMetaphor() != null){
			final MetaphorIcon icon = metaphor.getAssignedMetaphor();
			NResponse.MetaphorIcon protoIcon = createProtoIcon(icon);
			protoNm.setAssignedIcon(protoIcon);
		}
		final Map<String, MetaphorIcon> metaphors = metaphor.getMetaphors();
		for (String mSet : metaphors.keySet()){
			final MetaphorIcon icon = metaphors.get(mSet);
			NResponse.MetaphorIcon protoIcon = createProtoIcon(icon);
			protoNm.addMetaphorSets(mSet);
			protoNm.addMetaphors(protoIcon);
		}
		return protoNm.build();
	}

	private NResponse.MetaphorIcon createProtoIcon(final MetaphorIcon icon){
		NResponse.MetaphorIcon.Builder protoIcon = NResponse.MetaphorIcon.newBuilder();
		protoIcon.setIconName(icon.getIconName());
		protoIcon.setPriority(icon.getPriority());
		return protoIcon.build();
	}

	private void fillMetaphors(Collection<DBObject> objects, int schemaId){
		MetaphorService metaphorService = NSpringFactory.getInstance().getMetaphorService();
		metaphorService.fillMetaphors(objects, schemaId);
	}

	private AdvancedCriteria getAdvancedSearchCriteria(NRequest.Search request){
		ThreadLocalStorage threadLocalStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = threadLocalStorage.getCurrentUser();

		AdvancedCriteria criteria = new AdvancedCriteria();
		criteria.setSchema(request.getSchemaId());
		criteria.setFilteredValues(request.getPreFilter().getValueIdList());
		criteria.setLimit(request.getLimit());
		criteria.setUser(currentUser);

		criteria.setGeoCondition(request.getGeoSearchCriteria());
		criteria.setQueryType(request.getQueryType());

		criteria.setSections(new ArrayList<AdvancedCriteria.Section>());
		for (NRequest.SearchSection protoSS : request.getSectionList()){
			AdvancedCriteria.Section acs = new AdvancedCriteria.Section();
			acs.setEntity(protoSS.getEntity());
			for (NRequest.SearchCondition protoSC : protoSS.getConditionList()){
				acs.addCondition(new AdvancedCriteria.Section.Condition(protoSC.getAttributeId(), protoSC.getOperation(),
						protoSC.getTerm(), false));
			}
			for (NRequest.SearchOrder protoSO : protoSS.getOrderList()){
				acs.addOrder(new AdvancedCriteria.Section.Order(protoSO.getAttributeId(), protoSO.getAsc()));
			}
			criteria.getSections().add(acs);
		}
		return criteria;
	}

	private SimpleCriteria getSimpleSearchCriteria(NRequest.Search request){
		ThreadLocalStorage threadLocalStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = threadLocalStorage.getCurrentUser();
		SimpleCriteria simpleCriteria = new SimpleCriteria();
		simpleCriteria.setUser(currentUser);
		simpleCriteria.setSchema(request.getSchemaId());
		simpleCriteria.setTerm(request.getTerm());
		simpleCriteria.setFilteredValues(request.getPreFilter().getValueIdList());
		simpleCriteria.setLimit(SIMPLE_SEARCH_DEFAULT_LIMIT);
		return simpleCriteria;
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		switch (request.getAction()){
			case PERFORM_SIMPLE_SEARCH:
				activity = UserActivityType.SimpleSearch;
				break;
			case PERFORM_ADVANCED_SEARCH:
				final String geo = request.getGeoSearchCriteria();
				if (geo != null && !geo.isEmpty()){
					activity = UserActivityType.GeoSearch;
				} else{
					activity = UserActivityType.AdvancedSearch;
				}
				break;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}

}
