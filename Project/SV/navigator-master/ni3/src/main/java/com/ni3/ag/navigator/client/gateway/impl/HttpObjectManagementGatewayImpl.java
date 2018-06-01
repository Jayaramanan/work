package com.ni3.ag.navigator.client.gateway.impl;

import java.io.IOException;
import java.util.List;

import com.google.protobuf.ByteString;
import com.ni3.ag.navigator.client.domain.*;
import com.ni3.ag.navigator.client.gateway.ObjectManagementGateway;
import com.ni3.ag.navigator.shared.constants.ServletName;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NRequest.ObjectManagement.Builder;
import com.ni3.ag.navigator.shared.proto.NResponse;

public class HttpObjectManagementGatewayImpl extends AbstractGatewayImpl implements ObjectManagementGateway{
	@Override
	public void updateNodeMetaphor(int nodeId, String iconName){
		final Builder builder = NRequest.ObjectManagement.newBuilder();
		builder.setAction(NRequest.ObjectManagement.Action.UPDATE_NODE_METAPHOR);
		builder.setNodeId(nodeId);
		if (iconName != null){
			builder.setIconName(iconName);
		}
		NRequest.ObjectManagement request = builder.build();
		try{
			sendRequest(ServletName.ObjectManagementServlet, request);
		} catch (IOException e){
			showErrorAndThrow("Error update node metaphor", e);
		}
	}

	@Override
	public void updateNodeCoords(int id, double lon, double lat){
		NRequest.ObjectManagement request = NRequest.ObjectManagement.newBuilder().setAction(
				NRequest.ObjectManagement.Action.UPDATE_NODE_GEO_COORDS).setNodeId(id).setGeoCoords(
				NRequest.GeoCoords.newBuilder().setLon(lon).setLat(lat)).build();
		try{
			sendRequest(ServletName.ObjectManagementServlet, request);
		} catch (IOException e){
			showErrorAndThrow("Error update node coordinates", e);
		}
	}

	@Override
	public void delete(DBObject obj){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.DELETE);
		request.setObjectId(obj.getId());
		request.setEntityId(obj.getEntity().ID);
		request.setSchemaId(obj.getEntity().getSchema().ID);
		try{
			sendRequest(ServletName.ObjectManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error delete object " + obj.getId(), ex);
		}
	}

