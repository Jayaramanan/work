/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Node;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.NodeDAO;

public class NodeDAOMock implements NodeDAO{

	@Override
	public int executeUpdate(String query){
		return 0;
	}

	@Override
	public Node getNode(Integer nodeId){
		return null;
	}

	@Override
	public Integer getNewNodeId(){
		return 0;
	}

	@Override
	public Object[] getData(String sql, Object[] params){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getRowCount(String usrTable){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUniqueResult(String sql, Object[] params){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteReferencedCisNodes(ObjectDefinition object){
		// TODO Auto-generated method stub

	}

	@Override
	public List<Object[]> getIDsForUserTable(String tableName){
		// TODO Auto-generated method stub
		return null;
	}

}
