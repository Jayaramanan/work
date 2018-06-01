package com.ni3.ag.navigator.server.dao;

import java.util.List;

import com.ni3.ag.navigator.server.domain.ObjectConnection;

public interface ObjectConnectionDAO{
	List<ObjectConnection> getObjectConnections(int schema);

	List<ObjectConnection> getConnectionForToType(int toEntityId);

	List<ObjectConnection> getConnectionForFromType(int fromEntityId);

	ObjectConnection getConnectionForFromToTypes(int fromType, int toType, int edgeType);
}