	@Override
	public void insertNode(DBObject node){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.INSERT_NODE);
		request.setEntityId(node.getEntity().ID);
		request.setSchemaId(node.getEntity().getSchema().ID);
		fillObjectValues(node, false, request);
		try{
			ByteString payload = sendRequest(ServletName.ObjectManagementServlet, request.build());
			NResponse.ObjectManagement result = NResponse.ObjectManagement.parseFrom(payload);
			int id = result.getId();
			node.setId(id);
		} catch (IOException ex){
			showErrorAndThrow("Error insert new node", ex);
		}
	}

	@Override
	public void insertEdge(DBObject edge, final int favoritesID, final int fromID, final int toID){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.INSERT_EDGE);
		request.setFavoriteId(favoritesID);

		request.setFromId(fromID);
		request.setToId(toID);
		edge.setValueByAttributeName("fromID", fromID);
		edge.setValueByAttributeName("toID", toID);

		request.setEntityId(edge.getEntity().ID);
		request.setSchemaId(edge.getEntity().getSchema().ID);
		fillObjectValues(edge, false, request);
		try{
			ByteString payload = sendRequest(ServletName.ObjectManagementServlet, request.build());
			NResponse.ObjectManagement result = NResponse.ObjectManagement.parseFrom(payload);
			int id = result.getId();
			edge.setId(id);
		} catch (IOException ex){
			showErrorAndThrow("Error insert new edge", ex);
		}
	}

	@Override
	public void updateNode(DBObject node, final boolean locked){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.UPDATE_NODE);
		request.setObjectId(node.getId());
		request.setEntityId(node.getEntity().ID);
		request.setSchemaId(node.getEntity().getSchema().ID);
		fillObjectValues(node, locked, request);
		try{
			ByteString payload = sendRequest(ServletName.ObjectManagementServlet, request.build());
			NResponse.ObjectManagement result = NResponse.ObjectManagement.parseFrom(payload);
			int id = result.getId();
			node.setId(id);
		} catch (IOException ex){
			showErrorAndThrow("Error update node " + node.getId(), ex);
		}
	}

	@Override
	public void updateEdge(DBObject edge, final int favoritesID, final boolean locked){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.UPDATE_EDGE);
		request.setObjectId(edge.getId());
		request.setFavoriteId(favoritesID);
		request.setEntityId(edge.getEntity().ID);
		request.setSchemaId(edge.getEntity().getSchema().ID);
		fillObjectValues(edge, locked, request);
		try{
			ByteString payload = sendRequest(ServletName.ObjectManagementServlet, request.build());
			NResponse.ObjectManagement result = NResponse.ObjectManagement.parseFrom(payload);
			int id = result.getId();
			edge.setId(id);
		} catch (IOException ex){
			showErrorAndThrow("Error update edge " + edge.getId(), ex);
		}
	}

	public void merge(DBObject toNode, DBObject fromNode, List<Integer> attributes, List<Integer> connections){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.MERGE_NODE);
		request.setFromId(fromNode.getId());
		request.setToId(toNode.getId());
		request.setEntityId(toNode.getEntity().ID);
		request.setSchemaId(toNode.getEntity().getSchema().ID);
		request.addAllAttributeIds(attributes);
		request.addAllEdgeIds(connections);
		try{
			sendRequest(ServletName.ObjectManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error merge node", ex);
		}
	}

	@Override
	public boolean checkUserObjectPermissions(final int nodeId, int schemaId){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.CHECK_CAN_DELETE_NODE);
		request.setObjectId(nodeId);
		request.setSchemaId(schemaId);
		try{
			ByteString payload = sendRequest(ServletName.ObjectManagementServlet, request.build());
			NResponse.ObjectDeleteAccessResult result = NResponse.ObjectDeleteAccessResult.parseFrom(payload);
			return result.getResult();
		} catch (IOException ex){
			showErrorAndThrow("Error get object permissions " + nodeId, ex);
			return false;
		}
	}

	@Override
	public void setContext(DBObject obj, final Context c, final int favoriteId, final boolean locked){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.SET_CONTEXT);
		request.setObjectId(obj.getId());
		request.setContextId(c.ID);
		request.setFavoriteId(favoriteId);
		request.setEntityId(obj.getEntity().ID);
		request.setSchemaId(obj.getEntity().getSchema().ID);
		fillObjectValues(obj, locked, request);
		try{
			sendRequest(ServletName.ObjectManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error clear context ", ex);
		}
	}

	@Override
	public void clearContext(Favorite favorite){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.CLEAR_CONTEXT);
		request.setFavoriteId(favorite.getId());
		request.setSchemaId(favorite.getSchemaId());
		try{
			sendRequest(ServletName.ObjectManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error clear context ", ex);
		}
	}

	@Override
	public void cloneContext(int schemaId, int contextId, int oldFavoriteId, int newFavoriteId){
		NRequest.ObjectManagement.Builder request = NRequest.ObjectManagement.newBuilder();
		request.setAction(NRequest.ObjectManagement.Action.CLONE_CONTEXT);
		request.setContextId(contextId);
		request.setOldFavoriteId(oldFavoriteId);
		request.setFavoriteId(newFavoriteId);
		request.setSchemaId(schemaId);
		try{
			sendRequest(ServletName.ObjectManagementServlet, request.build());
		} catch (IOException ex){
			showErrorAndThrow("Error clone context ", ex);
		}
	}

	private void fillObjectValues(DBObject obj, boolean locked, NRequest.ObjectManagement.Builder request){
		for (final Attribute a : obj.getEntity().getAllAttributes()){
			final Object val = obj.getValue(a.ID);
			if (!a.isDynamic() && (val != null || a.isDisplayableOnEdit(locked))){
				int aId = a.ID;
				String value = "";
				if (val != null){
					if (a.predefined){
						if (a.multivalue){
							value = getTransferMultivalue((Value[]) val);
						} else{
							value = "" + ((Value) val).getId();
						}
					} else{
						if (a.multivalue){
							value = getTransferMultivalue(a, (Object[]) val);
						} else{
							value = a.getDataType().getTransferString(val);
						}
					}
				}
				request.addAttributeIds(aId);
				request.addValues(value);
			}
		}
	}

	private String getTransferMultivalue(final Attribute attr, final Object[] val){
		final StringBuilder ret = new StringBuilder();
		for (final Object o : val){
			ret.append("{").append(attr.getDataType().getTransferString(o)).append("}");
		}
		return ret.toString();
	}

	private String getTransferMultivalue(final Value[] val){
		final StringBuilder ret = new StringBuilder();
		for (final Value v : val){
			ret.append("{").append(v.getId()).append("}");
		}
		return ret.toString();
	}
}
