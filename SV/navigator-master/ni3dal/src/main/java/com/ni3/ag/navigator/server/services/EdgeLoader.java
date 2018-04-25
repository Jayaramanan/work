package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.ProxyEdge;

public interface EdgeLoader{
	void loadEdge(int id, ProxyEdge proxyEdge);
}
