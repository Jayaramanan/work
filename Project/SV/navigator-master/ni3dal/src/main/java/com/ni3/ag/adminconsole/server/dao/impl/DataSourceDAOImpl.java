package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import com.ni3.ag.adminconsole.domain.DataSource;
import com.ni3.ag.adminconsole.server.dao.DataSourceDAO;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DataSourceDAOImpl extends HibernateDaoSupport implements DataSourceDAO{

	@Override
	@SuppressWarnings("unchecked")
	public List<DataSource> getDataSources(){
		return getHibernateTemplate().loadAll(DataSource.class);
	}
}
