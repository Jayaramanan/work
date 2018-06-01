/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.Context;
import com.ni3.ag.adminconsole.domain.ContextAttribute;
import com.ni3.ag.adminconsole.domain.ObjectAttribute;
import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.SchemaAdminModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class CheckObjectContextRule implements ACValidationRule{
	private final static String FAVORITE = "Favorites";

	@Override
	public boolean performCheck(AbstractModel amodel){
		SchemaAdminModel model = (SchemaAdminModel) amodel;
		ObjectDefinition object = model.getCurrentObjectDefinition();
		if (object.isContextEdge()){
			resetInContextAttributes(object.getObjectAttributes());
		} else{
			generateContextAndAttributes(object, model);
		}
		removeContextAttributes(object);
		return true;
	}

	private void resetInContextAttributes(List<ObjectAttribute> objectAttributes){
		for (ObjectAttribute oa : objectAttributes){
			if (oa.isInContext()){
				oa.setInContext(false);
			}
		}
	}

	private void removeContextAttributes(ObjectDefinition object){
		if (object.getObjectAttributes() == null || object.getObjectAttributes().isEmpty())
			return;
		if (object.getContext() == null)
			return;
		for (ObjectAttribute oa : object.getObjectAttributes())
			if (!oa.isInContext())
				removeContextAttribute(object, oa);
	}

	private void removeContextAttribute(ObjectDefinition object, ObjectAttribute oa){
		Context c = object.getContext();
		for (ContextAttribute ca : c.getContextAttributes()){
			if (ca.getAttribute().equals(oa)){
				c.getContextAttributes().remove(ca);
				return;
			}
		}
	}

	private void generateContextAndAttributes(ObjectDefinition object, SchemaAdminModel model){
		if (object.getObjectAttributes() == null || object.getObjectAttributes().isEmpty())
			return;
		List<ObjectAttribute> attrs = object.getObjectAttributes();
		boolean foundAnyInContext = false;
		for (ObjectAttribute oa : attrs){
			if (oa.isInContext()){
				foundAnyInContext = true;
				break;
			}
		}
		if (!foundAnyInContext){
			Context c = object.getContext();
			if (c != null)
				model.addContextToDelete(c);
			object.setContext(null);
			return;
		}
		Context c = object.getContext();
		if (c == null){
			c = new Context();
			c.setObjectDefinition(object);
			c.setName(FAVORITE);
			c.setPkAttribute(findFavoritesIdAttribute(object));
			c.setTableName(object.getTableName() + ObjectAttribute.CONTEXT_TABLE_SUFFIX);
			object.setContext(c);
		}
		if (c.getContextAttributes() == null)
			c.setContextAttributes(new ArrayList<ContextAttribute>());

		for (ObjectAttribute oa : attrs){
			if (!oa.isInContext())
				continue;
			if (containsAttribute(c, oa))
				continue;
			ContextAttribute ca = new ContextAttribute();
			ca.setAttribute(oa);
			ca.setContext(c);
			c.getContextAttributes().add(ca);
		}
	}

	private ObjectAttribute findFavoritesIdAttribute(ObjectDefinition object){
		if (object.getObjectAttributes() == null)
			return null;

		for (ObjectAttribute oa : object.getObjectAttributes()){
			if (oa.getName().equals(ObjectAttribute.FAVORITE_ID_ATTRIBUTE_NAME))
				return oa;
		}
		return null;
	}

	private boolean containsAttribute(Context c, ObjectAttribute oa){
		for (ContextAttribute ca : c.getContextAttributes()){
			if (ca.getAttribute().equals(oa))
				return true;
		}
		return false;
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return null;
	}

}
