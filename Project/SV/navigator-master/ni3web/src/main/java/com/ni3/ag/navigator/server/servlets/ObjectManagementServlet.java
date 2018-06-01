package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.calc.FormulaExecutor;
import com.ni3.ag.navigator.server.dao.NodeDAO;
import com.ni3.ag.navigator.server.dao.UserDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.DeltaHeader;
import com.ni3.ag.navigator.server.domain.DeltaParam;
import com.ni3.ag.navigator.server.domain.DeltaParamIdentifier;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.services.ObjectManagementService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.server.util.Utility;
import com.ni3.ag.navigator.shared.domain.DeltaType;
import com.ni3.ag.navigator.shared.domain.User;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NRequest.ObjectManagement;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class ObjectManagementServlet extends Ni3Servlet{
	private static final long serialVersionUID = -5651809165921659029L;
	private static final Logger log = Logger.getLogger(ObjectManagementServlet.class);
	private final ObjectManagementService objService = NSpringFactory.getInstance().getObjectManagementService();
	private NRequest.ObjectManagement request;

	private Map<Attribute, String> attributeToValueMap;
	private int newId;

	@Override
	protected void doInternalPost(HttpServletRequest httpRequest, HttpServletResponse response) throws ServletException,
			IOException{
		InputStream io = getInputStream(httpRequest);
		request = NRequest.ObjectManagement.parseFrom(io);
		boolean result = false;
		NResponse.Envelope.Builder builder = NResponse.Envelope.newBuilder();
		switch (request.getAction()){
			case UPDATE_NODE_METAPHOR:
				result = updateNodeMetaphor();
				break;
			case UPDATE_NODE_GEO_COORDS:
				result = updateNodeGeoCoords();
				break;
			case DELETE:
				result = delete(request);
				break;
			case INSERT_NODE:
				result = insertNode(request, builder);
				break;
			case INSERT_EDGE:
				result = insertEdge(request, builder);
				break;
			case UPDATE_NODE:
				result = updateNode(request, builder);
				break;
			case UPDATE_EDGE:
				result = updateEdge(request, builder);
				break;
			case MERGE_NODE:
				result = mergeNode(request);
				break;
			case CLONE_CONTEXT:
				result = cloneContext(request);
				break;
			case CLEAR_CONTEXT:
				result = clearContext(request);
				break;
			case SET_CONTEXT:
				result = setContext(request);
				break;
			case CHECK_CAN_DELETE_NODE:
				result = canDeleteObject(request, builder);
				break;
		}
		NResponse.Envelope.Builder envelope = builder.setStatus(
				result ? NResponse.Envelope.Status.SUCCESS : NResponse.Envelope.Status.FAILED);
		sendResponse(httpRequest, response, envelope);
	}

	private boolean mergeNode(ObjectManagement request){
		final int entityId = request.getEntityId();
		final int fromId = request.getFromId();
		final int toId = request.getToId();
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<Integer> edgeIds = request.getEdgeIdsList();

		log.debug("merge node from " + fromId + " to " + toId);
		objService.merge(fromId, toId, entityId, attributeIds, edgeIds);
		return true;
	}

	private boolean updateEdge(ObjectManagement request, Builder builder){
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<String> values = request.getValuesList();
		final int entityId = request.getEntityId();
		final int favoriteId = request.getFavoriteId();
		final int objectId = request.getObjectId();

		log.debug("update edge " + objectId);
		attributeToValueMap = objService.getAttributeIdToValueMap(entityId, attributeIds, values, false);
		newId = objService.updateEdge(objectId, entityId, attributeToValueMap, favoriteId);
		NResponse.ObjectManagement.Builder protoResult = NResponse.ObjectManagement.newBuilder();
		protoResult.setId(newId);
		builder.setPayload(protoResult.build().toByteString());
		return true;
	}

	private boolean updateNode(ObjectManagement request, Builder builder){
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<String> values = request.getValuesList();
		final int entityId = request.getEntityId();
		final int objectId = request.getObjectId();

		log.debug("update node " + objectId);
		attributeToValueMap = objService.getAttributeIdToValueMap(entityId, attributeIds, values, false);
		int nodeId = objService.updateNode(objectId, entityId, attributeToValueMap);
		NResponse.ObjectManagement.Builder protoResult = NResponse.ObjectManagement.newBuilder();
		protoResult.setId(nodeId);
		builder.setPayload(protoResult.build().toByteString());
		return true;
	}

	private boolean insertEdge(ObjectManagement request, Builder builder){
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<String> values = request.getValuesList();
		final int entityId = request.getEntityId();
		final int favoriteId = request.getFavoriteId();
		final int fromId = request.getFromId();
		final int toId = request.getToId();

		log.debug("insert new edge from node " + fromId + " to node " + toId);
		attributeToValueMap = objService.getAttributeIdToValueMap(entityId, attributeIds, values, true);
		newId = objService.insertEdge(entityId, attributeToValueMap, favoriteId, fromId, toId);
		NResponse.ObjectManagement.Builder protoResult = NResponse.ObjectManagement.newBuilder();
		protoResult.setId(newId);
		builder.setPayload(protoResult.build().toByteString());
		return true;
	}

	private boolean insertNode(ObjectManagement request, Builder builder){
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<String> values = request.getValuesList();
		final int entityId = request.getEntityId();

		log.debug("insert new node");
		attributeToValueMap = objService.getAttributeIdToValueMap(entityId, attributeIds, values, true);
		newId = objService.insertNode(entityId, attributeToValueMap);
		NResponse.ObjectManagement.Builder protoResult = NResponse.ObjectManagement.newBuilder();
		protoResult.setId(newId);
		builder.setPayload(protoResult.build().toByteString());
		return true;
	}

	private boolean canDeleteObject(ObjectManagement request, Builder builder){
		int objectId = request.getObjectId();
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();

		boolean result = objService.canDeleteObject(currentUser, objectId);
		NResponse.ObjectDeleteAccessResult.Builder protoResult = NResponse.ObjectDeleteAccessResult.newBuilder();
		protoResult.setResult(result);
		builder.setPayload(protoResult.build().toByteString());
		return true;
	}

	private boolean clearContext(ObjectManagement request){
		int favoriteId = request.getFavoriteId();
		int schemaId = request.getSchemaId();
		objService.clearContext(favoriteId, schemaId);
		return true;
	}

	private boolean cloneContext(ObjectManagement request){
		int contextId = request.getContextId();
		int fromFavoriteId = request.getOldFavoriteId();
		int favoriteId = request.getFavoriteId();
		int schemaId = request.getSchemaId();

		log.debug("clone context from favorite " + fromFavoriteId + " to " + favoriteId);
		objService.cloneContext(schemaId, contextId, fromFavoriteId, favoriteId);
		return true;
	}

	private boolean setContext(ObjectManagement request){
		final List<Integer> attributeIds = request.getAttributeIdsList();
		final List<String> values = request.getValuesList();
		final int entityId = request.getEntityId();
		final int objectId = request.getObjectId();
		final int favoriteId = request.getFavoriteId();
		final int contextId = request.getContextId();

		log.debug("set context for the object " + objectId + ", favorite " + favoriteId);
		attributeToValueMap = objService.getAttributeIdToValueMap(entityId, attributeIds, values, false);
		objService.setContext(objectId, entityId, attributeToValueMap, contextId, favoriteId);
		return true;
	}

	private boolean delete(ObjectManagement request){
		int objectId = request.getObjectId();
		int entityId = request.getEntityId();

		log.debug("delete object " + objectId);
		objService.delete(entityId, objectId);
		return true;
	}

	private boolean updateNodeGeoCoords(){
		return objService.updateNodeGeoCoords(request.getNodeId(), request.getGeoCoords().getLon(), request.getGeoCoords()
				.getLat());
	}

	private boolean updateNodeMetaphor(){
		NodeDAO nodeDAO = NSpringFactory.getInstance().getNodeDAO();
		final String iconName;
		if (request.hasIconName()){
			iconName = request.getIconName();
		} else{
			iconName = null;
		}
		return nodeDAO.updateNodeMetaphor(request.getNodeId(), iconName);
	}

	protected DeltaHeader getTransactionDeltaForRequest(){
		DeltaHeader result = DeltaHeader.DO_NOTHING;
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		ThreadLocalStorage localStorage = NSpringFactory.getInstance().getThreadLocalStorage();
		User currentUser = localStorage.getCurrentUser();
		switch (request.getAction()){
			case UPDATE_NODE_METAPHOR:
				result = getTransactionDeltaForMetaphorUpdate();
				break;
			case UPDATE_NODE_GEO_COORDS:
				result = getTransactionDeltaForRequestForUpdateNodeCoords();
				break;
			case DELETE:
				params.put(DeltaParamIdentifier.DeleteObjectObjectId, new DeltaParam(
						DeltaParamIdentifier.DeleteObjectObjectId, "" + request.getObjectId()));
				params.put(DeltaParamIdentifier.DeleteObjectSchemaId, new DeltaParam(
						DeltaParamIdentifier.DeleteObjectSchemaId, "" + request.getSchemaId()));
				params.put(DeltaParamIdentifier.DeleteObjectObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.DeleteObjectObjectDefinitionId, "" + request.getEntityId()));
				result = new DeltaHeader(DeltaType.OBJECT_DELETE, currentUser, params);
				break;
			case INSERT_NODE: {
				params.putAll(getAttributeToValueParams(attributeToValueMap));
				params.put(DeltaParamIdentifier.CreateNodeNewId, new DeltaParam(DeltaParamIdentifier.CreateNodeNewId, ""
						+ newId));
				params.put(DeltaParamIdentifier.CreateNodeSchemaId, new DeltaParam(DeltaParamIdentifier.CreateNodeSchemaId,
						"" + request.getSchemaId()));
				params.put(DeltaParamIdentifier.CreateNodeObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.CreateNodeObjectDefinitionId, "" + request.getEntityId()));

				result = new DeltaHeader(DeltaType.NODE_CREATE, currentUser, params);
				break;
			}
			case INSERT_EDGE: {
				params.putAll(getAttributeToValueParams(attributeToValueMap));
				params.put(DeltaParamIdentifier.CreateEdgeNewId, new DeltaParam(DeltaParamIdentifier.CreateEdgeNewId, ""
						+ newId));
				params.put(DeltaParamIdentifier.CreateEdgeSchemaId, new DeltaParam(DeltaParamIdentifier.CreateEdgeSchemaId,
						"" + request.getSchemaId()));
				params.put(DeltaParamIdentifier.CreateEdgeObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.CreateEdgeObjectDefinitionId, "" + request.getEntityId()));

				result = new DeltaHeader(DeltaType.EDGE_CREATE, currentUser, params);
				break;
			}
			case UPDATE_NODE: {
				// TODO this map contains all the attributes, not only the ones that were changed. Investigate and
				// change to only contain the changed ones.
				params.putAll(getAttributeToValueParams(attributeToValueMap));
				params.put(DeltaParamIdentifier.UpdateNodeObjectId, new DeltaParam(DeltaParamIdentifier.UpdateNodeObjectId,
						"" + request.getObjectId()));
				params.put(DeltaParamIdentifier.UpdateNodeSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateNodeSchemaId,
						"" + request.getSchemaId()));
				params.put(DeltaParamIdentifier.UpdateNodeObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.UpdateNodeObjectDefinitionId, "" + request.getEntityId()));

				result = new DeltaHeader(DeltaType.NODE_UPDATE, currentUser, params);
				break;
			}
			case UPDATE_EDGE: {
				params.putAll(getAttributeToValueParams(attributeToValueMap));
				params.put(DeltaParamIdentifier.UpdateEdgeObjectId, new DeltaParam(DeltaParamIdentifier.UpdateEdgeObjectId,
						"" + request.getObjectId()));
				params.put(DeltaParamIdentifier.UpdateEdgeSchemaId, new DeltaParam(DeltaParamIdentifier.UpdateEdgeSchemaId,
						"" + request.getSchemaId()));
				params.put(DeltaParamIdentifier.UpdateEdgeObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.UpdateEdgeObjectDefinitionId, "" + request.getEntityId()));

				result = new DeltaHeader(DeltaType.EDGE_UPDATE, currentUser, params);
				break;
			}
			case MERGE_NODE: {
				params.put(DeltaParamIdentifier.MergeNodeSchemaId, new DeltaParam(DeltaParamIdentifier.MergeNodeSchemaId, ""
						+ request.getSchemaId()));
				params.put(DeltaParamIdentifier.MergeNodeObjectDefinitionId, new DeltaParam(
						DeltaParamIdentifier.MergeNodeObjectDefinitionId, "" + request.getEntityId()));
				params.put(DeltaParamIdentifier.MergeNodeFromId, new DeltaParam(DeltaParamIdentifier.MergeNodeFromId, ""
						+ request.getFromId()));
				params.put(DeltaParamIdentifier.MergeNodeToId, new DeltaParam(DeltaParamIdentifier.MergeNodeToId, ""
						+ request.getToId()));
				final String attributeIds = Utility.listToString(request.getAttributeIdsList());
				params.put(DeltaParamIdentifier.MergeNodeAttributeIDs, new DeltaParam(
						DeltaParamIdentifier.MergeNodeAttributeIDs, attributeIds));
				final String edgeIds = Utility.listToString(request.getEdgeIdsList());
				params.put(DeltaParamIdentifier.MergeNodeEdgeIDs, new DeltaParam(DeltaParamIdentifier.MergeNodeEdgeIDs,
						edgeIds));

				result = new DeltaHeader(DeltaType.NODE_MERGE, currentUser, params);
				break;
			}
		}
		return result;
	}

	private Map<DeltaParamIdentifier, DeltaParam> getAttributeToValueParams(final Map<Attribute, String> attributeToValueMap){
		Map<DeltaParamIdentifier, DeltaParam> res = new HashMap<DeltaParamIdentifier, DeltaParam>();
		for (Attribute a : attributeToValueMap.keySet()){
			int attributeId = a.getId();
			DeltaParamIdentifier deltaParamIdentifier = new DeltaParamIdentifier("" + attributeId);
			String value = attributeToValueMap.get(a);
			if (value == null)
				continue;
			res.put(deltaParamIdentifier, new DeltaParam(deltaParamIdentifier, value));
		}
		return res;
	}

	private DeltaHeader getTransactionDeltaForRequestForUpdateNodeCoords(){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		log.debug("making delta for coords update");
		int userId = storage.getCurrentUser().getId();
		int nodeId = request.getNodeId();
		double lon = request.getGeoCoords().getLon();
		double lat = request.getGeoCoords().getLat();

		log.debug("UserId: " + userId);
		log.debug("NodeId: " + nodeId);
		log.debug("Lon: " + lon);
		log.debug("Lat: " + lat);

		User creator = userDAO.get(userId);
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateNodeCoordsObjectId, new DeltaParam(
				DeltaParamIdentifier.UpdateNodeCoordsObjectId, "" + nodeId));
		params.put(DeltaParamIdentifier.UpdateNodeCoordsLon, new DeltaParam(DeltaParamIdentifier.UpdateNodeCoordsLon, ""
				+ lon));
		params.put(DeltaParamIdentifier.UpdateNodeCoordsLat, new DeltaParam(DeltaParamIdentifier.UpdateNodeCoordsLat, ""
				+ lat));
		return new DeltaHeader(DeltaType.NODE_UPDATE_COORDS, creator, params);
	}

	private DeltaHeader getTransactionDeltaForMetaphorUpdate(){
		UserDAO userDAO = NSpringFactory.getInstance().getUserDao();
		ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		log.debug("making delta for metaphor update");
		int userId = storage.getCurrentUser().getId();
		String iconName = request.getIconName();
		int nodeId = request.getNodeId();

		log.debug("UserId: " + userId);
		log.debug("IconName: " + iconName);
		log.debug("NodeId: " + nodeId);

		User creator = userDAO.get(userId);
		Map<DeltaParamIdentifier, DeltaParam> params = new HashMap<DeltaParamIdentifier, DeltaParam>();
		params.put(DeltaParamIdentifier.UpdateNodeMetaphorObjectId, new DeltaParam(
				DeltaParamIdentifier.UpdateNodeMetaphorObjectId, "" + nodeId));
		params.put(DeltaParamIdentifier.UpdateNodeMetaphorNewMetaphor, new DeltaParam(
				DeltaParamIdentifier.UpdateNodeMetaphorNewMetaphor, iconName));
		return new DeltaHeader(DeltaType.NODE_UPDATE_METAPHOR, creator, params);
	}

	@Override
	public void init(final ServletConfig config) throws ServletException{
		super.init(config);
		FormulaExecutor.init();
	}

	@Override
	protected UserActivityType getActivityType(){
		UserActivityType activity = null;
		switch (request.getAction()){
			case INSERT_NODE:
				activity = UserActivityType.CreateNode;
				break;
			case INSERT_EDGE:
				activity = UserActivityType.CreateEdge;
				break;
			case UPDATE_NODE:
				activity = UserActivityType.UpdateNode;
				break;
			case UPDATE_EDGE:
				activity = UserActivityType.UpdateEdge;
				break;
			case DELETE:
				final ObjectDefinition entity = objService.getEntityById(request.getEntityId());
				if (entity != null){
					activity = entity.isNode() ? UserActivityType.DeleteNode : UserActivityType.DeleteEdge;
				}
				break;
			case MERGE_NODE:
				activity = UserActivityType.MergeNode;
				break;
			case UPDATE_NODE_METAPHOR:
				activity = UserActivityType.UpdateNodeMetaphor;
				break;
		}
		return activity;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		List<LogParam> params = new ArrayList<LogParam>();
		switch (request.getAction()){
			case INSERT_NODE:
			case INSERT_EDGE:
				params.add(new LogParam(ID_LOG_PARAM, newId));
				break;
			case UPDATE_NODE:
			case UPDATE_EDGE:
			case DELETE:
				params.add(new LogParam(ID_LOG_PARAM, request.getObjectId()));
				break;
			case MERGE_NODE:
				params.add(new LogParam(FROMID_LOG_PARAM, request.getFromId()));
				params.add(new LogParam(TOID_LOG_PARAM, request.getToId()));
				break;
			case UPDATE_NODE_METAPHOR:
				params.add(new LogParam(ID_LOG_PARAM, request.getNodeId()));
				params.add(new LogParam(ICONNAME_LOG_PARAM, request.getIconName()));
		}
		return params;
	}
}
