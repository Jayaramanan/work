/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.domain.Metaphor;

public interface NodeMetaphorDAO{

	List<Metaphor> getMetaphorsByIcons(final List<Icon> icons);

}