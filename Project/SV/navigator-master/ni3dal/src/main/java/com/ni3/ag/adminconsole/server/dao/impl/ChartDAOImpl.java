/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Chart;
import com.ni3.ag.adminconsole.domain.ChartType;
import com.ni3.ag.adminconsole.domain.ChartAttribute;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.server.dao.ChartDAO;

public class ChartDAOImpl extends HibernateDaoSupport implements ChartDAO{

	@Override
	public void saveOrUpdate(Chart ch){
		getHibernateTemplate().saveOrUpdate(ch);
	}

	@SuppressWarnings("unchecked")
	public List<ChartType> getAllChartTypes(){
		return getHibernateTemplate().loadAll(ChartType.class);
	}

	@Override
	public void delete(Chart chart){
		getHibernateTemplate().delete(chart);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Chart> getChartsBySchema(final Schema schema){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Chart.class);
				criteria.add(Restrictions.eq(Chart.SCHEMA, schema));
				return criteria.list();
			}
		};
		return (List<Chart>) getHibernateTemplate().execute(callback);
	}

	@Override
	public void saveOrUpdateAll(List<Chart> newCharts){
		getHibernateTemplate().saveOrUpdateAll(newCharts);
	}

	@Override
	public Chart getChart(int chartID){
		return (Chart) getHibernateTemplate().load(Chart.class, chartID);
	}

	@Override
	public void saveOrUpdateChartAttribute(ChartAttribute attr){
		getHibernateTemplate().saveOrUpdate(attr);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Chart> getAllCharts(){
		HibernateCallback callback = new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException{
				Criteria criteria = session.createCriteria(Chart.class);
				criteria.addOrder(Order.asc(Chart.SCHEMA));
				return criteria.list();
			}
		};
		return (List<Chart>) getHibernateTemplate().execute(callback);
	}

}
