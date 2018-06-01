/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.GroupPrefilter;
import com.ni3.ag.adminconsole.server.dao.GroupPrefilterDAO;

public class GroupPrefilterDAOImpl extends HibernateDaoSupport implements GroupPrefilterDAO{

	public void deleteAll(List<GroupPrefilter> groupsToDelete){
		getHibernateTemplate().deleteAll(groupsToDelete);
	}

	public void updateAll(List<GroupPrefilter> groupsToUpdate){
		getHibernateTemplate().saveOrUpdateAll(groupsToUpdate);
	}

}
