package com.ni3.ag.navigator.server.graphXXL;

import java.util.HashSet;
import java.util.Set;

import com.ni3.ag.navigator.server.type.Scope;

public class GroupScope{
	private boolean hasNodeScope;
	private boolean hasEdgeScope;

	private Set<Integer> allowedEntities;
	private Set<Integer> allowedNodes;
	private Set<Integer> allowedEdges;
	private Set<Integer> deniedNodes;
	private Set<Integer> deniedEdges;

	public GroupScope(){
		hasNodeScope = false;
		hasEdgeScope = false;
		allowedEntities = new HashSet<Integer>();
		allowedNodes = new HashSet<Integer>();
		allowedEdges = new HashSet<Integer>();
		deniedNodes = new HashSet<Integer>();
		deniedEdges = new HashSet<Integer>();
	}

	public boolean hasNodeScope(){
		return hasNodeScope;
	}

	boolean hasGroupScope(final String flag){
		if (flag == null || flag.isEmpty())
			return false;
		switch (flag.charAt(0)){
			case 'S':
				return true;
			case 'A':
			default:
				return false;

		}

	}

	public void setHasNodeScope(String hasNodeScope){
		this.hasNodeScope = hasGroupScope(hasNodeScope);
	}

	public boolean hasEdgeScope(){
		return hasEdgeScope;
	}

	public void setHasEdgeScope(String hasEdgeScope){
		this.hasEdgeScope = hasGroupScope(hasEdgeScope);
	}

	public Set<Integer> getAllowedEntities(){
		return allowedEntities;
	}

	public void setAllowedEntities(Set<Integer> allowedEntities){
		this.allowedEntities = allowedEntities;
	}

	public Set<Integer> getAllowedNodes(){
		return allowedNodes;
	}

	public void setAllowedNodes(Set<Integer> allowedNodes){
		this.allowedNodes = allowedNodes;
	}

	public Set<Integer> getAllowedEdges(){
		return allowedEdges;
	}

	public void setAllowedEdges(Set<Integer> allowedEdges){
		this.allowedEdges = allowedEdges;
	}

	public Set<Integer> getDeniedNodes(){
		return deniedNodes;
	}

	public Set<Integer> getDeniedEdges(){
		return deniedEdges;
	}

	public Scope getNodeScope(int nodeId, int entityId){
		Scope scope = Scope.Allow;
		if (!allowedEntities.contains(entityId)){
			scope = Scope.Denied;
		} else if (hasNodeScope && !allowedNodes.contains(nodeId) || deniedNodes.contains(nodeId)){
			scope = Scope.Denied;
		}

		return scope;
	}

	public Scope getEdgeScope(int edgeId, int entityId){
		Scope scope = Scope.Allow;
		if (!allowedEntities.contains(entityId)){
			scope = Scope.Denied;
		} else if (hasEdgeScope && !allowedEdges.contains(edgeId) || deniedEdges.contains(edgeId)){
			scope = Scope.Denied;
		}
		return scope;
	}

	public void clearNode(int nodeId){
		allowedNodes.remove(nodeId);
		deniedNodes.remove(nodeId);
	}

	public void clearEdge(int edgeId){
		allowedEdges.remove(edgeId);
		deniedEdges.remove(edgeId);
	}
}
