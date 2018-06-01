package com.ni3.ag.navigator.server.graphXXL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.dao.GroupDAO;
import com.ni3.ag.navigator.server.domain.*;
import com.ni3.ag.navigator.server.services.GroupScopeProvider;
import com.ni3.ag.navigator.server.type.Scope;

public class LazyGroupScope{
	private Map<Integer, GroupScope> groupScopes;
	private Schema schema;

	public LazyGroupScope(Schema schema){
		groupScopes = new HashMap<Integer, GroupScope>();
		this.schema = schema;
	}

	public boolean isVisible(Node n, int groupId){
		initScope(groupId);
		return groupScopes.get(groupId).getNodeScope(n.getID(), n.getType()) == Scope.Allow;
	}

	public boolean isVisible(Edge e, int groupId){
		initScope(groupId);
		return groupScopes.get(groupId).getEdgeScope(e.getID(), e.getType()) == Scope.Allow;
	}

	private void initScope(int groupId){
		if(groupScopes.containsKey(groupId))
			return;
		GroupScope gs = new GroupScope();
		groupScopes.put(groupId, gs);

		final GroupDAO groupDAO = NSpringFactory.getInstance().getGroupDao();
		final Group group = groupDAO.get(groupId);

		gs.setHasEdgeScope(group.getEdgeScope());
		gs.setHasNodeScope(group.getNodeScope());

		for(ObjectDefinition od : schema.getDefinitions()){
			ObjectDefinitionGroup odg = getGroupPermissions(od, groupId);
			if(odg.isCanRead())
				gs.getAllowedEntities().add(od.getId());
		}

		GroupScopeProvider groupScopeProvider = NSpringFactory.getInstance().getGroupScopeProvider();
		if (gs.hasEdgeScope()){
			List<Integer> scopedEdges = groupScopeProvider.getEdgeScope(groupId);
			if (scopedEdges != null && !scopedEdges.isEmpty()){
				gs.getAllowedEdges().addAll(scopedEdges);
			}
		}

		if(gs.hasNodeScope()){
			List<Integer> scopedNodes = groupScopeProvider.getNodeScope(groupId);
			if (scopedNodes != null && !scopedNodes.isEmpty()){
				gs.getAllowedEdges().addAll(scopedNodes);
			}
		}
	}

	private ObjectDefinitionGroup getGroupPermissions(ObjectDefinition od, int groupId){
		for(ObjectDefinitionGroup odg : od.getObjectPermissions()){
			if(odg.getGroupId() == groupId)
				return odg;
		}
		throw new RuntimeException("Cannot find ObjectDefinitionGroup object for group " + groupId + " in object " + od.getId());
	}
}
