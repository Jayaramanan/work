/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Edge;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.EdgeDAO;

public class EdgeDAOMock implements EdgeDAO{

	@Override
	public Edge getEdge(Integer edgeId){
		return null;
	}

	@Override
	public void deleteReferencedCisEdges(ObjectDefinition object){
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object[]> getEdgeUserData(List<Edge> edges, List<ObjectAttribute> attributes, ObjectDefinition od){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object[]> getIDsForUserTable(String tableName){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getNewEdgeId(){
		// TODO Auto-generated method stub
		return null;
	}

}
