package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ThematicCluster;
import com.ni3.ag.adminconsole.server.dao.ThematicClusterDAO;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class ThematicClusterDAOImpl extends HibernateDaoSupport implements ThematicClusterDAO{
	@Override
	public int getMaxIdForRange(final int userRangeStart, final int userRangeEnd){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(ThematicCluster.class);
				c.add(Restrictions.between(ThematicCluster.ID, userRangeStart, userRangeEnd));
				c.setProjection(Projections.max(ThematicCluster.ID));
				return c.list();
			}
		};
		List<?> l = (List<?>) getHibernateTemplate().execute(callback);
		if (l == null || l.size() == 0)
			return userRangeStart;
		else if (l.get(0) == null)
			return userRangeStart;
		else
			return (Integer) l.get(0);
	}
}
