/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.adminconsole.validation.rules;

import static com.ni3.ag.adminconsole.shared.language.TextID.MsgConnectionFieldsEmpty;

import java.util.ArrayList;
import java.util.List;

import com.ni3.ag.adminconsole.domain.ObjectConnection;
import com.ni3.ag.adminconsole.dto.ErrorEntry;
import com.ni3.ag.adminconsole.shared.model.AbstractModel;
import com.ni3.ag.adminconsole.shared.model.impl.ObjectConnectionModel;
import com.ni3.ag.adminconsole.validation.ACValidationRule;

public class ObjectConnectionValidationRule implements ACValidationRule{

	private List<ErrorEntry> errors;

	ObjectConnectionValidationRule(){
	}

	@Override
	public boolean performCheck(AbstractModel model){

		errors = new ArrayList<ErrorEntry>();

		ObjectConnectionModel ocModel = (ObjectConnectionModel) model;
		List<ObjectConnection> objectConnections = ocModel.getCurrentObject().getObjectConnections();
		for (ObjectConnection conn : objectConnections){
			if (conn.getConnectionType() == null || conn.getObject() == null || conn.getFromObject() == null
			        || conn.getToObject() == null || conn.getRgb() == null || conn.getLineStyle() == null
			        || conn.getLineWeight() == null){
				errors.add(new ErrorEntry(MsgConnectionFieldsEmpty));
				break;
			}
		}
		return errors.isEmpty();
	}

	@Override
	public List<ErrorEntry> getErrorEntries(){
		return errors;
	}
}
