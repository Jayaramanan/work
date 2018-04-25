/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.Context;
import com.ni3.ag.adminconsole.server.dao.ContextDAO;

public class ContextDAOImpl extends HibernateDaoSupport implements ContextDAO{

	@Override
	public void deleteAll(List<Context> contextsToDelete){
		getHibernateTemplate().deleteAll(contextsToDelete);
	}

}
