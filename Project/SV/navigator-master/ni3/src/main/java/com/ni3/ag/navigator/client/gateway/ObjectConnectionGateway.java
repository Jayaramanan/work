package com.ni3.ag.navigator.client.gateway;

import java.util.List;

import com.ni3.ag.navigator.client.domain.ObjectConnection;

public interface ObjectConnectionGateway{
	List<ObjectConnection> getObjectConnections(int schema);
}
