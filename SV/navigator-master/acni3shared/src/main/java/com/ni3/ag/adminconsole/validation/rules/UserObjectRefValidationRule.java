/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectDefinition;
import com.ni3.ag.adminconsole.domain.Schema;
import com.ni3.ag.adminconsole.domain.User;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.language.TextID;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.UserAdminModel;
import com.ni3.ag.adminconsole.shared.service.def.UserAdminService;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class UserObjectRefValidationRule implements ACValidationRule{
	private UserAdminService userAdminService;
	private List<ErrorEntry> errors;

	@Override
	public boolean performCheck(AbstractModel aModel){
		errors = new ArrayList<ErrorEntry>();
		UserAdminModel model = (UserAdminModel) aModel;
		User u = model.getUserToDelete();
		if (u.getId() == null)
			return true;
		List<ObjectDefinition> objects = userAdminService.getObjectsByUser(u);
		List<Schema> schemas = userAdminService.getSchemasByUser(u);
		if (!objects.isEmpty())
			errors.add(new ErrorEntry(TextID.MsgUserReferencedInObjects, new String[] { getObjectNameList(objects) }));
		if (!schemas.isEmpty())
			errors.add(new ErrorEntry(TextID.MsgUserReferencedInSchemas, new String[] { getSchemaNameList(schemas) }));
		return objects.isEmpty() && schemas.isEmpty();
	}

	private String getSchemaNameList(List<Schema> schemas){
		if (schemas.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < schemas.size(); i++){
			if (i != 0)
				sb.append(',');
			sb.append(schemas.get(i).getName());
		}
		return sb.toString();
	}

	private String getObjectNameList(List<ObjectDefinition> objects){
		if (objects.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < objects.size(); i++){
			if (i != 0)
				sb.append(',');
			sb.append(objects.get(i).getName());
		}
		return sb.toString();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}

	public void setUserAdminService(UserAdminService userAdminService){
		this.userAdminService = userAdminService;
	}

	public UserAdminService getUserAdminService(){
		return userAdminService;
	}

}
