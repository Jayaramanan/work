package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.*;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.DataFilter;
import com.ni3.ag.navigator.client.domain.query.Condition;
import com.ni3.ag.navigator.client.domain.query.Order;
import com.ni3.ag.navigator.client.domain.query.Query;
import com.ni3.ag.navigator.client.domain.query.Section;
import com.ni3.ag.navigator.client.gateway.SearchGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.domain.DBObject;
import com.ni3.ag.navigator.shared.domain.MetaphorIcon;
import com.ni3.ag.navigator.shared.domain.NodeMetaphor;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpSearchGatewayImpl extends FilterApplicableCallGateway implements SearchGateway{

	@Override
	public List<DBObject> simpleSearch(final int schemaId, final String searchForString, DataFilter preFilter){
		NRequest.Search.Builder request = NRequest.Search.newBuilder();
		request.setAction(NRequest.Search.Action.PERFORM_SIMPLE_SEARCH);
		request.setTerm(searchForString);
		request.setPreFilter(makeFilter(preFilter));
		request.setSchemaId(schemaId);
		return responseExtractResult(request);
	}

	@Override
	public List<DBObject> advancedSearch(final int schemaId, final Query query, final DataFilter preFilter){
		NRequest.Search.Builder request = NRequest.Search.newBuilder();
		request.setAction(NRequest.Search.Action.PERFORM_ADVANCED_SEARCH);
		if (query.getGeoSearchCondition() != null)
			request.setGeoSearchCriteria(query.getGeoSearchCondition());
		request.setSchemaId(schemaId);
		request.setQueryType(query.getType().getValue());
		request.setLimit(query.getMaxResults());
		addSectionsToRequest(request, query.getSections());
		request.setPreFilter(makeFilter(preFilter));
		return responseExtractResult(request);
	}

	@Override
	public List<DBObject> getList(final int schemaId, final Map<Integer, Collection<Integer>> missing){
		NRequest.Search.Builder request = NRequest.Search.newBuilder();
		request.setAction(NRequest.Search.Action.PERFORM_GET_LIST);
		request.setSchemaId(schemaId);
		for (Integer eid : missing.keySet()){
			Collection<Integer> missingForEntity = missing.get(eid);
			NRequest.Missing.Builder missingBuilder = NRequest.Missing.newBuilder();
			missingBuilder.setEntityId(eid);
			missingBuilder.addAllId(missingForEntity);
			request.addMissing(missingBuilder);
		}
		return responseExtractResult(request);
	}

	@Override
	public List<DBObject> getListContext(final int schemaId, final int entityId, final List<Integer> ids,
			final int contextId, final String contextKey){
		NRequest.Search.Builder request = NRequest.Search.newBuilder();
		request.setAction(NRequest.Search.Action.PERFORM_GET_LIST_CONTEXT);
		request.setSchemaId(schemaId);
		request.setContextId(contextId);
		request.setContextKey(contextKey);
		request.addMissing(NRequest.Missing.newBuilder().setEntityId(entityId).addAllId(ids));
		return responseExtractResult(request);
	}

	@Override
	public List<DBObject> searchUnknown(final int schemaId, final List<Integer> ids, final boolean withDeleted){
		NRequest.Search.Builder request = NRequest.Search.newBuilder();
		request.setAction(NRequest.Search.Action.PERFORM_GET_LIST_UNKNOWN);
		request.setSchemaId(schemaId);
		request.addMissing(NRequest.Missing.newBuilder().setEntityId(0).addAllId(ids));
		request.setIncludeDeleted(withDeleted);
		return responseExtractResult(request);
	}

	private List<DBObject> responseExtractResult(NRequest.Search.Builder request){
		try{
			ByteString resultBytes = sendRequest(ServletName.SearchServlet, request.build());
			NResponse.SimpleSearch result = NResponse.SimpleSearch.parseFrom(resultBytes);
			List<NResponse.DBObject> protoObjects = result.getObjectList();
			List<DBObject> objects = new ArrayList<DBObject>();
			for (NResponse.DBObject protoObject : protoObjects){
				DBObject dbo = new DBObject(protoObject.getId(), protoObject.getEntityId());
				List<NResponse.DataPair> protoDataList = protoObject.getDataPairList();
				dbo.setData(new HashMap<Integer, String>());
				for (NResponse.DataPair protoData : protoDataList){
					dbo.getData().put(protoData.getAttributeId(), protoData.getValue());
				}
				if (protoObject.hasMetaphor()){ // node
					NodeMetaphor nm = createMetaphorFromProto(protoObject);
					dbo.setMetaphor(nm);
				}
				objects.add(dbo);
			}
			return objects;
		} catch (IOException ex){
			showErrorAndThrow("Error delete thematic map", ex);
			return null;
		}
	}

	private NodeMetaphor createMetaphorFromProto(NResponse.DBObject protoObject){
		NodeMetaphor nm = new NodeMetaphor();
		NResponse.NodeMetaphor protoMetaphor = protoObject.getMetaphor();
		if (protoMetaphor != null){
			if (protoMetaphor.hasAssignedIcon()){
				NResponse.MetaphorIcon icon = protoMetaphor.getAssignedIcon();
				nm.setAssignedMetaphor(new MetaphorIcon(icon.getIconName(), icon.getPriority()));
			}
			final HashMap<String, MetaphorIcon> map = new HashMap<String, MetaphorIcon>();
			final List<String> metaphorSets = protoMetaphor.getMetaphorSetsList();
			final List<NResponse.MetaphorIcon> icons = protoMetaphor.getMetaphorsList();
			for (int i = 0; i < metaphorSets.size(); i++){
				NResponse.MetaphorIcon icon = icons.get(i);
				map.put(metaphorSets.get(i), new MetaphorIcon(icon.getIconName(), icon.getPriority()));
			}
			nm.setMetaphors(map);
		}
		return nm;
	}

	private void addSectionsToRequest(NRequest.Search.Builder request, List<Section> sections){
		for (Section s : sections){
			NRequest.SearchSection.Builder section = NRequest.SearchSection.newBuilder();
			section.setEntity(s.getEnt().ID);
			for (Condition c : s.getConditions()){
				NRequest.SearchCondition.Builder searchCondition = NRequest.SearchCondition.newBuilder();
				searchCondition.setAttributeId(c.getAttributeId()).setOperation(c.getOperation()).setTerm(c.getTerm());
				section.addCondition(searchCondition);
			}
			for (Order o : s.getOrder()){
				NRequest.SearchOrder.Builder searchOrder = NRequest.SearchOrder.newBuilder();
				searchOrder.setAttributeId(o.getAttributeId());
				searchOrder.setAsc(o.getAsc());
				section.addOrder(searchOrder);
			}
			request.addSection(section);
		}
	}
}
