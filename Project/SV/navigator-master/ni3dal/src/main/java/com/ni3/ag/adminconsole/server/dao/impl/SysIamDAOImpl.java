/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.impl;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.SysIam;
import com.ni3.ag.adminconsole.server.dao.SysIamDAO;
import org.apache.log4j.Logger;

public class SysIamDAOImpl extends HibernateDaoSupport implements SysIamDAO{

	@Override
	public SysIam get(){
		try{
			return (SysIam) getHibernateTemplate().load(SysIam.class, 1);
		} catch (Throwable ex){
			Logger.getLogger(this.getClass()).warn("No sys_iam record found - database should be just generated");
			return null;
		}
	}

	@Override
	public void set(SysIam s){
		getHibernateTemplate().saveOrUpdate(s);
	}

}
