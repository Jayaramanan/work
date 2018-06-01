package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.navigator.server.dao.ObjectConnectionDAO;
import com.ni3.ag.navigator.server.domain.ObjectConnection;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ObjectConnectionDAOImpl extends HibernateDaoSupport implements ObjectConnectionDAO{
	@Override
	@SuppressWarnings("unchecked")
	public List<ObjectConnection> getObjectConnections(int schema){
		List<ObjectConnection> objectConnections = getHibernateTemplate().loadAll(ObjectConnection.class);
		List<ObjectConnection> result = new ArrayList<ObjectConnection>();
		for(ObjectConnection oc : objectConnections){
			if(oc.getConnectionObject().getSchema().getId() == schema)
				result.add(oc);
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ObjectConnection> getConnectionForToType(final int toEntityId){
		return (List<ObjectConnection>) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.eq("toObject.id", toEntityId));
				return criteria.list();
			}
		});
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ObjectConnection> getConnectionForFromType(final int fromEntityId){
		List<ObjectConnection> connections = (List<ObjectConnection>) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.eq("fromObject.id", fromEntityId));
				return criteria.list();
			}
		});
		for(ObjectConnection oc : connections){
			Hibernate.initialize(oc.getFromObject());
			Hibernate.initialize(oc.getFromObject().getSchema());
			Hibernate.initialize(oc.getToObject());
			Hibernate.initialize(oc.getToObject().getSchema());
			Hibernate.initialize(oc.getConnectionObject());
			Hibernate.initialize(oc.getConnectionObject().getSchema());
		}
		return connections;
	}

	@Override
	@SuppressWarnings("unchecked")
	public ObjectConnection getConnectionForFromToTypes(final int fromType, final int toType, final int edgeType){
		return (ObjectConnection) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(ObjectConnection.class);
				criteria.add(Restrictions.eq("fromObject.id", fromType)).add(Restrictions.eq("toObject.id", toType))
						.add(Restrictions.eq("connectionObject.id", edgeType));
				return criteria.uniqueResult();
			}
		});
	}
}
