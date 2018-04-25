/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.server.dao.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.PredefinedAttribute;
import com.ni3.ag.adminconsole.server.dao.PredefinedAttributeDAO;

public class PredefinedAttributeDAOMock extends HibernateDaoSupport implements PredefinedAttributeDAO{

	private List<PredefinedAttribute> container = new ArrayList<PredefinedAttribute>();

	/**
	 * TODO: remove to use container
	 */
	public static final String[] allowedAttributeNames = new String[] { "col1", "Organizational" };

	/**
	 * TODO: rewrite to use container
	 */
	public List<PredefinedAttribute> getPredefinedAttributes(ObjectAttribute attribute){
		List<PredefinedAttribute> attrList = new ArrayList<PredefinedAttribute>();
		for (int i = 0; i < allowedAttributeNames.length; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setLabel(allowedAttributeNames[i]);
			pa.setValue(allowedAttributeNames[i]);
			attrList.add(pa);
		}

		return attrList;
	}

	/**
	 * TODO: rewrite to use container
	 */
	public List<PredefinedAttribute> getPredefinedAttributes(Integer attributeId){
		ArrayList<PredefinedAttribute> attrs = new ArrayList<PredefinedAttribute>();
		for (int i = 1; i <= 5; i++){
			PredefinedAttribute pa = new PredefinedAttribute();
			pa.setId(i);
			pa.setLabel("predef_attr" + i);
			ObjectAttribute attr = new ObjectAttribute(new ObjectDefinition());
			attr.setId(attributeId);
			pa.setObjectAttribute(attr);
			attrs.add(pa);
		}
		return attrs;
	}

	@Override
	public Collection<PredefinedAttribute> saveOrUpdateAll(Collection<PredefinedAttribute> attrs){
		container.removeAll(attrs); // remove old
		container.addAll(attrs);
		return container;
	}

	@Override
	public List<PredefinedAttribute> getPredefinedAttributes(){
		return container;
	}

	@Override
	public PredefinedAttribute saveOrUpdate(PredefinedAttribute attr){
		container.remove(attr);
		container.add(attr);
		return attr;
	}

	@Override
	public PredefinedAttribute getById(Integer id){
		for (PredefinedAttribute pa : container)
			if (pa.getId().equals(id))
				return pa;
		return null;
	}

	@Override
	public PredefinedAttribute getPredefinedAttributeByValue(ObjectAttribute attr, String value){
		for (PredefinedAttribute pa : container){
			if (pa.getValue().equals(value) && pa.getObjectAttribute().equals(attr))
				return pa;
		}
		return null;
	}

	@Override
	public PredefinedAttribute getPredefinedAttributeByLabel(ObjectAttribute objectAttribute, String label){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object merge(Object entity){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsedInUserTable(PredefinedAttribute pa){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateValuesInUserTable(PredefinedAttribute pa, Integer newValue){
		// TODO Auto-generated method stub

	}

}
