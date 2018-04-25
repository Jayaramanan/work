package com.ni3.ag.navigator.server.dao.impl.postgres;

import java.sql.SQLException;
import java.util.List;

import com.ni3.ag.navigator.server.dao.ContextDAO;
import com.ni3.ag.navigator.server.domain.Context;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ContextDAOImpl extends HibernateDaoSupport implements ContextDAO{

	@Override
	@SuppressWarnings("unchecked")
	public List<Context> findByName(final String name){
		return (List<Context>) getHibernateTemplate().execute(new HibernateCallback(){
			@Override
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Context.class).add(Restrictions.ilike("name", name));
				return criteria.list();
			}
		});
	}
}
