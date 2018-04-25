package com.ni3.ag.navigator.server.services;

import java.util.List;

public interface GroupScopeProvider{
	List<Integer> getEdgeScope(int groupId);

	List<Integer> getNodeScope(int groupId);
}
