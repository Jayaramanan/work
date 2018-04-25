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

import com.ni3.ag.adminconsole.domain.Edge;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.ObjectStatus;
import com.ni3.ag.adminconsole.server.dao.EdgeDAO;

public class EdgeDAOImpl extends HibernateDaoSupport implements EdgeDAO{
	private final static String CIS_EDGES_TABLE = "cis_edges";

	@Override
	public Edge getEdge(Integer edgeId){
		return (Edge) getHibernateTemplate().get(Edge.class, edgeId);
	}

	@Override
	public void deleteReferencedCisEdges(final ObjectDefinition object){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "delete Edge where edgeType = :object";
				Query query = (Query) session.createQuery(sql);
				query.setEntity("object", object);

				return query.executeUpdate();
			}
		};
		getHibernateTemplate().execute(callback);
	}

	// used by API
	@Override
	public Integer getNewEdgeId(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				String sql = "select sp_getseq('seq_ObjectCount)')";
				SQLQuery query = (SQLQuery) session.createSQLQuery(sql);
				return query.uniqueResult();
			}
		};
		BigInteger edgeId = (BigInteger) getHibernateTemplate().execute(callback);
		return edgeId != null ? edgeId.intValue() : null;
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

	// used by API
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> getEdgeUserData(final List<Edge> edges, final List<ObjectAttribute> attributes,
	        final ObjectDefinition od){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				SQLQuery query = (SQLQuery) session.createSQLQuery(getEdgeSql(edges, attributes, od));
				List<Object[]> data = (List<Object[]>) query.list();
				return data;
			}
		};
		return (List<Object[]>) getHibernateTemplate().execute(callback);
	}

	private String getEdgeSql(List<Edge> edges, List<ObjectAttribute> attributes, ObjectDefinition od){
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		int paIndex = 0;
		for (int i = 0; i < attributes.size(); i++){
			if (i > 0)
				sql.append(", ");
			ObjectAttribute attr = attributes.get(i);
			if (attr.isPredefined()){
				sql.append("pa").append(paIndex++).append(".label as ").append(attr.getName());
			} else{
				if (attr.getInTable().equalsIgnoreCase(CIS_EDGES_TABLE)){
					sql.append("ed.").append(attr.getName());
				} else{
					sql.append("ut.").append(attr.getName());
				}
			}
		}
		sql.append(" from ").append(CIS_EDGES_TABLE).append(" ed");
		sql.append(" inner join ").append(od.getTableName()).append(" ut on (ut.id = ed.id)");
		sql.append(" inner join cis_objects obj on (obj.id = ut.id and obj.status in (");
		sql.append(ObjectStatus.Normal.toInt()).append(",").append(ObjectStatus.Locked.toInt()).append("))");
		paIndex = 0;
		for (ObjectAttribute attr : attributes){
			if (!attr.isPredefined()){
				continue;
			}
			sql.append(" left join cht_predefinedattributes pa").append(paIndex);
			sql.append(" on (pa").append(paIndex).append(".id = ");
			if (attr.getInTable().equalsIgnoreCase(CIS_EDGES_TABLE)){
				sql.append("ed.");
			} else{
				sql.append("ut.");
			}
			sql.append(attr.getName()).append(")");
			paIndex++;
		}

		sql.append(" where ed.id in (");
		for (int i = 0; i < edges.size(); i++){
			if (i > 0){
				sql.append(", ");
			}
			Edge edge = edges.get(i);
			sql.append(edge.getId());
		}
		sql.append(")");
		return sql.toString();
	}
}
