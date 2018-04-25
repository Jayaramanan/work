/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;

public interface IconDAO{
	public List<Icon> loadAll();

	public Integer save(Icon icon);

	public void saveAll(List<Icon> icons);

	public void delete(Icon icon);

	public Icon getIconByName(String iconAttr);

	public void deleteAll(List<Icon> iconsToDelete);
}
