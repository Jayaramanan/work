/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Favorites;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.FavoritesDAO;

public class PostgreFavoritesDAOImpl extends HibernateDaoSupport implements FavoritesDAO{

	@Override
	public int getMaxIdForRange(final int userRangeStart, final int userRangeEnd){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(Favorites.class);
				c.add(Restrictions.between(Favorites.ID, userRangeStart, userRangeEnd));
				c.setProjection(Projections.max(Favorites.ID));
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

	@Override
	public List<Favorites> getFavoritesWithoutCreator(final Schema schema){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(Favorites.class);
				c.add(Restrictions.isNull(Favorites.CREATOR));
				c.add(Restrictions.eq(Favorites.SCHEMA + ".id", schema.getId()));
				return c.list();
			}
		};
		@SuppressWarnings("unchecked")
		List<Favorites> list = (List<Favorites>) getHibernateTemplate().execute(callback);
		return list;
	}

	@Override
	public List<Favorites> getMinorOutdatedFavorites(final Schema schema, final String version){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(Favorites.class);
				c.add(Restrictions.eq(Favorites.SCHEMA + ".id", schema.getId()));
				c.add(Restrictions.sqlRestriction("substring(dbversion from '(%)#.%#.%$' for '#') = substring('" + version
				        + "' from '(%)#.%#.%$' for '#') and substring(dbversion from '%#.(%)#.%$' for '#') = substring('"
				        + version
				        + "' from '%#.(%)#.%$' for '#') and substring(dbversion from '%#.%#.(%)$' for '#') <> substring('"
				        + version + "' from '%#.%#.(%)$' for '#')"));
				return c.list();
			}
		};
		@SuppressWarnings("unchecked")
		List<Favorites> list = (List<Favorites>) getHibernateTemplate().execute(callback);
		return list;
	}

	@Override
	public List<Favorites> getMajorOutdatedFavorites(final Schema schema, final String version){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(Favorites.class);
				c.add(Restrictions.eq(Favorites.SCHEMA + ".id", schema.getId()));
				Criterion dbversionNull = Restrictions.or(Restrictions.isNull(Favorites.DB_VERSION),
				        Restrictions.eq(Favorites.DB_VERSION, ""));
				Criterion releaseOutdated = Restrictions
				        .sqlRestriction("substring(dbversion from '(%)#.%#.%$' for '#') <> substring('" + version
				                + "' from '(%)#.%#.%$' for '#')");
				Criterion majorOutdated = Restrictions
				        .sqlRestriction("substring(dbversion from '%#.(%)#.%$' for '#') <> substring('" + version
				                + "' from '%#.(%)#.%$' for '#')");
				c.add(Restrictions.or(dbversionNull, Restrictions.or(releaseOutdated, majorOutdated)));
				return c.list();
			}
		};
		@SuppressWarnings("unchecked")
		List<Favorites> list = (List<Favorites>) getHibernateTemplate().execute(callback);
		return list;
	}

	@Override
	public List<Favorites> getFavorites(final Schema schema, final Integer mode){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria c = session.createCriteria(Favorites.class);
				c.add(Restrictions.eq(Favorites.SCHEMA + ".id", schema.getId()));
				if (mode != null)
					c.add(Restrictions.eq(Favorites.MODE, mode));
				return c.list();
			}
		};
		@SuppressWarnings("unchecked")
		List<Favorites> list = (List<Favorites>) getHibernateTemplate().execute(callback);
		return list;
	}
}
