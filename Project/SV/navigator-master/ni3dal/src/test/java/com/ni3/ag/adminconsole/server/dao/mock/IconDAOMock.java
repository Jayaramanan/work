/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.List;

import com.ni3.ag.adminconsole.domain.Icon;
import com.ni3.ag.adminconsole.server.dao.IconDAO;

public class IconDAOMock implements IconDAO{

	@Override
	public void delete(Icon icon){
		// TODO Auto-generated method stub

	}

	@Override
	public Icon getIconByName(String iconAttr){
		Icon icon = new Icon();
		icon.setIconName(iconAttr);
		return icon;
	}

	@Override
	public List<Icon> loadAll(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer save(Icon icon){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveAll(List<Icon> icons){
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(List<Icon> iconsToDelete){
	}
}
