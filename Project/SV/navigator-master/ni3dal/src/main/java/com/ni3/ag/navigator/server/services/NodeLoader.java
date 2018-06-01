package com.ni3.ag.navigator.server.services;

import com.ni3.ag.navigator.server.domain.ProxyNode;

public interface NodeLoader{
	void loadNode(int id, ProxyNode target);
}
