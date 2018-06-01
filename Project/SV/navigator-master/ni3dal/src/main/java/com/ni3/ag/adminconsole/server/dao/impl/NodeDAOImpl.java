/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Node;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.server.dao.NodeDAO;

public class NodeDAOImpl extends HibernateDaoSupport implements NodeDAO{

	@Override
	public Node getNode(Integer nodeId){
		return (Node) getHibernateTemplate().get(Node.class, nodeId);
	}

	@Override
	public int executeUpdate(final String updateSql){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(updateSql);
				return query.executeUpdate();
			}
		};
		return (Integer) getHibernateTemplate().execute(callback);
	}

	@Override
	public Object getData(final String sql, final Object[] params){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				for (int i = 0; i < params.length; i++){
					query.setParameter(i, params[i]);
				}
				return query.list();
			}
		};
		return getHibernateTemplate().execute(callback);
	}

	@Override
	public Object getUniqueResult(final String sql, final Object[] params){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				for (int i = 0; i < params.length; i++){
					query.setParameter(i, params[i]);
				}
				return query.uniqueResult();
			}
		};
		return getHibernateTemplate().execute(callback);
	}

	@Override
	public Integer getNewNodeId(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select sp_getseq('seq_ObjectCount)')";
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return query.uniqueResult();
			}
		};
		BigInteger nodeId = (BigInteger) getHibernateTemplate().execute(callback);
		return nodeId != null ? nodeId.intValue() : null;
	}

	@Override
	public Integer getRowCount(final String usrTable){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select count(id) from " + usrTable;
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return (BigInteger) query.uniqueResult();
			}
		};
		BigInteger bigInt = (BigInteger) getHibernateTemplate().execute(callback);
		return new Integer(bigInt.intValue());
	}

	@Override
	public void deleteReferencedCisNodes(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sqls[] = { "delete Edge e where e.fromId in (select id from Node where objectDefinition = :object)",
						"delete Edge e where e.toId in (select id from Node where objectDefinition = :object)",
						"delete Node where objectDefinition = :object" };
				for (String sql : sqls){
					Query query = (Query) session.createQuery(sql);
					query.setEntity("object", object);
					query.executeUpdate();
				}

				return null;
			}
		};
		getHibernateTemplate().execute(callback);
	}

	// used by API
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getIDsForUserTable(final String tableName){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select id, srcid from " + tableName;
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return query.list();
			}
		};
		return (List<Object[]>) getHibernateTemplate().execute(callback);
	}
}
