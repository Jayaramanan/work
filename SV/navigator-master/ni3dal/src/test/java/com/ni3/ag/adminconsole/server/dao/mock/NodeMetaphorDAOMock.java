/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;
import com.ni3.ag.adminconsole.server.dao.NodeMetaphorDAO;

public class NodeMetaphorDAOMock extends HibernateTemplate implements NodeMetaphorDAO{

	@Override
	public List<Metaphor> getMetaphorsByIcons(List<Icon> icons){
		return null;
	}

}
